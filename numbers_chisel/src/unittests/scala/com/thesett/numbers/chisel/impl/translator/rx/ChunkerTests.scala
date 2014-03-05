package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._
import scala.sys.process.Process
import scala.collection.mutable
import org.scalatest.{Matchers, BeforeAndAfterAll, PropSpec}
import org.scalatest.prop.{Checkers, GeneratorDrivenPropertyChecks, TableDrivenPropertyChecks}
import org.scalacheck.Gen
import com.thesett.chisel.util.chiselTestHelper
import scala.util.Random
import org.scalatest.prop.nsv.NoShrinkVariants

class ChunkerTests extends PropSpec
with BeforeAndAfterAll with TableDrivenPropertyChecks
with GeneratorDrivenPropertyChecks with Matchers with Checkers with NoShrinkVariants
{
  def trace = false

  class DummyTests(c: Chunker) extends Tester(c, Array(c.io))
  {
    defTests
    { true }
  }

  val (c, t) =
    chiselTestHelper(Array("--genHarness", "--compile", "--backend", "c", "--vcd", "--targetDir", "target", "--test"),
      () => Module(new Chunker()))
    { c => new DummyTests(c) }

  var testProcess: Process = _

  override def beforeAll()
  {
    testProcess = t.startTest
  }

  override def afterAll()
  {
    t.endTest(testProcess)
  }

  val errorConditions =
    Table(
      ("in", "error"),
      (0, false)
    )

  /**
   * Implements simple error checking against inputs.
   *
   * @param in The input word.
   * @param inEnable The input enable flag.
   * @param error <tt>true</tt> iff this input combination is expected to produce an error.
   */
  def checkInOut(in: Int, inEnable: Boolean, error: Boolean, eom: Boolean) =
  {
    val vars = new mutable.HashMap[Node, Node]()
    val ovars = new mutable.HashMap[Node, Node]()

    vars(c.io.in) = UInt(in)
    vars(c.io.inEnable) = Bool(inEnable)

    //System.out.println(in, noInput, error)

    t.step(vars, ovars, isTrace = trace) should be(true)

    ovars(c.io.error).litValue().toInt == 1 should be(error)
    ovars(c.io.eom).litValue().toInt == 1 should be(eom)
  }

  val illegalInputs = Array(3, 6, 7, 11, 12, 13, 14, 15)
  val legalInputs = Array(0, 1, 2, 4, 5, 8, 9, 10)
  val legalHighs = Array(8, 9, 10)
  val legalLows = Array(1, 5, 9)
  val legalHigh = Gen.oneOf(legalHighs)
  val legalLow = Gen.oneOf(legalLows)
  val legal = Gen.oneOf(legalInputs)
  val digit = Gen.choose(0, 9)

  /**
   * Builds an input in the expected format for the chunker; that is, numbers separated by separator characters. The
   * digits making up the numbers are chosen randomly, which will allow leading zeros.
   *
   * @param separators A bit pattern describing the positions on the separator characters.
   * @param width The byte width of the input to construct.
   *
   * @return An randomized input in the format expected by the chunker.
   */
  def toInput(separators: Int, width: Int): Int =
  {
    width match
    {
      case 0 => 0
      case z =>
      {
        ((if ((separators & Math.pow(2, z - 1).toInt) > 0)
          1 << ((width - 1) * 8)
        else
          (0x30 + Random.nextInt(9)) << ((width - 1) * 8))
          + toInput(separators, width - 1))
      }
    }
  }

  property("All illegal single inputs are recognized as errors.")
  {
    illegalInputs.map(e => checkInOut(toInput(e, 4), inEnable = true, error = true, eom = false))
  }

  property("All legal single inputs are accepted without error.")
  {
    // Some 2s have been added higher up this list, to avoid the situation where the last character is a separator
    // in one input, and the first character in the next input is too, which is an error.
    List(0, 1, 2, 4, 5, 8, 2, 9, 2, 10, 2).map(e => checkInOut(toInput(e, 4), inEnable = true, error = false, eom = false))
  }

  property("Input with separator last, cannot be followed by input with separator first (illegal input, " +
    "over two cycles).")
  {
    forAllNoShrink((legalHigh, "in1"), (legalLow, "in2"))
    {
      (in1: Int, in2: Int) =>
      {
        checkInOut(toInput(in1, 4), inEnable = true, error = false, eom = false)
        checkInOut(toInput(in2, 4), inEnable = true, error = true, eom = false)

        // This should always work, and effectively resets for the next test.
        checkInOut(toInput(2, 4), inEnable = true, error = false, eom = false)
      }
    }
  }

  /*property("Input with one separator always flips outputs and input with two separators does not flip outputs.")
  {
    var previous: Int = 0

    forAllNoShrink((legal, "in"))
    {
      (in: Int, in2: Int) =>
        whenever((previous & 8) == 0 || (in & 1) == 0)
        {
          checkInOut(toInput(in, 4), inEnable = true, error = false)
          previous = in
        }
    }
  }*/

  property("Input ignored when input is not enabled; separator conflicts within one input word not detected.")
  {
    illegalInputs.map(e => checkInOut(toInput(e, 4), inEnable = false, error = false, eom = false))
  }

  property("Input ignored when input is not enabled; separator conflicts across two words not detected.")
  {
    forAllNoShrink((legalHigh, "in1"), (legalLow, "in2"))
    {
      (in1: Int, in2: Int) =>
      {
        checkInOut(toInput(in1, 4), inEnable = false, error = false, eom = false)
        checkInOut(toInput(in2, 4), inEnable = false, error = false, eom = false)
      }
    }
  }

  property("Separator conflicts across two words detected when disabled input occurs in-between.")
  {
    forAllNoShrink((legalHigh, "in1"), (Gen.choose(0, 15), "in2"), (legalLow, "in3"))
    {
      (in1: Int, in2: Int, in3: Int) =>
      {
        checkInOut(toInput(in1, 4), inEnable = true, error = false, eom = false)
        checkInOut(toInput(in2, 4), inEnable = false, error = false, eom = false)
        checkInOut(toInput(in3, 4), inEnable = true, error = true, eom = false)

        // This should always work, and effectively resets for the next test.
        checkInOut(toInput(2, 4), inEnable = true, error = false, eom = false)
      }
    }
  }

  /*property("Input never flips outputs, when input is not enabled.")
  {
    var previous: Int = 0

    forAllNoShrink((legal, "in"))
    {
      (in: Int, in2: Int) =>
        whenever((previous & 8) == 0 || (in & 1) == 0)
        {
          checkInOut(toInput(in, 4), inEnable = true, error = false)
          previous = in
        }
    }
  }*/

  property("EOM detected when EOM marker is present.")
  {
    checkInOut(0x2e0000, inEnable = true, error = false, eom = true)
  }

  property("EOM is not detected when EOM marker is present, but input is not enabled.")
  {
    checkInOut(0x2e0000, inEnable = false, error = false, eom = false)
  }
}
