/*
 * © Copyright Rupert Smith, 2005 to 2013.
 *
 * ALL RIGHTS RESERVED. Any unauthorized reproduction or use of this
 * material is prohibited. No part of this work may be reproduced or
 * transmitted in any form or by any means, electronic or mechanical,
 * including photocopying, recording, or by any information storage
 * and retrieval system without express written permission from the
 * author.
 */
package com.thesett.numbers.test.stack;

import java.nio.ByteBuffer;
import java.text.ParseException;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import com.thesett.numbers.message.Numbers;
import com.thesett.numbers.message.NumbersVisitor;
import com.thesett.numbers.translator.NumbersDeserializer;
import com.thesett.util.debug.DebugPrintUtils;

/**
 * TestVisitorDeserializer connects to a {@link TestVisitorSerializer}, from which it obtains a byte buffer containing a
 * serialized message. This is deserialized back into a message during the 'pre-apply' pass. If a delegate is set up on
 * this, the message is then visited on to the delegate.
 *
 * <p/>This test pass will check that the message is deserialized, that is, the deserializer does not return null.
 * Further checking of the message contents is left up to the delegate.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestVisitorDeserializer extends TestVisitor
{
    /** Holds the deserializer to use to deserialize the messages. */
    private final NumbersDeserializer deserializer;

    /** Holds the test message serializer. */
    private TestVisitorSerializer testSerializer;

    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate     The level of the stack below this.
     * @param deserializer The deserializer to test.
     */
    public TestVisitorDeserializer(NumbersVisitor delegate, NumbersDeserializer deserializer)
    {
        super(delegate);

        this.deserializer = deserializer;
    }

    /**
     * Creates a new test visitor, with an optional delegate (the level of the stack below this).
     *
     * @param delegate        The level of the stack below this.
     * @param deserializer    The deserializer to test.
     * @param delegateVisits  If the delegate should visit the numbers data.
     * @param delegateApplies If the delegate should be applied.
     */
    public TestVisitorDeserializer(NumbersVisitor delegate, NumbersDeserializer deserializer, boolean delegateVisits,
        boolean delegateApplies)
    {
        super(delegate, delegateVisits, delegateApplies);

        this.deserializer = deserializer;
    }

    /**
     * Records the test message serializer.
     *
     * @param testSerializer The test message serializer.
     */
    public void setTestSerializer(TestVisitorSerializer testSerializer)
    {
        this.testSerializer = testSerializer;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Deserializes a message then visits it with the delegate.
     */
    protected void preApply()
    {
        // Get the buffer to deserializer.
        ByteBuffer buffer = testSerializer.getBuffer();
        buffer.flip();

        //System.out.println(DebugPrintUtils.printBuffer(buffer, 100));

        // Deserialize the message.
        deserializer.setBuffer(buffer);

        Numbers message;

        try
        {
            message = deserializer.tryNextMessage();
            //System.out.println(message);
        }
        catch (ParseException e)
        {
            AssertionFailedError error = new AssertionFailedError("Deserializer encountered a parsing error.");
            error.initCause(e);

            throw error;
        }

        Assert.assertNotNull("Deserializer returned 'null' message.", message);

        if (delegate != null)
        {
            message.accept(delegate);
        }
    }
}
