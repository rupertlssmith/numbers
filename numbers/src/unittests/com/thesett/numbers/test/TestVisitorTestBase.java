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
package com.thesett.numbers.test;

import java.util.Random;

import junit.framework.TestCase;

import com.thesett.numbers.message.NumbersVisitor;
import com.thesett.numbers.test.stack.TestVisitor;
import com.thesett.numbers.test.stack.TestVisitorChecker;
import com.thesett.numbers.test.stack.TestVisitorRecorder;

/**
 * TestVisitorTestBase provides a standard test suite for running against any {@link TestVisitor} based test stack.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class TestVisitorTestBase extends TestCase
{
    private static final Random RANDOM = new Random();

    /** A test visitor used to coordinate the whole test. */
    protected TestVisitor testVisitor;

    /** A test visitor used to check expected value read from the message. */
    protected TestVisitorChecker testChecker;

    /** A test recorder used to record the values written into the message. */
    protected TestVisitorRecorder testRecorder;

    /**
     * Creates the named test.
     *
     * @param name The name of the test.
     */
    public TestVisitorTestBase(String name)
    {
        super(name);
    }

    public void testAppendVisitOk()
    {
        for (int i = 0; i < 100; i++)
        {
            appendNRandom(testVisitor, 20);
            /*System.out.println("=== Test message: " + i);*/
            testVisitor.apply();
            testVisitor.reset();
        }
    }

    /**
     * Creates a test stack made of {@link TestVisitor}s composed together. This method must set up the
     * {@link #testRecorder}, {@link #testChecker}, and {@link #testVisitor} fields.
     */
    protected abstract void buildTestStack();

    /** Resets the test value recorder and checkers. */
    protected void setUp()
    {
        buildTestStack();
    }

    private void appendNRandom(NumbersVisitor visitor, int n)
    {
        for (int i = 0; i < n; i++)
        {
            int tagMaxLength = RANDOM.nextInt(4) + 1;
            int valMaxLength = RANDOM.nextInt(9) + 1;
            int tagMax = (int) Math.pow(10, tagMaxLength);
            int valMax = (int) Math.pow(10, valMaxLength);
            visitor.visitTagValue(RANDOM.nextInt(tagMax), RANDOM.nextInt(valMax));
        }
    }
}
