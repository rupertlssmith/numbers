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

import com.thesett.numbers.factory.NumbersFactory;
import com.thesett.numbers.message.NumbersBuilder;
import com.thesett.numbers.message.NumbersVisitor;

/**
 * TestVisitorAppender is a test stack layer, that maps the visit tag/value method onto appending into a message
 * builder.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Append visited tag/value pairs into a message builder.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestVisitorAppender extends TestVisitor
{
    /** The factory to use to create message builders. */
    private final NumbersFactory factory;

    /** The builder to append to. */
    private NumbersBuilder builder;

    /**
     * Creates a new test visitor, with an optional delegate (the level of the stack below this).
     *
     * @param delegate The level of the stack below this.
     * @param factory  The factory to use to create message builders.
     */
    public TestVisitorAppender(NumbersVisitor delegate, NumbersFactory factory)
    {
        super(delegate);

        this.factory = factory;
    }

    /** {@inheritDoc} */
    public void reset()
    {
        super.reset();
        builder = null;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Appends the tag/value onto the message builder.
     */
    public void visitTagValue(int tag, int val)
    {
        if (builder == null)
        {
            builder = factory.getBuilder();
        }

        builder.append(tag, val);
        super.visitTagValue(tag, val);
    }

    /**
     * Provides the builder in which the message is being assembled.
     *
     * @return The builder in which the message is being assembled.
     */
    public NumbersBuilder getBuilder()
    {
        return builder;
    }
}
