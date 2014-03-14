package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._
import scala.collection.mutable
import org.scalatest.{Ignore, BeforeAndAfterAll, Matchers, PropSpec}
import org.scalatest.prop.{Checkers, GeneratorDrivenPropertyChecks, TableDrivenPropertyChecks}
import scala.sys.process.Process
import org.scalacheck.Gen
import scala.math._
import scala.Array
import com.thesett.chisel.util.chiselTestHelper
import org.scalatest.prop.nsv.NoShrinkVariants

class IntDecoderTests extends PropSpec
with BeforeAndAfterAll with TableDrivenPropertyChecks with GeneratorDrivenPropertyChecks
with Matchers with Checkers with NoShrinkVariants
{
  def trace = false

  class DummyTests(c: IntDecoder) extends Tester(c, isTrace = trace)
  {
  }

  val (c, t) =
    chiselTestHelper(Array("--genHarness", "--compile", "--backend", "c", "--vcd", "--targetDir", "target", "--test"),
      () => Module(new IntDecoder()))
    { c => new DummyTests(c) }

  var testProcess: Process = _

  override def beforeAll()
  {
    testProcess = t.startTesting()
  }

  override def afterAll()
  {
    t.endTesting()
  }

  val zeros =
    Table(
      ("bcd0", "bcd1", "bcd2", "bcd3", "end", "next", "count", "rdy", "out"),
      (0, 0, 0, 0, true, false, 0, true, 0),
      (0, 0, 0, 0, true, false, 1, true, 0),
      (0, 0, 0, 0, true, false, 2, true, 0),
      (0, 0, 0, 0, true, false, 3, true, 0),
      (0, 0, 0, 0, true, false, 4, true, 0)
    )

  val digitSubSequence =
    Table(
      ("bcd0", "bcd1", "bcd2", "bcd3", "end", "next", "count", "rdy", "out"),
      (3, 5, 2, 7, true, false, 4, true, 3527),
      (3, 5, 2, 7, true, false, 3, true, 352),
      (3, 5, 2, 7, true, false, 2, true, 35),
      (3, 5, 2, 7, true, false, 1, true, 3),
      (3, 5, 2, 7, true, false, 0, true, 0)
    )

  val digit = Gen.choose(0, 9)
  val shortLengthGen = Gen.choose(0, 4)

  def scaleAndAdd(d: Seq[Int], length: Int): Int =
  {
    if (length == 0)
      0
    else
      (0 until length).map(i => d(i) * pow(10, length - i - 1).toInt).reduceRight(_ + _)
  }

  def checkInOut: (Int, Int, Int, Int, Boolean, Boolean, Int, Boolean, Int) => Unit =
  {
    (d0, d1, d2, d3, end, next, count, rdy, out) =>
    {
      t.poke(c.io.in.bcdAndFlags(0).bcd, d0)
      t.poke(c.io.in.bcdAndFlags(1).bcd, d1)
      t.poke(c.io.in.bcdAndFlags(2).bcd, d2)
      t.poke(c.io.in.bcdAndFlags(3).bcd, d3)
      t.poke(c.io.in.endMarker, if(end) 1 else 0)
      t.poke(c.io.in.nextAfterEnd, if(next) 1 else 0)
      t.poke(c.io.in.count, count)

      t.step(1)

      t.expect(c.io.rdy, if (rdy) 1 else 0)
      t.expect(c.io.out, out)
      t.expect(c.io.rdy, if (rdy) 1 else 0)
      t.expect(c.io.out, out)
    }
  }

  property("Any number of input zeros results in zero output.")
  {
    forAll(zeros)
    { checkInOut }
  }

  property("Sub-sequence, length 0 to 4, of a list of digits should convert only the length specified and correctly.")
  {
    forAll(digitSubSequence)
    { checkInOut }
  }

  property("Random combinations of up to 4 input digits, should convert to integers correctly.")
  {
    forAllNoShrink((digit, "bcd0"), (digit, "bcd1"), (digit, "bcd2"), (digit, "bcd3"), (shortLengthGen, "count"))
    {
      (d0: Int, d1: Int, d2: Int, d3: Int, length: Int) =>
      {checkInOut(d0, d1, d2, d3, true, false, length, true, scaleAndAdd(Array(d0, d1, d2, d3), length)) }
    }
  }

  def longInput[T](g: Gen[T]): Gen[List[T]] = Gen.sized
  {
    size => Gen.listOfN(size, g)
  }

  def toTuple(l: List[Int]): (Int, Int, Int, Int) =
    l match
    {
      case List(x1) => (x1, 0, 0, 0)
      case List(x1, x2) => (x1, x2, 0, 0)
      case List(x1, x2, x3) => (x1, x2, x3, 0)
      case List(x1, x2, x3, x4) => (x1, x2, x3, x4)
    }

  property("Random combinations of up to 8 input digits, chunked randomly, should convert to integers correctly.")
  {
    forAllNoShrink(longInput(digit), Gen.choose(1, 4), minSize(1), maxSize(8))
    {
      (ds: List[Int], split: Int) =>
      {
        val (l1, lt) = ds.splitAt(split)
        val (l2, l3) = lt.splitAt(4)

        val result = scaleAndAdd(ds, ds.length)

        val checkTuple = (ds: (Int, Int, Int, Int), length: Int, end: Boolean) =>
          checkInOut(ds._1, ds._2, ds._3, ds._4, end, false, length, end,
            if (end) result else 0)

        if (!l1.isEmpty)
          checkTuple(toTuple(l1), l1.length, l2.isEmpty)

        if (!l2.isEmpty)
          checkTuple(toTuple(l2), l2.length, l3.isEmpty)

        if (!l3.isEmpty)
          checkTuple(toTuple(l3), l3.length, true)
      }
    }
  }

  property("Random pairs of up to 4 input digits, delayed one cycle by next-after-end, " +
    "should convert to integers correctly.")
  {
    forAllNoShrink(longInput(digit), longInput(digit), minSize(1), maxSize(4))
    {
      (ds1: List[Int], ds2: List[Int]) =>
      {
        val result1 = scaleAndAdd(ds1, ds1.length)
        val result2 = scaleAndAdd(ds2, ds2.length)

        val checkTuple = (ds: (Int, Int, Int, Int), length: Int, end: Boolean, next: Boolean, result: Int) =>
          checkInOut(ds._1, ds._2, ds._3, ds._4, end, next, length, end || next, result)

        checkTuple(toTuple(ds1), ds1.length, false, false, 0)
        checkTuple(toTuple(ds2), ds2.length, true, true, result1)
        checkTuple((0, 0, 0, 0), 0, true, false, result2)
      }
    }
  }
}
