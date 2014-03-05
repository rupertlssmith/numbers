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
 * NumbersVisitor is a visitor over {@link Numbers}, that can be presented with the list of integers in the order in
 * which they are found in the message.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Visit the next integer in the numbers list.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface NumbersVisitor
{
    /**
     * Presents the next integer in the numbers list.
     *
     * @param val The next integer in the numbers list.
     */
    void visitTagValue(int tag, int val);
}
