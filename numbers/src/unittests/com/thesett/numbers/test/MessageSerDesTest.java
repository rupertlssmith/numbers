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
package com.thesett.numbers.test;

import com.thesett.numbers.factory.NumbersFactory;

public class MessageSerDesTest extends MessageSerDesTestBase
{
    /**
     * Creates the named test.
     *
     * @param name The name of the test.
     */
    public MessageSerDesTest(String name)
    {
        super(name, NumbersFactory.createInstance());
    }
}
