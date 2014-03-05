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

/**
 * TestVisitorCommandBase, provides a base implementation for layers of a test stack, that need to capture the visit
 * actions carried out on other layers of the test stack. Each visit method has a corresponding {@link VisitCommand}
 * class, which can be used to capture the visit. The {@link VisitCommand} classes properly implement 'hashCode' and
 * 'equals', in order that identical visit calls can be checked for.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide visit commands to capture visit method calls.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class TestVisitorCommandBase extends TestVisitor
{
    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate The level of the stack below this.
     */
    public TestVisitorCommandBase(NumbersVisitor delegate)
    {
        super(delegate);
    }

    /**
     * Creates a new test visitor, with an optional delegate (the level of the stack below this).
     *
     * @param delegate        The level of the stack below this.
     * @param delegateVisits  If the delegate should visit the numbers data.
     * @param delegateApplies If the delegate should be applied.
     */
    public TestVisitorCommandBase(NumbersVisitor delegate, boolean delegateVisits, boolean delegateApplies)
    {
        super(delegate, delegateVisits, delegateApplies);
    }

    protected abstract class VisitCommand
    {
        public final int tag;

        protected VisitCommand(int tag)
        {
            this.tag = tag;
        }

        public abstract void visit(NumbersVisitor visitor);

        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (!(o instanceof VisitCommand))
            {
                return false;
            }

            VisitCommand that = (VisitCommand) o;

            if (tag != that.tag)
            {
                return false;
            }

            return true;
        }

        public int hashCode()
        {
            return tag;
        }
    }

    protected class IntegerVisitCommand extends VisitCommand
    {
        public final int value;

        public IntegerVisitCommand(int tag, int value)
        {
            super(tag);
            this.value = value;
        }

        public void visit(NumbersVisitor visitor)
        {
            visitor.visitTagValue(tag, value);
        }

        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if ((o == null) || (getClass() != o.getClass()))
            {
                return false;
            }

            if (!super.equals(o))
            {
                return false;
            }

            IntegerVisitCommand that = (IntegerVisitCommand) o;

            if (value != that.value)
            {
                return false;
            }

            return true;
        }

        public int hashCode()
        {
            int result = super.hashCode();
            result = (31 * result) + value;

            return result;
        }

        public String toString()
        {
            return "IntegerVisitCommand: [ tag = " + tag + ", value = " + value + "]";
        }
    }
}
