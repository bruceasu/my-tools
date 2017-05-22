package asu.tool.util;

/**
 * code from apache
 *
 *  Thrown when there is a failure condition during the decoding process. This exception is thrown when a
 * encounters a decoding specific exception such as invalid data, or characters outside of the expected range.
 *
 * @author Apache Software Foundation
 * @version $Id: DecoderException.java 1157192 2011-08-12 17:27:38Z ggregory $
 */
public class DecoderException extends Exception {
    private static final long serialVersionUID = 1L;

    public DecoderException() {
    }

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecoderException(Throwable cause) {
        super(cause);
    }
}
