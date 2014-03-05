package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._

class RxTop extends Module
{
  val io = new Bundle()
  {
    val in = UInt(INPUT, 32)
    val inEnable = Bool(INPUT)

    val tagOut = UInt(OUTPUT, 32)
    val valOut = UInt(OUTPUT, 32)
    val rdy = Bool(OUTPUT)

    val seqNo = UInt(OUTPUT, 32)
    val seqNoRdy = Bool(OUTPUT)

    val eomRdy = Bool(OUTPUT)
  }

  val chunker = Module(new Chunker())
  val tagDecoder = Module(new IntDecoder())
  val valDecoder = Module(new IntDecoder())
  val pairer = Module(new Pairer())

  chunker.io.in := io.in
  chunker.io.inEnable := io.inEnable

  tagDecoder.io.in <> chunker.io.tag
  valDecoder.io.in <> chunker.io.data

  pairer.io.tagIn := tagDecoder.io.out
  pairer.io.tagRdy := tagDecoder.io.rdy

  pairer.io.valIn := valDecoder.io.out
  pairer.io.valRdy := valDecoder.io.rdy

  io.tagOut := pairer.io.tagOut
  io.valOut := pairer.io.valOut
  io.rdy := pairer.io.rdy

  io.seqNo := UInt(0)
  io.seqNoRdy := Bool(false)

  io.eomRdy := chunker.io.eom

  val newMessage = RegInit(Bool(true))

  when(newMessage)
  {
    newMessage := Bool(false)
    chunker.io.inEnable := Bool(false)

    io.seqNo := io.in
    io.seqNoRdy := Bool(true)
  }

  when(chunker.io.eom)
  {
    newMessage := Bool(true)
  }
}