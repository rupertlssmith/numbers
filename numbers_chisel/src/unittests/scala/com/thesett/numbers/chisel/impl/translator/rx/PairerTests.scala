package com.thesett.numbers.chisel.impl.translator.rx

import org.scalatest.{Ignore, Matchers, BeforeAndAfterAll, PropSpec}
import org.scalatest.prop.{Checkers, GeneratorDrivenPropertyChecks, TableDrivenPropertyChecks}
import Chisel._
import scala.sys.process.Process
import scala.collection.mutable
import org.scalacheck.{Gen, Commands}
import scala.util.Random
import org.scalacheck.Test.Parameters.Default
import com.thesett.chisel.util.chiselTestHelper

class PairerTests extends PropSpec
with BeforeAndAfterAll with TableDrivenPropertyChecks with GeneratorDrivenPropertyChecks with Matchers with Checkers
{
  def trace = false

  class DummyTests(c: Pairer) extends Tester(c, isTrace = trace)
  {
  }

  val (c, t) =
    chiselTestHelper(Array("--genHarness", "--compile", "--backend", "c", "--vcd", "--targetDir", "target", "--test"),
      () => Module(new Pairer()))
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

  val examples =
    Table(
      ("tagIn", "tagRdy", "valIn", "valRdy", "reset", "tagOut", "valOut", "rdy"),
      (0, false, 0, false, true, 0, 0, false),
      (1, true, 1, true, false, 1, 1, true),
      (5, true, 0, false, false, 0, 0, false),
      (0, false, 7, true, false, 5, 7, true),
      (0, false, 2, true, false, 0, 0, false),
      (6, true, 0, false, false, 6, 2, true)
    )

  def checkInOut: (Int, Boolean, Int, Boolean, Boolean, Int, Int, Boolean) => Unit =
  {
    (tagIn, tagRdy, valIn, valRdy, reset, tagOut, valOut, rdy) =>
    {
      t.poke(c.io.tagIn, tagIn)
      t.poke(c.io.tagRdy, if(tagRdy) 1 else 0)
      t.poke(c.io.valIn, valIn)
      t.poke(c.io.valRdy, if(valRdy) 1 else 0)
      t.poke(c.io.reset, if(reset) 1 else 0)

      t.step(1)

      t.expect(c.io.tagOut, tagOut)
      t.expect(c.io.valOut, valOut)
      t.expect(c.io.rdy, if(rdy) 1 else 0)
    }
  }

  object PairerSpecification extends Commands
  {

    case class State(prevCommand: Command,
                     tag: Int,
                     tagRdy: Boolean,
                     value: Int,
                     valueRdy: Boolean,
                     nextTag: Int,
                     nextValue: Int)

    def initialState() =
    {
      /*System.out.println("Requesting initial state.")*/
      checkInOut(0, false, 0, false, true, 0, 0, false) // Resets the DUT.
      State(null, 0, false, 0, false, Math.abs(Random.nextInt()), Math.abs(Random.nextInt()))
    }

    case object Tag extends Command
    {
      def run(s: State) =
      {
        /*System.out.println("Tag: run with state:", s)*/
        checkInOut(s.nextTag, true, 0, false, false, if (s.valueRdy) s.nextTag else 0, s.value, s.valueRdy)
      }

      def nextState(s: State) =
      {
        val next = Math.abs(Random.nextInt())

        if (s.valueRdy)
        {
          State(Tag, 0, false, 0, false, next, s.nextValue)
        }
        else
        {
          State(Tag, s.nextTag, true, 0, false, next, s.nextValue)
        }
      }
    }

    case object Val extends Command
    {
      def run(s: State) =
      {
        /*System.out.println("Val: run with state:", s)*/
        checkInOut(0, false, s.nextValue, true, false, s.tag, if (s.tagRdy) s.nextValue else 0, s.tagRdy)
      }

      def nextState(s: State) =
      {
        val next = Math.abs(Random.nextInt())

        if (s.tagRdy)
        {
          State(Val, 0, false, 0, false, s.nextTag, next)
        }
        else
        {
          State(Val, 0, false, s.nextValue, true, s.nextTag, next)
        }
      }
    }

    case object TagVal extends Command
    {
      def run(s: State) =
      {
        /*System.out.println("TagVal: run with state:", s)*/

        // If a tag or a value is already registered, that should be output. Otherwise, the value just input should be
        // output.
        checkInOut(s.nextTag, true, s.nextValue, true, false,
          if (s.tagRdy) s.tag else s.nextTag,
          if (s.valueRdy) s.value else s.nextValue,
          true)
      }

      def nextState(s: State) =
      {
        val nextVal = Math.abs(Random.nextInt())
        val nextTag = Math.abs(Random.nextInt())

        // If a tag or a value was currently set, that should have been output, and the registered value replaced
        // by the next value. If neither was set, the state should be clear.
        if (s.valueRdy)
        {
          State(s.prevCommand, 0, false, s.nextValue, true, nextTag, nextVal)
        }
        else if (s.tagRdy)
        {
          State(s.prevCommand, s.nextTag, true, 0, false, nextTag, nextVal)
        }
        else
        {
          State(s.prevCommand, 0, false, 0, false, nextTag, nextVal)
        }
      }
    }

    case object Noop extends Command
    {
      def run(s: State) =
      {
        /*System.out.println("Noop: run with state:", s)*/
        checkInOut(0, false, 0, false, false, 0, 0, false)
      }

      def nextState(s: State) = State(s.prevCommand, s.tag, s.tagRdy, s.value, s.valueRdy, s.nextTag, s.nextValue)
    }

    def genCommand(s: State): Gen[Command] =
    {
      s.prevCommand match
      {
        case null => Gen.oneOf(Tag, Val, Noop, TagVal)
        case Tag => Gen.oneOf(Val, Noop, TagVal)
        case Val => Gen.oneOf(Tag, Noop, TagVal)
      }
    }
  }

  property("Produces output only once both inputs have arrived.")
  {
    forAll(examples)
    { checkInOut }
  }

  property("For random inputs, produces output only once both inputs have arrived, and first value seen is latched.")
  {
    val prms = new Default
    {
      override val minSuccessfulTests = 20
      override val minSize = 2
      override val maxSize = 40
    }

    PairerSpecification.check(prms)
  }
}