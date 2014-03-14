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
    Module.initChisel();
    chiselMain.readArgs(args)

    try
    {
      /* JACK - If loading design, read design.prm file*/
      if (Module.jackLoad != null)
      {Jackhammer.load(Module.jackDir, Module.jackLoad) }
      val c = gen();

      /* JACK - If dumping design, dump to jackDir with jackNumber points*/
      if (Module.jackDump != null)
      {
        Jackhammer.dump(Module.jackDir, Module.jackDump)
      } else
      {
        Module.backend.elaborate(c)
      }
      if (Module.isCheckingPorts) Module.backend.checkPorts(c)
      if (Module.isCompiling && Module.isGenHarness) Module.backend.compile(c)
      var tester: Tester[T] = null
      if (ftester != null && !Module.backend.isInstanceOf[VerilogBackend])
      {
        var res = false

        try
        {
          tester = ftester(c)
        } finally
        {
          if (tester != null && tester.process != null)
            res = tester.endTesting()
        }
        println(if (res) "PASSED" else "*** FAILED ***")
        if (!res) throwException("Module under test FAILED at least one test vector.")
      }
      (c, tester)
    } finally
    {
      ChiselError.report()
    }
  }
}
