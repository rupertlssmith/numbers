package com.thesett.numbers.chisel.lifecycle;

import com.thesett.numbers.message.MessageContext;
import com.thesett.numbers.message.Numbers;

import java.nio.ByteBuffer;

/**
 * RxTopWrapper provides an interface onto RxTop as an external process. Input frame buffers can be posted onto it
 * for processing, and it can be stepped until an end of message condition is encountered, at which time, the message
 * may be extracted.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Emulate the arrival of frame buffers. </td></tr>
 * <tr><td> Run the simulation for a single message decode. </td></tr>
 * <tr><td> Extract decoded messages. </td></tr>
 * <tr><td> Provide a context wrapper around a message.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface RxTopWrapper extends MessageContext
{
    /**
     * Adds a buffer of data as an input frame to the Rx process.
     *
     * @param buffer A frame buffer to process.
     */
    void addFrameBuffer(ByteBuffer buffer);

    /**
     * Steps the Rx process until an end of message condition is encountered, or no more input is available to read
     * from a frame buffer.
     *
     * @return <tt>true</tt> if an end of message condition was found, <tt>false</tt> if cannot continue without more
     *         input frame buffers.
     */
    boolean stepTillEndOfMessage();

    /**
     * When {@link #stepTillEndOfMessage()} returns true, this will provide the extracted message.
     *
     * <p/>As a side effect, the extracted message will be set to null, so subsequent calls do not get the same message.
     * Internally, resources used to build the message will be reset, ready to produce the next message.
     *
     * @return The extracted message iff {@link #stepTillEndOfMessage()} just returned true, or <tt>null</tt> otherwise.
     */
    public Numbers consumeMessage();
}
