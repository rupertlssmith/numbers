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

import com.thesett.numbers.message.Numbers;
import com.thesett.numbers.message.NumbersVisitor;
import com.thesett.numbers.translator.NumbersSerializer;

/**
 * TestVisitorSerializer connects to a {@link TestVisitorBuilder} to extract a message to serialize. That message is
 * serialized during the 'pre-apply' pass, and the serialized message made available in a byte buffer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Serialize a message to a byte buffer.
 *     <td> {@link TestVisitorBuilder}, {@link NumbersSerializer}, {@link Numbers}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestVisitorSerializer extends TestVisitor
{
    /** Holds the serializer to use to serialize the messages. */
    private final NumbersSerializer serializer;

    /** Holds the test message builder. */
    private TestVisitorBuilder builder;

    /** Holds the buffer with the last serialised message in it. */
    protected ByteBuffer buffer;

    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate   The level of the stack below this.
     * @param serializer The message serializer to test.
     */
    public TestVisitorSerializer(NumbersVisitor delegate, NumbersSerializer serializer)
    {
        super(delegate);
        this.serializer = serializer;
    }

    /**
     * Creates a new test visitor, with an optional delegate (the level of the stack below this).
     *
     * @param delegate        The level of the stack below this.
     * @param serializer      The message serializer to test.
     * @param delegateVisits  If the delegate should visit the numbers data.
     * @param delegateApplies If the delegate should be applied.
     */
    public TestVisitorSerializer(NumbersVisitor delegate, NumbersSerializer serializer, boolean delegateVisits,
        boolean delegateApplies)
    {
        super(delegate, delegateVisits, delegateApplies);
        this.serializer = serializer;
    }

    /** {@inheritDoc} */
    public void reset()
    {
        super.reset();
        buffer = null;
    }

    /**
     * Records the test message builder to extract the message to serialize from. This should be set up prior to
     * invoking 'apply'.
     *
     * @param builder The test message builder.
     */
    public void setBuilder(TestVisitorBuilder builder)
    {
        this.builder = builder;
    }

    /**
     * Supplies the buffer serialized to. This will be available, after 'apply' has been invoked.
     *
     * @return The buffer serialized to.
     */
    public ByteBuffer getBuffer()
    {
        return buffer;
    }

    /** {@inheritDoc} */
    protected void preApply()
    {
        Numbers message = builder.getMessage();

        // Serialise the message.
        buffer = ByteBuffer.allocate(22000);

        serializer.setBuffer(buffer);
        serializer.serialize(message);
    }
}
