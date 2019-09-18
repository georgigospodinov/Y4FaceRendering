package util.file.editing;

import util.log.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Wraps {@link BufferedWriter}, so that
 * methods can be called without having to try-catch.
 *
 * @version 1.1
 */
public class WrappedWriter {

    /**
     * Writes the given text to the specified file.
     * Instantiates a {@link WrappedWriter} with the given Logger and calls
     * {@link WrappedWriter#write(String)} on it with the given text
     * and then closes the writer.
     *
     * @param text     the text
     * @param filename the name of the file to write to
     * @param l        {@link Logger} used for logging {@link IOException}s.
     */
    public static void saveToFile(final String text,
                                  final String filename,
                                  final Logger l) {
        WrappedWriter writer = new WrappedWriter(filename, l);
        writer.write(text);
        writer.close();
    }

    /**
     * Writes the given text to the specified file.
     * Instantiates a {@link WrappedWriter} with no Logger and calls
     * {@link WrappedWriter#write(String)} on it with the given text
     * and then closes the writer.
     * This is equivalent to <code>saveToFile(text, filename, null)</code>.
     *
     * @param text     the text
     * @param filename the name of the file to write to
     */
    public static void saveToFile(final String text,
                                  final String filename) {
        saveToFile(text, filename, null);
    }

    /** A {@link Logger} to write all errors to. */
    private Logger l;

    /** The wrapped {@link BufferedWriter} instance. */
    private BufferedWriter writer;

    /**
     * Opens a {@link BufferedWriter} to the given filename.
     *
     * @param filename filename of output file
     * @param logger   {@link Logger} used for logging {@link IOException}s
     */
    public WrappedWriter(final String filename, final Logger logger) {
        this.l = logger;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /**
     * Opens a {@link BufferedWriter} to the given filename.
     * {@link IOException}s will be printed to standard error.
     * This is equivalent to using
     * {@link WrappedWriter#WrappedWriter(String, Logger)} with null
     * as the second argument.
     *
     * @param filename filename of output file
     */
    public WrappedWriter(final String filename) {
        this(filename, null);
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
     * Writes a {@link String}.
     *
     * @param s String to be written
     */
    public void write(final String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /**
     * Writes the given {@link String} and then a line separator.
     * This method is equivalent to calling {@link WrappedWriter#write(String)},
     * followed by {@link WrappedWriter#newLine()}.
     *
     * @param s String to be written
     */
    public void writeLine(final String s) {
        write(s);
        newLine();
    }

    /**
     * Writes a single character. (Unicode)
     *
     * @param c the character to be written
     */
    public void write(final int c) {
        try {
            writer.write(c);
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /**
     * Writes a line separator.
     * The line separator string is defined by
     * the system property line.separator,
     * and is not necessarily a single newline ('\n') character.
     */
    public void newLine() {
        try {
            writer.newLine();
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /** Flushes the writer. */
    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

    /** Closes the writer, flushing it first. */
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            defaultCatch(e);
        }
    }

}
