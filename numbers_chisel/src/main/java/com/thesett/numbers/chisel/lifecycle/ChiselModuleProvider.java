package com.thesett.numbers.chisel.lifecycle;

/**
 * ChiselModuleProvider provides Chisel module simulation implementations for messaging.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide Chisel module simulations.
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ChiselModuleProvider
{
    /**
     * Provides access to the RX module.
     *
     * @return The RX module.
     */
    RxTopWrapper getRxTop();
}
