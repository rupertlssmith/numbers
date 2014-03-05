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
package com.thesett.numbers.message;

import com.thesett.numbers.builder.Builder;

/**
 * NumbersBuilder is a builder for {@link Numbers} messages.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Append a new tag/value pair onto the message under construction.
 * <tr><td> Construct a numbers message.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface NumbersBuilder extends Builder<Numbers>
{
    /**
     * Adds a new tag/value pair to the numbers message being constructed.
     *
     * @param  tag   The tag to append.
     * @param  value The value to append.
     *
     * @return This builder, for continuation.
     */
    NumbersBuilder append(int tag, int value);
}
