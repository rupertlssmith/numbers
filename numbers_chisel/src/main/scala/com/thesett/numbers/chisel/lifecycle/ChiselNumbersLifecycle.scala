package com.thesett.numbers.chisel.lifecycle

import com.thesett.util.concurrent.StartStopLifecycleBase
import com.thesett.numbers.chisel.impl.translator.rx.RxTop
import Chisel._
import scala.Array
import scala.sys.process.Process
import com.thesett.chisel.util.chiselTestHelper
import com.thesett.numbers.message.{HeaderBuilder, NumbersBuilder, Numbers}
import java.nio.ByteBuffer
import scala.collection.mutable
import com.thesett.numbers.util.FrameReaderUtil
import com.thesett.numbers.factory.NumbersFactory
import com.thesett.numbers.chisel.impl.factory.NumbersFactoryChiselImpl

/**
 * This lifecycle controls the Chisel simulation and process lifecycle, and provides {@link NumbersFactory} instances
 * that are legitimate within the this lifecycle.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Implement a lifecycle for Chisel simulation.
 * <tr><td> Provide message factories, valid within the Chisel simulation lifecycle.
 * <tr><td> Provide Chisel modules.
 * </table></pre>
 *
 * @author Rupert Smith
 */
class ChiselNumbersLifecycle(trace: Boolean = false) extends StartStopLifecycleBase
with ChiselModuleProvider with RxTopWrapper
{
  class DummyTests(c: RxTop) extends Tester(c, isTrace = trace)
  {
  }

  val (c, t) =
    chiselTestHelper(Array("--genHarness", "--compile", "--backend", "c", "--vcd", "--targetDir", "target", "--test"),
      () => Module(new RxTop()))
    { c => new DummyTests(c) }

  var testProcess: Process = _

  var frames: List[ByteBuffer] = Nil

  val numbersFactory: NumbersFactory = NumbersFactory.createInstance
  var builder: NumbersBuilder = numbersFactory.getBuilder
  var headerBuilder: HeaderBuilder = numbersFactory.getHeaderBuilder

  var message: Numbers = null

  /**
   * Advances RxTop by one step.
   *
   * @param in The next input word.
   * @param inEnable The input enable signal.
   *
   * @return (<tt>true</tt> iff a tag/value pair is found,
   *         the next tag found,
   *         the next value found,
   *         <tt>true</tt> iff a sequence number is found,
   *         the sequence number,
   *         <tt>true</tt> iff EOM is detected).
   */
  def step(in: Int, inEnable: Boolean): (Boolean, Int, Int, Boolean, Int, Boolean) =
  {
    t.poke(c.io.in, in)
    t.poke(c.io.inEnable, if(inEnable) 1 else 0)

    t.step(1)

    (t.peek(c.io.rdy).intValue() == 1,
     t.peek(c.io.tagOut).intValue(),
     t.peek(c.io.valOut).intValue(),
     t.peek(c.io.seqNoRdy).intValue() == 1,
     t.peek(c.io.seqNo).intValue(),
     t.peek(c.io.eomRdy).intValue() == 1
     )
  }

  def start() =
  {
    testProcess = t.startTesting

    running()
  }

  override def shutdown()
  {
    t.endTesting()//(testProcess)

    terminated()
  }

  def getRxTop: RxTopWrapper =
  { this }

  def addFrameBuffer(buffer: ByteBuffer) =
  {
    frames = frames ::: List(buffer)
  }

  /**
   * Processes one frame buffer. Any tag/value pairs produced by the circuit whilst feeding in the frame buffer, are
   * appended onto the message builder.
   *
   * @param frame A frame buffer to process.
   *
   * @return (<tt>true</tt> iff the whole buffer was consumed,
   *          <tt>true</tt> iff end of message was found).
   */
  def stepFrame(frame: ByteBuffer): (Boolean, Boolean) =
  {
    while (FrameReaderUtil.hasNextWord(frame))
    {
      val nextWord = FrameReaderUtil.nextWord(frame)
      val (rdy, tag, value, seqRdy, seqNo, eomRdy) = step(nextWord, true)

      if (rdy)
      {
        builder.append(tag, value)
      }

      if (seqRdy)
      {
        System.out.println("stepFrame: Got sequence number - " + seqNo)
        headerBuilder.setSequenceNo(seqNo)
      }

      if(eomRdy)
      {
        System.out.println("stepFrame: Got EOM")
        return (!FrameReaderUtil.hasNextWord(frame), true)
      }
    }

    System.out.println("stepFrame: Consumed frame, but did not get EOM");
    (true, false)
  }

  /**
   * Processes all frames, until either EOM is encountered, or the frames are exhausted.
   *
   * @param frames The frames to process.
   *
   * @return (Any frames remaining that are partially processed, <tt>true</tt> iff EOM was found).
   */
  def stepFrames(frames: List[ByteBuffer]): (List[ByteBuffer], Boolean) =
  {
    frames match
    {
      case Nil => (Nil, false) // Consumes all frames, but did not find EOM.
      case f :: fs =>
        stepFrame(f) match
        {
          case (true, true) => (fs, true) // Found EOM and consumed the whole frame.
          case (true, false) => stepFrames(fs) // Did not find EOM, continue on next frame.
          case (false, true) => (f :: fs, true) // Found EOM but did not consume the whole frame.
          case (false, false) => (f :: fs, false) // Did not find EOM, and did not consume the whole frame (error).
        }
    }
  }

  /**
   * Steps the Rx circuit with no input until EOM is seen, or enough cycles have passed that EOM cannot be produced
   * without further input.
   *
   * @return <tt>true</tt> if EOM is seen within the extra no-input cycles.
   */
  def stepNoFrame(count: Int): Boolean =
  {
    count match
    {
      case 0 => false
      case c =>
      {
        val (rdy, tag, value, _, _, _) = step(0, false)

        if (rdy)
        {
          builder.append(tag, value)
          true
        }
        else
          stepNoFrame(c - 1)
      }
    }

    message = builder.build()
    true
  }

  /**
   * Steps through all frames, until EOM is encountered, or the frames are fully processed. If the frames are all
   * consumed without encountered EOM, stepping continues whilst asserting the end-of-frame condition, for enough
   * cycles to allow any pending EOM condition to complete processing.
   *
   * @return <tt>true</tt> if an end of message condition was found, <tt>false</tt> if cannot continue without more
   */
  def stepTillEndOfMessage(): Boolean =
  {
    val (nfs, eomDuringFrames) = stepFrames(frames)
    frames = nfs

    if (!eomDuringFrames && frames == Nil)
      stepNoFrame(4)
    else
    {
      if (eomDuringFrames)
      {
        message = builder.build()
      }

      eomDuringFrames
    }
  }

  def consumeMessage(): Numbers =
  {
    val result = message

    builder = numbersFactory.getBuilder
    headerBuilder = numbersFactory.getHeaderBuilder
    message = null

    result
  }

  /**
   * Provides a messaging factory, valid within the Chisel lifecycle.
   *
   * @return A messaging factory.
   */
  def getNumbersFactory: NumbersFactory =
  {
    new NumbersFactoryChiselImpl(this)
  }

  /** {@inheritDoc} */
  def moveToContext(message: Numbers)
  {
    //headerBuilder.setSequenceNo(sequenceNo)
    message.setHeader(headerBuilder.build)
  }
}
