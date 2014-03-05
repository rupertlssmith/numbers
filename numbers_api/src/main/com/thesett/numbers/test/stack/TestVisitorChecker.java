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
import java.util.logging.Logger;

import junit.framework.Assert;

import com.thesett.numbers.message.NumbersVisitor;

/**
 * TestVisitorChecker builds on {@link TestVisitorRecorder}, to add additional post-apply functionality. A recorder must
 * be supplied to the checker, through the {@link #setRecorder(TestVisitorRecorder)} method. During the post-apply pass,
 * the set of values captured by this checker, are compared with an original set of values captured by the recorder.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Record the last tag/value of each type seen.
 * <tr><td> Compare values seen, with an original set of values captured by a recorder. <td> {@link TestVisitorRecorder}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestVisitorChecker extends TestVisitorRecorder
{
    /** Used for debugging purposes. */
    /*private static final Logger log = Logger.getLogger(TestVisitorChecker.class.getName());*/

    /** The recorder of an original set of values to compare with. */
    TestVisitorRecorder recorder;

    /**
     * Creates a new test visitor, with an optional delegate. The delegate is always visited and applied, if set.
     *
     * @param delegate The level of the stack below this.
     */
    public TestVisitorChecker(NumbersVisitor delegate)
    {
        super(delegate);
    }

    /**
     * Sets up the {@link TestVisitorRecorder} that this checker compares itself with, in order to validate results.
     *
     * @param recorder The recorder of the original values to compare with.
     */
    public void setRecorder(TestVisitorRecorder recorder)
    {
        this.recorder = recorder;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Compares the queue of captured visits to those captured by the recorder.
     */
    public void postApply()
    {
        if (recorder == null)
        {
            throw new IllegalStateException("'recorder' was not set up using 'setRecorder()' prior to invoking apply.");
        }

        while (!recorder.commandQueue.isEmpty())
        {
            VisitCommand originalCommand = recorder.commandQueue.poll();
            VisitCommand compareCommand = commandQueue.poll();

            /*System.out.println("originalCommand = " + originalCommand);
            System.out.println("compareCommand = " + compareCommand);*/

            if (compareCommand == null)
            {
                Assert.fail("The original command queue has a value '" + originalCommand +
                    "', but the checker does not.");
            }

            Assert.assertEquals("Original value and compare to value are different.", originalCommand, compareCommand);
        }
    }

    /**
     * Used for debugging purposes only.
     *
     * @param message      A message to print.
     * @param commandQueue A queue of visit commands to print.
     */
    private void printCommands(String message, Queue<VisitCommand> commandQueue)
    {
        for (VisitCommand command : commandQueue)
        {
            /*log.fine(message + command);*/
            System.out.println(message + command);
        }
    }
}
