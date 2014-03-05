package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._
import com.thesett.numbers.util.BitsAndBytes
import scala.collection.mutable

class BCDAndFlags extends Bundle
{
  val bcd = UInt(width = 4)
  val flags = UInt(width = 4)

  def default() =
  {
    this.bcd := UInt(0)
    this.flags := UInt(0)
  }
}

object AsarFlags
{
  final val NON_DIGIT: UInt = UInt(0x8, 4)
  final val MINUS: UInt = UInt(0x1, 4)
  final val DOT: UInt = UInt(0x2, 4)
  final val COLON: UInt = UInt(0x3, 4)
  final val SOH: UInt = UInt(0x4, 4)
  final val EQUALS: UInt = UInt(0x5, 4)
}

/**
 * AtoiSubtractAndRange converts ASCII into BCD, or if an ASCII character is not within the range from ASCII zero to
 * ASCII nine, detects this, and also sets flags to indicate if it is within a special set of characters.
 */
class AtoiSubtractAndRange extends Module
{
  val io = new Bundle
  {
    val ascii = UInt(INPUT, 8)
    val out = new BCDAndFlags().asOutput()
  }

  io.out.flags := UInt(0)
  io.out.bcd := io.ascii - UInt(BitsAndBytes.ZERO_ASCII)

  when(io.ascii < UInt(BitsAndBytes.ZERO_ASCII))
  {
    io.out.flags := AsarFlags.NON_DIGIT
    io.out.bcd := UInt(0)
  }

  when(io.ascii > UInt(BitsAndBytes.NINE_ASCII))
  {
    io.out.flags := AsarFlags.NON_DIGIT
    io.out.bcd := UInt(0)
  }

  switch(io.ascii)
  {
    is(UInt(BitsAndBytes.MINUS_ASCII))
    {
      io.out.flags := AsarFlags.MINUS
      io.out.bcd := UInt(0)
    }
    is(UInt(BitsAndBytes.DOT_ASCII))
    {
      io.out.flags := AsarFlags.DOT
      io.out.bcd := UInt(0)
    }
    is(UInt(BitsAndBytes.COLON_ASCII))
    {
      io.out.flags := AsarFlags.COLON
      io.out.bcd := UInt(0)
    }
    is(UInt(BitsAndBytes.SOH_ASCII))
    {
      io.out.flags := AsarFlags.SOH
      io.out.bcd := UInt(0)
    }
    is(UInt(BitsAndBytes.EQUALS_ASCII))
    {
      io.out.flags := AsarFlags.EQUALS
      io.out.bcd := UInt(0)
    }
  }
}

object AtoiSubtractAndRange
{
  def apply(ascii: UInt): BCDAndFlags =
  {
    val asar = Module(new AtoiSubtractAndRange())
    asar.io.ascii := ascii
    asar.io.out
  }
}


