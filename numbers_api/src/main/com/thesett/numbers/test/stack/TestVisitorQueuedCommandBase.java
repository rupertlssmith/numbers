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

import java.util.Queue;

import com.thesett.numbers.message.NumbersVisitor;

/**
 * TestVisitorQueuedCommandBase, provides a base implementation for layers of a test stack, that need to capture the
 * visit actions carried out on other layers of the test stack. Each visit method invocation is captured on a command
 * queue.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Capture all visits on a command queue.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestVisitorQueuedCommandBase extends TestVisitorCommandBase
{
    /** A queue of all the visit method invocations. */
    protected final Queue<VisitCommand> commandQueue;

    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate     The level of the stack below this.
     * @param commandQueue The queue implementation.
     */
    public TestVisitorQueuedCommandBase(NumbersVisitor delegate, Queue<VisitCommand> commandQueue)
    {
        super(delegate);
        this.commandQueue = commandQueue;
    }

    /**
     * Creates a new test visitor, with an optional delegate (the level of the stack below this).
     *
     * @param delegate        The level of the stack below this.
     * @param commandQueue    The queue implementation.
     * @param delegateVisits  If the delegate should visit the numbers data.
     * @param delegateApplies If the delegate should be applied.
     */
    public TestVisitorQueuedCommandBase(NumbersVisitor delegate, Queue<VisitCommand> commandQueue,
        boolean delegateVisits, boolean delegateApplies)
    {
        super(delegate, delegateVisits, delegateApplies);
        this.commandQueue = commandQueue;
    }

    /** {@inheritDoc} */
    public void reset()
    {
        super.reset();
        commandQueue.clear();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Records the visit in the command queue.
     */
    public void visitTagValue(int tag, int val)
    {
        commandQueue.offer(new IntegerVisitCommand(tag, val));
        super.visitTagValue(tag, val);
    }
}
