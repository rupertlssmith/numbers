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

import com.thesett.numbers.message.MessageContext;
import com.thesett.numbers.message.Numbers;
import com.thesett.numbers.message.NumbersBuilder;
import com.thesett.numbers.message.NumbersVisitor;

/**
 * TestVisitorBuilder invokes the {@link NumbersBuilder#build()} method in its pre-apply custom function.
 *
 * <p/>If a delegate is set and the 'visitMessage' flag is set, then the message is used to visit the delegate.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Build messages during pre-apply. <td> {@link NumbersBuilder}
 * <tr><td> Optionally use the built message to visit on the delegate. <td> {@link Numbers}, {@link NumbersVisitor}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestVisitorBuilder extends TestVisitor
{
    /** Indicates whether the message should be visited after it is built. */
    private final boolean visitMessage;

    /** Holds the context to place message in, after building them. */
    private final MessageContext context;

    /** Holds the last message built. */
    private Numbers numbers;

    /** The appender from which this obtains the message builder to invoke build on. */
    private TestVisitorAppender appender;

    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate     The level of the stack below this.
     * @param visitMessage If the message should visit on the delegate after being built.
     * @param context      The message context to place built messages into.
     */
    public TestVisitorBuilder(NumbersVisitor delegate, boolean visitMessage, MessageContext context)
    {
        // Do not delegate visits onto the delegate, as it will instead be visited by the message once built.
        // Do delegate applies onto the delegate, as its apply method may be used to check the results.
        super(delegate, false, true);

        this.visitMessage = visitMessage;
        this.context = context;
    }

    /** {@inheritDoc} */
    public void reset()
    {
        super.reset();
        numbers = null;
    }

    /**
     * Provides the last message built.
     *
     * @return The last message built.
     */
    public Numbers getMessage()
    {
        return numbers;
    }

    /**
     * Sets up the appender from which this obtains the message builder to invoke build on.
     *
     * @param appender The appender from which this obtains the message builder to invoke build on.
     */
    public void setAppender(TestVisitorAppender appender)
    {
        this.appender = appender;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Builds the message. If a delegate is set and the 'visitMessage' flag is set, then the message is used to
     * visit the delegate.
     */
    protected void preApply()
    {
        numbers = appender.getBuilder().build();

        context.moveToContext(numbers);

        if ((delegate != null) && visitMessage)
        {
            numbers.accept(delegate);
        }
    }
}
