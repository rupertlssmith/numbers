package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._
import scala.math._

/**
 * BCDtoi transforms 4-bit decimal numbers in BCD into binary. The 4-bit BCDs are input as a vector, and digits within
 * this vector are multiplied by powers of ten, then added up. The power of ten used to multiply the decimal at a
 * particular index within the input vector, at position i, is 10 to the i.
 */
class BCDtoi extends Module
{
  val io = new Bundle()
  {
    val bcd = Vec.fill(4)
    { UInt(width = 4) }.asInput()
    val out = UInt(OUTPUT, 32)
  }

  io.out := (0 until 4).map(i => io.bcd(i) * UInt(pow(10, i).toInt)).reduceRight(_ + _)
}



