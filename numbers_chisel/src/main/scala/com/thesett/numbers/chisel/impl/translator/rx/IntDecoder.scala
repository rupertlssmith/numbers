package com.thesett.numbers.chisel.impl.translator.rx

import scala.math._
import Chisel._

/**
 * Converts a variable length vector of BCD encoded digits, into a binary integer. The digits are presented most
 * significant first, which means that the power of ten used to multiply each position in the vector, depends on the
 * count of active digits in the vector. The {@link BCDtoi} conversion module, expects the least significant digit
 * first, and expects the digits to always appear in the same position, in order to be multiplied by a given power
 * of ten (10 to the i, where i is the position of the digit). This function maps the variable length and position
 * input, onto the fixed length and position conversion module.
 */
class IntDecoder extends Module
{
  val io = new Bundle()
  {
    val in = new ChunkedAsciiBCD().asInput()

    val out = UInt(OUTPUT, 32)
    val rdy = Bool(OUTPUT)
  }

  /**
   * Shifts an integer by a power of ten using multiplication.
   *
   * <p/>All the possible multiplications are performed, and the correct one selected. This is ok, as the multipliers
   * are constant powers of ten. It is possible that these constant multiplies will get replaced with shifts and adds
   * at some point.
   */
  def scale(power: UInt, value: UInt) =
  {
    MuxLookup(power, value,
      (1 until 5)
        .map(x => UInt(x) -> value * UInt(pow(10, x).toInt)))
  }

  val bcdToi = Module(new BCDtoi())

  val total = Reg(init = UInt(0, 32))

  io.out := UInt(0)
  io.rdy := Bool(false)

  val count = io.in.count
  val end = io.in.endMarker
  val next = io.in.nextAfterEnd

  // Extract the individual BCD digits to a vector.
  val bcd = Vec(io.in.bcdAndFlags.map(_.bcd))

  // Map the BCD digits from most significant order to least significant, padding the higher position digits with
  // zeros to make up the full width of the conversion.
  val bcdLow = MuxLookup(count, UInt(0, 16),
      (1 until 5)
        .map( x => UInt(x) -> Vec((0 until 4).map(i => (if (i >= x) UInt(0) else bcd(x - i - 1)))).toBits()))

  (0 until 4).map(x => bcdToi.io.bcd(x) := bcdLow((x + 1) * 4 - 1, x * 4))

  val nextInt: UInt = bcdToi.io.out
  val prevScaled: UInt = scale(count, total)

  when(count > UInt(0))
  {
    when(end)
    {
      when(next)
      {
        io.out := total
        io.rdy := Bool(true)
        total := nextInt
      }
      .otherwise
      {
        io.out := prevScaled + nextInt
        io.rdy := Bool(true)
        total := UInt(0)
      }
    }
    .otherwise
    {
      total := prevScaled + nextInt
    }
  }
  .elsewhen(end)
  {
    io.out := total
    io.rdy := Bool(true)
    total := UInt(0)
  }
}

