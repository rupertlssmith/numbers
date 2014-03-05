package com.thesett.numbers.util;

import java.nio.ByteBuffer;
import java.util.Formatter;

/**
 * Some utility methods for accessing ByteBuffers as network frames.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Read a padded out 4 byte word from the frame. </td></tr>
 * <tr><td> Check if a frame contains at least 1 more byte of data. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FrameReaderUtil
{
    /**
     * Fetches the next input word from the byte buffer. When there are less than 4 bytes remaining, the higher
     * significance bytes are filled with zeros.
     *
     * @param buffer The buffer to take the next word from.
     * @return The next input word.
     */
    public static int nextWord(ByteBuffer buffer)
    {
        int nextVal = 0;

        if (buffer.remaining() >= 4) {
            nextVal = buffer.getInt();
        } else {
            if (buffer.remaining() >= 1) {
                nextVal |= buffer.get() << 24;

                if (buffer.remaining() >= 1) {
                    nextVal |= buffer.get() << 16;

                    if (buffer.remaining() >= 1) {
                        nextVal |= buffer.get() << 8;
                    }
                }
            }
        }

        // The data is posted onto the input port in network order, that is, the big endian byte is put on first.
        int nextWord = BitsAndBytes.swapOrder(nextVal);
        /*System.out.println("FrameReaderUtil: " + toDebugString(nextVal, nextWord));*/

        return nextWord;
    }

    private static String toDebugString(int nextVal, int nextWord)
    {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder);

        formatter.format("%08x", nextVal);
        builder.append(", ");
        formatter.format("%08x", nextWord);
        builder.append(" ");

        return builder.toString();
    }

    /**
     * Checks if more input words can be extracted from the byte buffer.
     *
     * @param buffer The buffer to take the next word from.
     * @return <tt>true</tt> iff more input words can be extracted from the byte buffer.
     */
    public static boolean hasNextWord(ByteBuffer buffer)
    {
        return ((buffer.remaining() > 0));
    }
}
