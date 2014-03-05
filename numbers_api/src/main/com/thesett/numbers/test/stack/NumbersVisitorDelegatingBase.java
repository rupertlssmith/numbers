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
 * NumbersVisitorDelegatingBase provides a convenient base implementation of {@link NumbersVisitor}, that by default
 * passes all of the tag/values that it visits to a wrapped {@link NumbersVisitor} as a delegate, acting itself as a
 * filter.
 *
 * <p/>Implementations extending this can implement just the tag visitation methods that they need, and elect whether or
 * not to continue to pass any tags that they do read to the delegate or not. This can be extended to intercept a subset
 * of the tags visited, and to pass the remainder to the delegate.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Pass all numbers visited to a delegate.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class NumbersVisitorDelegatingBase implements NumbersVisitor
{
    /** The optional delegate. */
    protected final NumbersVisitor delegate;

    /**
     * Creates a delegating visitor, on the specified delegate.
     *
     * @param delegate The visitor to delegate all visit actions to.
     */
    public NumbersVisitorDelegatingBase(NumbersVisitor delegate)
    {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    public void visitTagValue(int tag, int val)
    {
        delegate.visitTagValue(tag, val);
    }
}
