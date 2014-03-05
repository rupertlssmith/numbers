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
import com.thesett.numbers.test.stack.TestVisitorDeserializer;
import com.thesett.numbers.test.stack.TestVisitorRecorder;
import com.thesett.numbers.test.stack.TestVisitorSerializer;
import com.thesett.numbers.translator.NumbersDeserializer;
import com.thesett.numbers.translator.NumbersSerializer;

public class MessageSerDesTestBase extends TestVisitorTestBase
{
    protected NumbersFactory factory;

    /**
     * Creates the named test.
     *
     * @param name The name of the test.
     */
    public MessageSerDesTestBase(String name, NumbersFactory factory)
    {
        super(name);
        this.factory = factory;
    }

    /** {@inheritDoc} */
    protected void buildTestStack()
    {
        NumbersBuilder messageBuilder = factory.getBuilder();
        NumbersSerializer messageSerializer = factory.getSerializer();
        NumbersDeserializer messageDeserializer = factory.getDeserializer();
        TestMessageCreateContext testMessageContext = new TestMessageCreateContext(factory);

        TestVisitorChecker checker = new TestVisitorChecker(null);
        TestVisitorDeserializer deserializer = new TestVisitorDeserializer(checker, messageDeserializer);
        TestVisitorSerializer serializer = new TestVisitorSerializer(deserializer, messageSerializer);
        TestVisitorBuilder builder = new TestVisitorBuilder(serializer, false, testMessageContext);
        TestVisitorRecorder recorder = new TestVisitorRecorder(builder);
        TestVisitorAppender appender = new TestVisitorAppender(recorder, factory);

        checker.setRecorder(recorder);
        serializer.setBuilder(builder);
        builder.setAppender(appender);
        deserializer.setTestSerializer(serializer);

        testVisitor = appender;
        testChecker = checker;
        testRecorder = recorder;
    }
}
