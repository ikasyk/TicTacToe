package me.ikasyk.util;

/**
 * Serialize message to send.
 */
public class SocketMessage {
    private String buf;
    private int lines;
    private boolean instant = false;

    /**
     * Creates a message object.
     *
     * @param buf     - text message to send.
     * @param lines   - count of lines that will be read in stream.
     * @param instant - uses when the message must be show instantly (deprecated).
     */
    public SocketMessage(String buf, int lines, boolean instant) {
        this.buf = buf;
        this.lines = lines;
        this.instant = instant;
    }

    public SocketMessage(String buf, int lines) {
        this(buf, lines, false);
    }

    public SocketMessage(String buf, boolean instant) {
        this(buf, buf.split("\r\n|\r|\n").length, instant);
    }

    public SocketMessage(String buf) {
        this(buf, buf.split("\r\n|\r|\n").length, false);
    }

    /**
     * Returns serialized message.
     *
     * @return string with count of message lines (the first byte) and message data ((lines - 1) lines).
     */
    public String get() {
        int ch = lines;
        if (instant) {
            ch |= (1 << 15);
        }
        return Character.toString((char) ch) + this.buf.trim();
    }
}
