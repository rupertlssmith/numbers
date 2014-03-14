package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._
import scala.Array
import scala.collection.mutable
import org.scalatest.{Ignore, BeforeAndAfterAll, PropSpec, Matchers}
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, TableDrivenPropertyChecks}
import com.thesett.numbers.util.BitsAndBytes
import scala.sys.process.Process
import com.thesett.chisel.util.chiselTestHelper
import org.scalatest.prop.nsv.NoShrinkVariants

class AtoiSubtractAndRangeTests(ignore: String) extends PropSpec
with BeforeAndAfterAll with TableDrivenPropertyChecks with Matchers
{
  def trace = false

  class DummyTests(c: AtoiSubtractAndRange) extends Tester(c, isTrace = trace)
  {
  }

  val (c, t) =
    chiselTestHelper(Array("--genHarness", "--compile", "--backend", "c", "--vcd", "--targetDir", "target", "--test"),
      () => Module(new AtoiSubtractAndRange()))
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

  val specialChars =
    Set(BitsAndBytes.MINUS_ASCII,
      BitsAndBytes.DOT_ASCII,
      BitsAndBytes.COLON_ASCII,
      BitsAndBytes.SOH_ASCII,
      BitsAndBytes.EQUALS_ASCII
    )

  val specialCharExamples =
    Table(
      ("ascii", "flags", "bcd"),
      (BitsAndBytes.MINUS_ASCII.toInt, AsarFlags.MINUS, 0),
      (BitsAndBytes.DOT_ASCII.toInt, AsarFlags.DOT, 0),
      (BitsAndBytes.COLON_ASCII.toInt, AsarFlags.COLON, 0),
      (BitsAndBytes.SOH_ASCII.toInt, AsarFlags.SOH, 0),
      (BitsAndBytes.EQUALS_ASCII.toInt, AsarFlags.EQUALS, 0)
    )

  val digits =
    Table(
      ("ascii", "flags", "bcd"),
      (0x30, UInt(0), 0),
      (0x31, UInt(0), 1),
      (0x32, UInt(0), 2),
      (0x33, UInt(0), 3),
      (0x34, UInt(0), 4),
      (0x35, UInt(0), 5),
      (0x36, UInt(0), 6),
      (0x37, UInt(0), 7),
      (0x38, UInt(0), 8),
      (0x39, UInt(0), 9)
    )

  def checkInOut: (Int, UInt, Int) => Unit =
  {
    (a, f, b) =>
    {
      val vars = new mutable.HashMap[Node, Node]()

      t.poke(c.io.ascii, a)

      t.step(1)

      t.expect(c.io.out.flags, f.litValue())
      t.expect(c.io.out.bcd, b)
    }
  }

  property("Certain ASCII special characters should be matched.")
  {
    forAll(specialCharExamples)
    { checkInOut }
  }

  property("ASCII digits should be converted to BCD.")
  {
    forAll(digits)
    { checkInOut }
  }

  property("All non-digits (and not special char) should be detected and mapped to zero with non-digit flag set.")
  {
    (0 until 256).map(ascii =>
      if (ascii >= 0 && (ascii <= 0x2f || ascii >= 0x40) && ascii <= 255 && !specialChars.contains(ascii.toByte))
      {checkInOut(ascii, AsarFlags.NON_DIGIT, 0) })
  }
}