package com.thesett.chisel.util

import Chisel._

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
object chiselTestHelper
{
  def apply[T <: Module](args: Array[String], gen: () => T, scanner: T => TestIO = null, printer: T => TestIO = null)
                        (ftester: T => Tester[T]): (T, Tester[Module]) =
  {
    Module.initChisel()
    chiselMain.readArgs(args)

    try
    {
      val c = gen()

      if (scanner != null)
      {
        val s = scanner(c)
        Module.scanArgs ++= s.args
        for (a <- s.args) a.isScanArg = true
        Module.scanFormat = s.format
      }

      if (printer != null)
      {
        val p = printer(c)
        Module.printArgs ++= p.args
        for (a <- p.args) a.isPrintArg = true
        Module.printFormat = p.format
      }

      if (ftester != null)
      {
        Module.tester = ftester(c)
      }

      Module.backend.elaborate(c)

      if (Module.isCheckingPorts) Module.backend.checkPorts(c)
      if (Module.isCompiling && Module.isGenHarness) Module.backend.compile(c)

      (c, Module.tester)
    }
    finally
    {
      ChiselError.report()
    }
  }
}
