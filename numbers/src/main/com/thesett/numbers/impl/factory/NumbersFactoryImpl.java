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
package com.thesett.numbers.impl.factory;

import com.thesett.numbers.factory.NumbersFactory;
import com.thesett.numbers.impl.message.HeaderImpl;
import com.thesett.numbers.impl.message.NumbersImpl;
import com.thesett.numbers.impl.translator.NumbersDeserializerImpl;
import com.thesett.numbers.impl.translator.NumbersSerializerImpl;
import com.thesett.numbers.message.HeaderBuilder;
import com.thesett.numbers.message.NumbersBuilder;
import com.thesett.numbers.translator.NumbersDeserializer;
import com.thesett.numbers.translator.NumbersSerializer;

/**
 * NumbersFactoryImpl is the default implementation of {@link NumbersFactory}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a builder for numbers messages.
 * <tr><td> Provide a serializer for numbers messages.
 * <tr><td> Provide a deserializer for the numbers messages.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NumbersFactoryImpl extends NumbersFactory
{
    /** {@inheritDoc} */
    public NumbersBuilder getBuilder()
    {
        return new NumbersImpl();
    }

    /** {@inheritDoc} */
    public HeaderBuilder getHeaderBuilder()
    {
        return new HeaderImpl();
    }

    /** {@inheritDoc} */
    public NumbersSerializer getSerializer()
    {
        return new NumbersSerializerImpl();
    }

    /** {@inheritDoc} */
    public NumbersDeserializer getDeserializer()
    {
        return new NumbersDeserializerImpl();
    }
}
