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
 * MessageContext abstractly defines a context in which a message is being used. For example, a message may be sent over
 * a channel, received on a channel, placed in a message store, used within a test framework, and so on. Each context
 * may interpret, extend or annotate the underlying message in its own way.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide a context wrapper around a message.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface MessageContext
{
    /**
     * Places the specified message within this context.
     *
     * @param message The message to interpret in this context.
     */
    void moveToContext(Numbers message);
}
