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
 * HeaderBuilder is a builder for message {@link Header}s.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Set up message header fields.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface HeaderBuilder extends Builder<Header>
{
    /**
     * Sets the message sequence number.
     *
     * @param  sequenceNo The message sequence number.
     *
     * @return This builder, for continuation.
     */
    HeaderBuilder setSequenceNo(int sequenceNo);
}
