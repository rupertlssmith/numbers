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

import com.thesett.numbers.factory.NumbersFactory;
import com.thesett.numbers.message.NumbersBuilder;
import com.thesett.numbers.test.stack.TestMessageCreateContext;
import com.thesett.numbers.test.stack.TestVisitorAppender;
import com.thesett.numbers.test.stack.TestVisitorBuilder;
import com.thesett.numbers.test.stack.TestVisitorChecker;
import com.thesett.numbers.test.stack.TestVisitorRecorder;

public class NumbersImplTest extends TestVisitorTestBase
{
    /**
     * Creates the named test.
     *
     * @param name The name of the test.
     */
    public NumbersImplTest(String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    protected void buildTestStack()
    {
        NumbersFactory factory = NumbersFactory.createInstance();
        TestMessageCreateContext testMessageContext = new TestMessageCreateContext(factory);

        TestVisitorChecker checker = new TestVisitorChecker(null);
        TestVisitorBuilder builder = new TestVisitorBuilder(checker, true, testMessageContext);
        TestVisitorRecorder recorder = new TestVisitorRecorder(builder);
        TestVisitorAppender appender = new TestVisitorAppender(recorder, factory);

        checker.setRecorder(recorder);
        builder.setAppender(appender);

        testVisitor = appender;
        testChecker = checker;
        testRecorder = recorder;
    }
}
