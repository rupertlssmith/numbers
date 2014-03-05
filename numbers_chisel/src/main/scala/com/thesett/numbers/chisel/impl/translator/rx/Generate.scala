package com.thesett.numbers.chisel.impl.translator.rx

import Chisel._

object Generate
{
  def main(args: Array[String]): Unit =
  {
    chiselMain(args, () => Module(new RxTop()))
  }
}