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
import java.text.ParseException;

import com.thesett.numbers.impl.message.HeaderImpl;
import com.thesett.numbers.impl.message.NumbersImpl;
import com.thesett.numbers.message.HeaderBuilder;
import com.thesett.numbers.message.Numbers;
import com.thesett.numbers.message.NumbersBuilder;
import com.thesett.numbers.translator.NumbersDeserializer;
import com.thesett.numbers.util.BitsAndBytes;
import com.thesett.util.buffer.ByteBufferUtils;
import com.thesett.util.resetable.Resetable;

/**
 * Deserializes a message from ASCII.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Deserialize a message from a byte buffer.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NumbersDeserializerImpl implements NumbersDeserializer
{
    private ByteBuffer buffer;
    private ParseState state = new ParseState();

    /** {@inheritDoc} */
    public void setBuffer(ByteBuffer buffer)
    {
        this.buffer = buffer;
        state.reset();
    }

    /** {@inheritDoc} */
    public Numbers tryNextMessage() throws ParseException
    {
        NumbersBuilder builder = new NumbersImpl();

        parseSequenceNo();

        do
        {
            parseTagValue();
            builder.append(state.tag, state.value);
        }
        while (!state.eof && !state.eom);

        if (state.eom)
        {
            state.eom = false;

            Numbers message = builder.build();
            HeaderBuilder headerBuilder = new HeaderImpl();
            headerBuilder.setSequenceNo(state.sequenceNo);
            message.setHeader(headerBuilder.build());

            return message;
        }
        else
        {
            return null;
        }
    }

    /**
     * Finds the next occurrence of a byte character in a buffer, starting at the specified position.
     *
     * @param  buffer ByteBuffer The buffer to scan.
     * @param  value  The byte to search from.
     * @param  start  The index to start searching from.
     *
     * @return The index of the byte if it is found, or -1 if it is not.
     */
    private static int indexOf(ByteBuffer buffer, byte value, int start)
    {
        int limit = buffer.limit();

        if (start >= limit)
        {
            return -1;
        }

        while ((start < limit) && (buffer.get(start) != value))
        {
            start++;
        }

        if (start == limit)
        {
            return -1;
        }

        return start;
    }

    /** Parses the first 32-bits of the message, as the sequence number in binary. */
    private void parseSequenceNo()
    {
        state.sequenceNo = buffer.getInt(state.pos);
        state.pos += 4;
    }

    private void parseTagValue() throws ParseException
    {
        state.tag = parseInteger();
        state.value = parseInteger();
    }

    private int parseInteger() throws ParseException
    {
        int nextSeparatorPos = indexOf(buffer, (byte) 0x01, state.pos);

        if (nextSeparatorPos == -1)
        {
            throw new ParseException("No more separators found after this position: " + state.pos, state.pos);
        }

        state.length = nextSeparatorPos - state.pos;

        if (state.length == 0)
        {
            throw new ParseException("Zero length value found at this position: " + state.pos, state.pos);
        }

        int result = ByteBufferUtils.parseUInt32(buffer, state.pos, state.length);
        state.pos = nextSeparatorPos + 1;

        // Check for EOM
        byte peekAhead = buffer.get(state.pos);

        if (peekAhead == BitsAndBytes.DOT_ASCII)
        {
            state.pos += 1;
            state.eom = true;
        }

        // Check for EOF
        state.eof = state.pos >= buffer.limit();

        return result;
    }

    private static class ParseState implements Resetable
    {
        public int sequenceNo = 0;
        public int pos = 0;
        public int length = 0;
        public int tag = 0;
        public int value = 0;
        public boolean eof = false;
        public boolean eom = false;

        public void reset()
        {
            sequenceNo = 0;
            pos = 0;
            length = 0;
            tag = 0;
            value = 0;
            eof = false;
            eom = false;
        }
    }
}
