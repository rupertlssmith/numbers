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

import com.thesett.numbers.message.Header;
import com.thesett.numbers.message.HeaderBuilder;

/**
 * HeaderImpl implements the header for the message format.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Set up message header fields.
 * <tr><td> Provide message header fields.
 * <tr><td> Construct the header from its fields.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class HeaderImpl implements Header, HeaderBuilder
{
    /** The message sequence number. */
    protected int sequenceNo;

    /** {@inheritDoc} */
    public int getSequenceNo()
    {
        return sequenceNo;
    }

    /** {@inheritDoc} */
    public HeaderBuilder setSequenceNo(int sequenceNo)
    {
        this.sequenceNo = sequenceNo;

        return this;
    }

    /** {@inheritDoc} */
    public Header build()
    {
        return this;
    }
}
