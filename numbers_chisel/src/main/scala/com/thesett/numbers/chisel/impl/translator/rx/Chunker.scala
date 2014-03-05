package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._

class ChunkedAsciiBCD extends Bundle
{
  val bcdAndFlags = Vec.fill(4)
  { new BCDAndFlags() }
  val ascii = Vec.fill(4)
  { UInt(width = 8) }
  val count = UInt(width = 3)
  val endMarker = Bool()
  val nextAfterEnd = Bool()

  def default() =
  {
    Range(0, 4).map(this.bcdAndFlags(_).default())
    Range(0, 4).map(this.ascii(_) := UInt(0))
    this.count := UInt(0)
    this.endMarker := Bool(false)
    this.nextAfterEnd := Bool(false)
  }
}

class Chunker extends Module
{
  val io = new Bundle
  {
    val in = UInt(INPUT, 32)
    val inEnable = Bool(INPUT)

    val tag = new ChunkedAsciiBCD().asOutput()
    val data = new ChunkedAsciiBCD().asOutput()

    val eom = Bool(OUTPUT)

    val error = Bool(OUTPUT)
  }

  // Registers.
  val nextStartsWithTag = RegInit(Bool(true))

  // Default outputs.
  io.tag.default()
  io.data.default()
  io.error := Bool(false)

  // Separate out characters.
  val cs = Vec(Range(0, 4).map(i => io.in((i + 1) * 8 - 1, i * 8)))

  // Begin the conversion to decimals here.
  val ds = Vec(cs.map(AtoiSubtractAndRange(_)))

  // Compare for separators, enumerating the results.
  val scan = Vec(ds.map(_.flags === AsarFlags.SOH))

  // Scan for the EOM marker.
  io.eom := ds.map(_.flags === AsarFlags.DOT).reduceRight(_ || _)

  // Check if the last character of the previous input was a separator, and the first of this input, in which case
  // there are two together which is not allowed.
  val lastWasSeparator = RegInit(Bool(false))

  when(io.inEnable)
  {
    lastWasSeparator := ds(3).flags === AsarFlags.SOH
  }

  when((ds(0).flags === AsarFlags.SOH) && lastWasSeparator)
  {
    io.error := Bool(true)
  }

  val startsWithTag = Bool()
  startsWithTag := nextStartsWithTag

  // Slice up the input.
  val val1 = new ChunkedAsciiBCD()
  val val2 = new ChunkedAsciiBCD()

  val1.default()
  val2.default()

  switch(scan.toBits)
  {
    is(UInt(0))
    {
      val1.count := UInt(4)
      val1.bcdAndFlags(0) := ds(0)
      val1.bcdAndFlags(1) := ds(1)
      val1.bcdAndFlags(2) := ds(2)
      val1.bcdAndFlags(3) := ds(3)
    }
    is(UInt(1))
    {
      nextStartsWithTag := ~nextStartsWithTag
      val2.count := UInt(3)
      val2.bcdAndFlags(0) := ds(1)
      val2.bcdAndFlags(1) := ds(2)
      val2.bcdAndFlags(2) := ds(3)
      val1.endMarker := Bool(true)
    }
    is(UInt(2))
    {
      nextStartsWithTag := ~nextStartsWithTag
      val1.count := UInt(1)
      val2.count := UInt(2)
      val1.bcdAndFlags(0) := ds(0)
      val2.bcdAndFlags(0) := ds(2)
      val2.bcdAndFlags(1) := ds(3)
      val1.endMarker := Bool(true)
    }
    is(UInt(4))
    {
      nextStartsWithTag := ~nextStartsWithTag
      val1.count := UInt(2)
      val2.count := UInt(1)
      val1.bcdAndFlags(0) := ds(0)
      val1.bcdAndFlags(1) := ds(1)
      val2.bcdAndFlags(0) := ds(3)
      val1.endMarker := Bool(true)
    }
    is(UInt(5))
    {
      val2.count := UInt(1)
      val1.count := UInt(1)
      val2.bcdAndFlags(0) := ds(1)
      val1.bcdAndFlags(0) := ds(3)
      val1.endMarker := Bool(true)
      val2.endMarker := Bool(true)
      val1.nextAfterEnd := Bool(true)
    }
    is(UInt(8))
    {
      nextStartsWithTag := ~nextStartsWithTag
      val1.count := UInt(3)
      val1.bcdAndFlags(0) := ds(0)
      val1.bcdAndFlags(1) := ds(1)
      val1.bcdAndFlags(2) := ds(2)
      val1.endMarker := Bool(true)
    }
    is(UInt(9))
    {
      val2.count := UInt(2)
      val2.bcdAndFlags(0) := ds(1)
      val2.bcdAndFlags(1) := ds(2)
      val1.endMarker := Bool(true)
      val2.endMarker := Bool(true)
    }
    is(UInt(10))
    {
      val1.count := UInt(1)
      val2.count := UInt(1)
      val1.bcdAndFlags(0) := ds(0)
      val2.bcdAndFlags(0) := ds(2)
      val1.endMarker := Bool(true)
      val2.endMarker := Bool(true)
    }
    is(UInt(3))
    { io.error := Bool(true) }
    is(UInt(6))
    { io.error := Bool(true) }
    is(UInt(7))
    { io.error := Bool(true) }
    is(UInt(11))
    { io.error := Bool(true) }
    is(UInt(12))
    { io.error := Bool(true) }
    is(UInt(13))
    { io.error := Bool(true) }
    is(UInt(14))
    { io.error := Bool(true) }
    is(UInt(15))
    { io.error := Bool(true) }
  }

  // Map to outputs.
  when(startsWithTag)
  {
    io.tag := val1
    io.data := val2
  }
  .otherwise
  {
    io.tag := val2
    io.data := val1
  }

  // Check if input is disabled, and if it is output zero length, and zeroed out data.
  // This is almost the same as a reset, but does not reset the registers, as state is preserved until there is more
  // input. The noInput condition is temporary.
  when(!io.inEnable)
  {
    io.tag.default()
    io.data.default()

    nextStartsWithTag := nextStartsWithTag
    lastWasSeparator := lastWasSeparator
    io.error := Bool(false)
    io.eom := Bool(false)
  }
}