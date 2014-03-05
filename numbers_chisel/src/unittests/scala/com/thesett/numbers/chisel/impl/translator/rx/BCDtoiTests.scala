package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._
import scala.Array
import scala.collection.mutable
import org.scalatest.{BeforeAndAfterAll, PropSpec, Matchers}
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, TableDrivenPropertyChecks}
import org.scalacheck.Gen
import scala.sys.process.Process
import com.thesett.chisel.util.chiselTestHelper
import org.scalatest.prop.nsv.NoShrinkVariants

class BCDtoiTests extends PropSpec
with BeforeAndAfterAll with TableDrivenPropertyChecks with GeneratorDrivenPropertyChecks
with Matchers with NoShrinkVariants
{
  def trace = false

  class DummyTests(c: BCDtoi) extends Tester(c, Array(c.io))
  {
    defTests
    {
      true
    }
  }

  val (c, t) =
    chiselTestHelper(Array("--genHarness", "--compile", "--backend", "c", "--vcd", "--targetDir", "target", "--test"),
      () => Module(new BCDtoi()))
    {
      c => new DummyTests(c)
    }

  var testProcess: Process = _

  override def beforeAll()
  {
    testProcess = t.startTest
  }

  override def afterAll()
  {
    t.endTest(testProcess)
  }

  val examples =
    Table(
      ("bcd0", "bcd1", "bcd2", "bcd3", "out"),
      (0, 0, 0, 0, 0),
      (1, 0, 0, 0, 1),
      (0, 1, 0, 0, 10),
      (0, 0, 1, 0, 100),
      (0, 0, 0, 1, 1000),
      (9, 0, 0, 0, 9),
      (0, 9, 0, 0, 90),
      (0, 0, 9, 0, 900),
      (0, 0, 0, 9, 9000),
      (9, 9, 9, 9, 9999)
    )

  val digit = Gen.choose(0, 9)

  def checkInOut: (Int, Int, Int, Int, Int) => Unit =
  {
    (d0, d1, d2, d3, o) =>
    {
      val vars = new mutable.HashMap[Node, Node]()

      vars(c.io.bcd(0)) = UInt(d0)
      vars(c.io.bcd(1)) = UInt(d1)
      vars(c.io.bcd(2)) = UInt(d2)
      vars(c.io.bcd(3)) = UInt(d3)
      vars(c.io.out) = UInt(o)

      t.step(vars, isTrace = trace) should be(true)
    }
  }

  def scaleAndAdd(d0: Int, d1: Int, d2: Int, d3: Int): Int =
  {
    d0 + 10 * d1 + 100 * d2 + 1000 * d3
  }

  property("Selected combinations of input digits, should convert to integers correctly.")
  {
    forAll(examples)
    { checkInOut }
  }

  /*property("All combinations of input digits, should convert to integers correctly.")
  {
    val ds =
      for (d0 <- 0 until 10 ; d1 <- 0 until 10 ; d2 <- 0 until 10 ; d3 <- 0 until 10)
        yield(d0, d1, d2, d3)

    ds.map(x => checkInOut(x._1, x._2, x._3, x._4, scaleAndAdd(x._1, x._2, x._3, x._4)))
  }*/

  property("Random combinations of input digits, should convert to integers correctly.")
  {
    forAllNoShrink((digit, "d0"), (digit, "d1"), (digit, "d2"), (digit, "d3"))
    {
      (d0: Int, d1: Int, d2: Int, d3: Int) =>
        checkInOut(d0, d1, d2, d3, scaleAndAdd(d0, d1, d2, d3))
    }
  }
}
