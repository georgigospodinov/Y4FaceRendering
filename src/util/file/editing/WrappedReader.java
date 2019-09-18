package util.file.editing;

import util.log.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static util.PrintFormatting.NEW_LINE;

/**
 * Wraps {@link BufferedReader}, so that
 * methods can be called without having to try-catch.
 *
 * @version 2.1
 */
public class WrappedReader {

    /**
     * Reads the full contents of the specified file line by line
     * and returns them in a single {@link String}.
     *
     * @param filename the name of the file to read
     * @param l        {@link Logger} used for logging {@link IOException}s.
     *
     * @return a {@link String} containing all the lines of the file
     */
    public static String readFile(final String filename, final Logger l) {
        WrappedReader reader = new WrappedReader(filename, l);
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(line -> {
            sb.append(line);
            sb.append(NEW_LINE);
        });
        reader.close();
        return sb.toString();
    }

    /**
     * Reads the full contents of the specified file line by line
     * and returns them in a single {@link String}.
     *
     * @param filename the name of the file to read
     *
     * @return a {@link String} containing all the lines of the file
     */
    public static String readFile(final String filename) {
        WrappedReader reader = new WrappedReader(filename, null);
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(line -> {
            sb.append(line);
            sb.append(NEW_LINE);
        });
        reader.close();
        return sb.toString();
    }

    /**
     * Reads the full contents of the specified file line by line
     * and adds them to the specified {@link List}.
     *
     * @param filename the name of the file to read
     * @param list     the list to be filled
     * @param l        {@link Logger} used for logging {@link IOException}s
     */
    public static void readFile(final String filename,
                                final List<String> list,
                                final Logger l) {
        WrappedReader reader = new WrappedReader(filename, l);
        reader.lines().forEach(list::add);
        reader.close();
    }

    /**
     * Reads the full contents of the specified file line by line
     * and adds them to the specified {@link List}.
     * This method is equivalent to <code>readFile(filename, list, null)</code>.
     *
     * @param filename the name of the file to read
     * @param list     the list to be filled
     */
    public static void readFile(final String filename,
                                final List<String> list) {
        readFile(filename, list, null);
    }

    /**
     * Reads the full contents of the specified file line by line
     * and stores them in an {@link ArrayList}.
     *
     * @param filename the name of the file to read
     * @param l        {@link Logger} uesd for logging {@link IOException}s
     *
     * @return the list with all lines of the file
     */
    public static ArrayList<String> readFileLines(final String filename,
                                                  final Logger l) {
        ArrayList<String> lines = new ArrayList<>();
        readFile(filename, lines, l);
        return lines;
    }

    /**
     * Reads the full contents of the specified file line by line
     * and stores them in an {@link ArrayList}.
     * This method is equivalent to <code>readFileLines(filename, null)</code>.
     *
     * @param filename the name of the file to read
     *
     * @return the list with all lines of the file
     */
    public static ArrayList<String> readFileLines(final String filename) {
        return readFileLines(filename, null);
    }

    /** A {@link Logger} to write all errors to. */
    private Logger l;

    /** The wrapped {@link BufferedReader} instance. */
    private BufferedReader reader;

    /**
     * Opens a {@link BufferedReader} to the given filename.
     *
     * @param filename filename of input file
     * @param logger   {@link Logger} to be used for logging
     *                 {@link IOException}s.
     */
    public WrappedReader(final String filename, final Logger logger) {
        this.l = logger;
        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr;
            isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            reader = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            defaultCatch(e);
        }
    }

    /**
     * Opens a {@link BufferedReader} to the given filename.
     * {@link IOException}s will be printed to standard error.
     * This is equivalent to using
     * {@link WrappedReader#WrappedReader(String, Logger)} with null
     * as the second argument.
     *
     * @param filename filename of input file
     */
    public WrappedReader(final String filename) {
        this(filename, null);
    }

    /**
     * Opens a {@link BufferedReader} to the given {@link InputStream}.
     *
     * @param in     {@link InputStream} to read
     * @param logger {@link Logger} used for logging {@link IOException}s.
     */
    public WrappedReader(final InputStream in, final Logger logger) {
        this.l = logger;
        InputStreamReader isr;
        isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        reader = new BufferedReader(isr);
    }

    /**
     * Opens a {@link BufferedReader} to the given {@link InputStream}.
     * {@link IOException}s will be printed to standard error.
     * This is equivalent to using
     * {@link WrappedReader#WrappedReader(InputStream, Logger)} with null
     * as the second argument.
     *
     * @param in {@link InputStream} to read
     */
    public WrappedReader(final InputStream in) {
        this(in, null);
    }

    /**
     * Opens a {@link BufferedReader} to {@link System#in}.
     * This is equivalent to using
     * {@link WrappedReader#WrappedReader(InputStream, Logger)} with
     * {@link System#in} as the first argument.
     *
     * @param logger {@link Logger} to be used for logging {@link IOException}s.
     */
    public WrappedReader(final Logger logger) {
        this(System.in, logger);
    }

    /**
     * Opens a {@link BufferedReader} to {@link System#in}.
     * {@link IOException}s will be printed to standard error.
     *
     * This is equivalent to using
     * {@link WrappedReader#WrappedReader(InputStream)} with
     * {@link System#in} as the argument.
     */
    public WrappedReader() {
        this(System.in);
    }

    /**
     * The default behaviour for caught {@link Exception}s.
     *
     * @param e the caught {@link Exception}
     */
    private void defaultCatch(final Exception e) {
        if (l == null) {
            e.printStackTrace();
        } else {
            l.log(e);
        }
    }

    /**
     * Reads a line of text. A line is considered to be terminated by any
     * one of a linefeed ('\n'), a carriage return ('\r'), or
     * a carriage return followed immediately by a linefeed.
     *
     * @return A String containing the contents of the line,
     * not including any line-termination characters,
     * or null if the end of the stream has been reached
     */
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            defaultCatch(e);
        }
        return null;
    }

    /**
     * Reads a single character. (Unicode?)
     *
     * @return the character read, as an integer in the range
     * 0 to 65535 (0x00-0xffff),
     * or -1 if the end of the stream has been reached
     */
    public int read() {
        try {
            return reader.read();
        } catch (IOException e) {
            defaultCatch(e);
        }
        return -1;
    }

    /**
     * Skips characters.
     *
     * @param n The number of characters to skip
     *
     * @return The number of characters actually skipped
     */
    public long skip(final long n) {
        if (n < 0) {
            String message = "Cannot skip a negative number of characters!";
            throw new IllegalArgumentException(message);
        }

        try {
            return reader.skip(n);
        } catch (IOException e) {
            defaultCatch(e);
        }
        return n;
    }

    /**
     * Tells whether this stream is ready to be read.
     * A buffered character stream is ready if the buffer is not empty,
     * or if the underlying character stream is ready.
     *
     * @return True if the next read() is guaranteed not to block for input,
     * false otherwise. Note that returning false does not guarantee
     * that the next read will block.
     */
    public boolean ready() {
        try {
            return reader.ready();
        } catch (IOException e) {
            defaultCatch(e);
        }
        return false;
    }

    /**
     * Returns a Stream, the elements of which are lines
     * read from this {@link WrappedReader}.
     *
     * @return a Stream<String> providing the lines of text
     * described by this {@link WrappedReader}
     */
    public Stream<String> lines() {
        return reader.lines();
    }

    /**
     * Fills the specified list with the lines of the {@link WrappedReader}.
     * For each line, {@link List#add} is invoked.
     *
     * @param list the list to be filled
     */
    public void putLinesIn(final List<String> list) {
        reader.lines().forEach(list::add);
    }

    /**
     * Closes the stream and releases any system resources associated with it.
     */
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /** Resets the stream to the most recent mark. */
    public void reset() {
        try {
            reader.reset();
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /**
     * Marks the present position in the stream.
     * Subsequent calls to reset() will attempt to
     * reposition the stream to this point.
     *
     * @param readAheadLimit Limit on the number of characters that
     *                       may be read while still preserving the mark.
     *                       An attempt to reset the stream after reading
     *                       characters up to this limit or beyond may fail.
     *                       A limit value larger than the size of the
     *                       input buffer will cause a new buffer to be
     *                       allocated whose size is no smaller than limit.
     *                       Therefore large values should be used with care.
     */
    public void mark(final int readAheadLimit) {
        try {
            reader.mark(readAheadLimit);
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /**
     * Tells whether this stream supports the mark() operation, which it does.
     *
     * @return true if and only if this stream supports the mark operation.
     */
    public boolean markSupported() {
        return reader.markSupported();
    }

}
