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

/**
 * Numbers is a message containing a list of pairs of integers. The first integer in the pair is called a 'tag' and the
 * second integer in the pair is called a 'value'.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Run a visitor over the list of integers.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Numbers
{
    /**
     * Visits the list of integers with the supplied visitor.
     *
     * @param visitor The visitor to visit the list of integers with.
     */
    void accept(NumbersVisitor visitor);

    /**
     * Associates the specified header with this message.
     *
     * @param header The message header.
     */
    void setHeader(Header header);

    /**
     * Provides the message header.
     *
     * @return The message header, or <tt>null</tt> if it does not have one.
     */
    Header getHeader();

    /**
     * Indicates that this message is complete and valid. The meaning of the term 'valid' may depend on context, for
     * example, there may be various levels of message validation. At the very least, it means that the message is
     * complete (with a header), and can be transformed into its serialized form.
     *
     * @return <tt>true</tt> iff the message is 'valid'.
     */
    boolean isValid();
}
