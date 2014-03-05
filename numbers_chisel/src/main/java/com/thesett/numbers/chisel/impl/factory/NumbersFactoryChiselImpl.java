package com.thesett.numbers.chisel.impl.factory;

import com.thesett.numbers.chisel.impl.translator.ChiselDeserializer;
import com.thesett.numbers.chisel.lifecycle.ChiselModuleProvider;
import com.thesett.numbers.factory.NumbersFactory;
import com.thesett.numbers.impl.factory.NumbersFactoryImpl;
import com.thesett.numbers.translator.NumbersDeserializer;

/**
 * NumbersFactoryChiselImpl is an implementation of {@link NumbersFactory}, that uses hardware simulations to implement
 * message translation.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a deserializer for the numbers messages.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NumbersFactoryChiselImpl extends NumbersFactoryImpl
{
    private ChiselModuleProvider moduleProvider;

    public NumbersFactoryChiselImpl(ChiselModuleProvider moduleProvider)
    {
        this.moduleProvider = moduleProvider;
    }

    /** {@inheritDoc} */
    public NumbersDeserializer getDeserializer()
    {
        return new ChiselDeserializer(moduleProvider);
    }
}
