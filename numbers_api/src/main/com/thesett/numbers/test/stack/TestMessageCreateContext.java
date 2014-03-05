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

import com.thesett.numbers.factory.NumbersFactory;
import com.thesett.numbers.message.Header;
import com.thesett.numbers.message.MessageContext;
import com.thesett.numbers.message.Numbers;

/**
 * TestMessageCreateContext provides a {@link MessageContext} to use when tests create messages. This context sets up
 * sequence numbers starting from one and incrementing on each message, and sets these up as message {@link Header}s on
 * the messages.
 *
 * <p/>The latest sequence number created by this context is provided through the {@link #getSequenceNo} method.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Sequentially number test messages from one.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestMessageCreateContext extends TestMessageContext
{
    private final NumbersFactory factory;

    /**
     * Creates a test message context using the supplied factory.
     *
     * @param factory The factory to use to create headers with.
     */
    public TestMessageCreateContext(NumbersFactory factory)
    {
        this.factory = factory;
        sequenceNo = 1;
    }

    /** {@inheritDoc} */
    public void moveToContext(Numbers message)
    {
        Header header = factory.getHeaderBuilder().setSequenceNo(sequenceNo++).build();
        message.setHeader(header);
    }
}
