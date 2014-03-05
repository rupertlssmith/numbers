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
package com.thesett.numbers.translator;

import java.nio.ByteBuffer;
import java.text.ParseException;

import com.thesett.numbers.message.Numbers;

/**
 * NumbersDeserializer provides the ability to deserialize a message from a byte buffer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Deserialize a message from a byte buffer.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface NumbersDeserializer
{
    /**
     * Sets up the buffer to deserialize from.
     *
     * @param buffer The buffer to deserialize from.
     */
    void setBuffer(ByteBuffer buffer);

    /**
     * Tries to deserialize a message from the buffer.
     *
     * @return The next message if one is available, or <tt>null</tt> if no complete message is available.
     *
     * @throws ParseException If an error in the message format prevents it from being parsed.
     */
    Numbers tryNextMessage() throws ParseException;
}
