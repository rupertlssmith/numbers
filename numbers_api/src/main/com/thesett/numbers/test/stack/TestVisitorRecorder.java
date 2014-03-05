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

import java.util.LinkedList;

import com.thesett.numbers.message.NumbersVisitor;

/**
 * TestVisitorRecorder builds on the {@link TestVisitorQueuedCommandBase} to provide an in-order recording of all visit
 * method invocations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Record the last tag/value of each type seen.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestVisitorRecorder extends TestVisitorQueuedCommandBase
{
    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate The level of the stack below this.
     */
    public TestVisitorRecorder(NumbersVisitor delegate)
    {
        super(delegate, new LinkedList<VisitCommand>());
    }
}
