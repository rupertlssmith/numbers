package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._

/**
 * Pairer registers pairs of input values, and once it has a pair of values, outputs them in the same cycle.
 *
 * <p/>Does not take account of one input becoming ready in the same cycle as the other, when that input has already
 * been registered and is waiting for the other side to arrive. Could use FIFOs between the pairer and decoders,
 * or just enrich this directly to handle ready and more data arriving in one go. It is not yet clear if this will
 * be needed, or if it will be safe to assume this cannot happen. For example, when working with 4 input characters
 * at once, it will not be needed, as the worst case within one cycle is to end a tag, start and end a value, and start
 * but not complete the next tag; the next tag cannot complete in the same cycle, so this should be sufficient.
 *
 * Not true:
 *
 * Word 1: 3tag/1sep
 * Word 2: 1val/1sep/1tag/1sep
 *
 * Tag becomes ready, when a tag is already latched. This is the worst case for a 4 byte decoder.
 */
class Pairer extends Module
{
  val io = new Bundle()
  {
    val tagIn = UInt(INPUT, 32)
    val tagRdy = Bool(INPUT)

    val valIn = UInt(INPUT, 32)
    val valRdy = Bool(INPUT)

    val reset = Bool(INPUT)

    val tagOut = UInt(OUTPUT, 32)
    val valOut = UInt(OUTPUT, 32)
    val rdy = Bool(OUTPUT)
  }

  val tag = RegInit(UInt(0, 32))
  val data = RegInit(UInt(0, 32))
  val tagRdy = RegInit(Bool(false))
  val dataRdy = RegInit(Bool(false))

  io.tagOut := UInt(0)
  io.valOut := UInt(0)
  io.rdy := Bool(false)

  val tagAvailable: Bool = io.tagRdy || tagRdy
  val valAvailable: Bool = io.valRdy || dataRdy

  // Produce output when both a tag and a value are available.
  when (tagAvailable && valAvailable)
  {
    io.rdy := Bool(true)

    io.tagOut := Mux(tagRdy, tag, io.tagIn)
    io.valOut := Mux(dataRdy, data, io.valIn)

    tagRdy := Bool(false)
    dataRdy := Bool(false)
  }

  // Register any input tag, so long as output is not being produced as a pass-through value.
  when(io.tagRdy && !(io.rdy && !tagRdy))
  {
    tag := io.tagIn
    tagRdy := Bool(true)
  }

  // Register any input value, so long as output is not being produced as a pass-through value.
  when(io.valRdy && !(io.rdy && !dataRdy))
  {
    data := io.valIn
    dataRdy := Bool(true)
  }

  when (io.reset)
  {
    tag := UInt(0)
    data := UInt(0)
    tagRdy := Bool(false)
    dataRdy := Bool(false)
  }
}