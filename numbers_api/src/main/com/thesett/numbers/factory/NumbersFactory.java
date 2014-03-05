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
package com.thesett.numbers.factory;

import com.thesett.numbers.message.HeaderBuilder;
import com.thesett.numbers.message.NumbersBuilder;
import com.thesett.numbers.translator.NumbersDeserializer;
import com.thesett.numbers.translator.NumbersSerializer;
import com.thesett.util.reflection.ReflectionUtils;

/**
 * NumbersFactory provides the ability to create {@link NumbersBuilder}s in order to work with Numbers messages.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide a builder for numbers messages.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class NumbersFactory
{
    /** The full name of the numbers builder implementing class. */
    private static final String NUMBERS_FACTORY_CLASS = "com.thesett.numbers.impl.factory.NumbersFactoryImpl";

    /**
     * Provides a builder for numbers messages.
     *
     * @return A builder for numbers messages.
     */
    public abstract NumbersBuilder getBuilder();

    /**
     * Provides a builder for message headers.
     *
     * @return A builder for message headers.
     */
    public abstract HeaderBuilder getHeaderBuilder();

    /**
     * Provides a message serializer.
     *
     * @return A message serializer.
     */
    public abstract NumbersSerializer getSerializer();

    /**
     * Provides a message deserializer.
     *
     * @return A message deserializer.
     */
    public abstract NumbersDeserializer getDeserializer();

    /**
     * Creates an instance of this factory. Note, this is not a singleton instance, a new one is created each time this
     * is invoked.
     *
     * @return An instance of this factory.
     */
    public static NumbersFactory createInstance()
    {
        return (NumbersFactory) ReflectionUtils.newInstance(ReflectionUtils.forName(NUMBERS_FACTORY_CLASS));
    }
}
