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

import com.thesett.numbers.message.Numbers;

/**
 * NumbersSerializer provides the ability to 'serialize' a message into a byte buffer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Serialize a message into a byte buffer.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface NumbersSerializer
{
    /**
     * Sets up the buffer to serialize to.
     *
     * @param buffer The buffer to serialize to.
     */
    void setBuffer(ByteBuffer buffer);

    /**
     * Serializes the message to the specified buffer.
     *
     * @param message The message to serialize.
     */
    void serialize(Numbers message);
}
