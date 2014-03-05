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
package com.thesett.numbers.util;

public class BitsAndBytes
{
    public static final byte SOH_ASCII = 0x01;
    public static final byte EQUALS_ASCII = 0x3d;
    public static final byte ZERO_ASCII = 0x30;
    public static final byte NINE_ASCII = 0x39;
    public static final byte COLON_ASCII = 0x3a;
    public static final byte MINUS_ASCII = 0x2d;
    public static final byte DOT_ASCII = 0x2e;

    public static final int BYTE_1 = 255;
    public static final int BYTE_2 = 255 << 8;
    public static final int BYTE_3 = 255 << 16;
    public static final int BYTE_4 = 255 << 24;
    public static final int TOP_3 = BYTE_2 + BYTE_3 + BYTE_4;
    public static final int BOT_3 = BYTE_1 + BYTE_2 + BYTE_3;
    public static final int TOP_2 = BYTE_3 + BYTE_4;
    public static final int MID_2 = BYTE_2 + BYTE_3;
    public static final int BOT_2 = BYTE_1 + BYTE_2;

    public static Byte[] toBytes(int val, int count)
    {
        Byte[] result = new Byte[count];

        for (int i = 0; i < count; i++)
        {
            byte next = (byte) (val & BYTE_1);
            result[i] = next;
            val = val >> 8;
        }

        return result;
    }

    public static int fromBytes(Byte[] bytes)
    {
        int result = 0;

        for (int i = 0; i < bytes.length; i++)
        {
            result += bytes[i] << (8 * i);
        }

        return result;
    }

    public static int swapOrder(int word)
    {
        return ((word & BYTE_4) >> 24) | ((word & BYTE_3) >> 8) | ((word & BYTE_2) << 8) | ((word & BYTE_1) << 24);
    }
}
