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

import com.thesett.numbers.message.NumbersVisitor;
import com.thesett.util.resetable.Resetable;

/**
 * TestVisitor extends {@link NumbersVisitor} with the ability to form a stack of visitors, and to {@link #apply()} some
 * custom function at each level of the stack. When the stack is 'applied', a pass is made down and back up the entire
 * stack starting at the level at which the apply call is made. Custom functionality can be added either on the way down
 * the stack, or on the way back up, by implemented the {@link #preApply()} or {@link #postApply()} methods as
 * appropriate.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Pass all numbers visited to an optional delegate. <td> {@link NumbersVisitorDelegatingBase}
 * <tr><td> Add custom functionality to a stack of of numbers visitors. <td> {@link NumbersVisitor}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class TestVisitor extends NumbersVisitorDelegatingBase implements Resetable
{
    /** <tt>true</tt> iff the level of the stack below should be visited. */
    private final boolean delegateVisits;

    /** <tt>true</tt> iff the level of the stack below should be applied. */
    private final boolean delegateApplies;

    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate The level of the stack below this.
     */
    public TestVisitor(NumbersVisitor delegate)
    {
        this(delegate, true, true);
    }

    /**
     * Creates a new test visitor, with an optional delegate (the level of the stack below this).
     *
     * @param delegate        The level of the stack below this.
     * @param delegateVisits  If the delegate should visit the numbers data.
     * @param delegateApplies If the delegate should be applied.
     */
    public TestVisitor(NumbersVisitor delegate, boolean delegateVisits, boolean delegateApplies)
    {
        super(delegate);
        this.delegateVisits = delegateVisits;
        this.delegateApplies = delegateApplies;
    }

    /** {@inheritDoc} */
    public void reset()
    {
        if ((delegate != null) && (delegate instanceof Resetable))
        {
            ((Resetable) delegate).reset();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Delegates the visit, iff a delegate is set, and the 'delegateVisits' flag is set.
     */
    public void visitTagValue(int tag, int val)
    {
        if ((delegate != null) && delegateVisits)
        {
            delegate.visitTagValue(tag, val);
        }
    }

    /**
     * Invoked the custom apply functionality on the stack below this point.
     *
     * <p/>{@link #preApply()} is invoked, then the layer below is applied, and {@link #postApply()} is invoked after
     * that. This means that the pre-apply calls are invoked top-to-bottom working down the stack, and the post-apply
     * calls are invoked bottom-to-top working back up the stack.
     */
    public final void apply()
    {
        preApply();
        delegateApply();
        postApply();
    }

    /** Implement to provide a custom pre-apply function. */
    protected void preApply()
    {
    }

    /** Implement to provide a custom post-apply function. */
    protected void postApply()
    {
    }

    /**
     * Applies to the level of the stack below this, providing a delegate is set, the {@link #delegateApplies} flag is
     * set, and the delegate is also a TestVisitor.
     */
    private void delegateApply()
    {
        if ((delegate != null) && delegateApplies && (delegate instanceof TestVisitor))
        {
            TestVisitor testVisitor = (TestVisitor) delegate;
            testVisitor.apply();
        }
    }
}
