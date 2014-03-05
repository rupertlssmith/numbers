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
package com.thesett.numbers.impl.translator;

import java.nio.ByteBuffer;

import com.thesett.numbers.message.Header;
import com.thesett.numbers.message.Numbers;
import com.thesett.numbers.message.NumbersVisitor;
import com.thesett.numbers.translator.NumbersSerializer;
import com.thesett.numbers.util.BitsAndBytes;

/**
 * Serializes a message into ASCII.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Serialize a message into a byte buffer.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NumbersSerializerImpl implements NumbersSerializer, NumbersVisitor
{
    private ByteBuffer buffer;

    /** {@inheritDoc} */
    public void setBuffer(ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    /** {@inheritDoc} */
    public void serialize(Numbers message)
    {
        // Check the message is valid.
        if (!message.isValid())
        {
            throw new IllegalArgumentException("'message' must be valid prior to invoking serialize.");
        }

        // Check for a message header, and serialize it first if there is one.
        serializeHeader(message.getHeader());
        message.accept(this);
        serializeTerminator();
    }

    /** {@inheritDoc} */
    public void visitTagValue(int tag, int val)
    {
        buffer.put(Integer.toString(tag).getBytes());
        buffer.put((byte) 1);
        buffer.put(Integer.toString(val).getBytes());
        buffer.put((byte) 1);
    }

    /**
     * Outputs the header.
     *
     * @param header The header to serialize.
     */
    private void serializeHeader(Header header)
    {
        buffer.putInt(header.getSequenceNo());
    }

    /** Outputs the terminator. */
    private void serializeTerminator()
    {
        buffer.put(BitsAndBytes.DOT_ASCII);
    }
}
