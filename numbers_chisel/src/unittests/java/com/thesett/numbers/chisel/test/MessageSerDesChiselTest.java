package com.thesett.numbers.chisel.test;

import com.thesett.numbers.chisel.lifecycle.ChiselNumbersLifecycle;
import com.thesett.numbers.test.MessageSerDesTestBase;

public class MessageSerDesChiselTest extends MessageSerDesTestBase
{
    private ChiselNumbersLifecycle lifecycle;

    /**
     * Creates the named test.
     *
     * @param name The name of the test.
     */
    public MessageSerDesChiselTest(String name)
    {
        super(name, null);
    }

    protected void setUp()
    {
        lifecycle = new ChiselNumbersLifecycle(false);
        lifecycle.start();

        factory = lifecycle.getNumbersFactory();

        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        lifecycle.shutdown();
    }
}
