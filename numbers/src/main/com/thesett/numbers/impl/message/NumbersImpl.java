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
package com.thesett.numbers.impl.message;

import java.util.LinkedList;
import java.util.List;

import com.thesett.numbers.message.Header;
import com.thesett.numbers.message.Numbers;
import com.thesett.numbers.message.NumbersBuilder;
import com.thesett.numbers.message.NumbersVisitor;

/**
 * NumbersImpl is an implementation of the {@link Numbers} message format.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Append a new tag/value pair onto the message under construction.
 * <tr><td> Construct a numbers message.
 * <tr><td> Run a visitor over the list of integers.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NumbersImpl implements Numbers, NumbersBuilder
{
    /** Holds the list of numbers. */
    private final List<Integer> numbers = new LinkedList<Integer>();

    /** The message header. */
    private Header header;

    /** {@inheritDoc} */
    public NumbersBuilder append(int tag, int value)
    {
        numbers.add(tag);
        numbers.add(value);

        return this;
    }

    /** {@inheritDoc} */
    public Numbers build()
    {
        return this;
    }

    /** {@inheritDoc} */
    public void accept(NumbersVisitor visitor)
    {
        boolean lastWasTag = false;
        int tag = 0;

        for (Integer next : numbers)
        {
            if (lastWasTag)
            {
                visitor.visitTagValue(tag, next);
            }
            else
            {
                tag = next;
            }

            lastWasTag = !lastWasTag;
        }
    }

    /** {@inheritDoc} */
    public void setHeader(Header header)
    {
        this.header = header;
    }

    /** {@inheritDoc} */
    public Header getHeader()
    {
        return header;
    }

    /** {@inheritDoc} */
    public boolean isValid()
    {
        return header != null;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("NumbersImpl: [ ");

        if (header != null)
        {
            sb.append("sequenceNo = ").append(header.getSequenceNo()).append(", ");
        }

        sb.append("size = ").append(numbers.size()).append(" ]");

        return sb.toString();
    }
}
