package com.thesett.numbers.chisel.impl.translator;

import com.thesett.numbers.chisel.lifecycle.ChiselModuleProvider;
import com.thesett.numbers.chisel.lifecycle.RxTopWrapper;
import com.thesett.numbers.message.Numbers;
import com.thesett.numbers.translator.NumbersDeserializer;

import java.nio.ByteBuffer;
import java.text.ParseException;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ChiselDeserializer implements NumbersDeserializer
{
    private final ChiselModuleProvider moduleProvider;
    private ByteBuffer buffer;

    public ChiselDeserializer(ChiselModuleProvider moduleProvider)
    {
        this.moduleProvider = moduleProvider;
    }

    /** {@inheritDoc} */
    public void setBuffer(ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    /** {@inheritDoc} */
    public Numbers tryNextMessage() throws ParseException
    {
        RxTopWrapper rxTop = moduleProvider.getRxTop();
        rxTop.addFrameBuffer(buffer);
        rxTop.stepTillEndOfMessage();

        return rxTop.consumeMessage();
    }
}
