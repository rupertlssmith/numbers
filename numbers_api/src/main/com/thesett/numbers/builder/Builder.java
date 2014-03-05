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
package com.thesett.numbers.builder;

/**
 * Builder is an abstraction of the builder pattern down to its simlpest form, the ability to build something.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Build an instance of the target type.
 * </table></pre>
 *
 * @param  <T> The target type that this builder constructs.
 *
 * @author Rupert Smith
 */
public interface Builder<T>
{
    /**
     * Builds an instance of the target type.
     *
     * @return An instance of the target type.
     */
    T build();
}
