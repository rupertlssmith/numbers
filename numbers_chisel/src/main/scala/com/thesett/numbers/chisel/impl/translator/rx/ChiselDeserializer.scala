package com.thesett.numbers.chisel.impl.translator.rx

import com.thesett.numbers.translator.NumbersDeserializer
import com.thesett.numbers.message.Numbers
import java.nio.ByteBuffer

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
class ChiselDeserializer extends NumbersDeserializer
{
  def setBuffer(buffer: ByteBuffer) =
  {}

  def tryNextMessage(): Numbers =
  { null }
}
