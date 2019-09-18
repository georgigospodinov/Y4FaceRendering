package util.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.BiConsumer;

import static util.PrintFormatting.NEW_LINE;

/**
 * Provides reading of ".props" files.
 *
 * @version 2.4
 */
public class BasicProps {

    /** The name of the default properties file. */
    private static final String DEFAULT_PROPS_FILE = "default.props";

    /** The symbol used to separate keys from values in the properties file. */
    private static final String KEY_VALUE_SEPARATOR = "=";

    /** The symbol that indicates a comment in the properties file. */
    private static final String COMMENT_SYMBOL = "#";

    /** The key index in the raw array of the key-value pair. */
    private static final int KEY_INDEX = 0;

    /** The value index in the raw array of the key-value pair. */
    private static final int VALUE_INDEX = 1;

    /** Stores properties that have {@link String} values. */
    private final HashMap<String, String> stringProperties = new HashMap<>();

    /** Stores properties that have {@link Long} values. */
    private final HashMap<String, Long> longProperties = new HashMap<>();

    /** Stores properties that have {@link Integer} values. */
    private final HashMap<String, Integer> intProperties = new HashMap<>();

    /** Stores properties that have {@link Double} values. */
    private final HashMap<String, Double> doubleProperties = new HashMap<>();

    /** Stores properties that have {@link Float} values. */
    private final HashMap<String, Float> floatProperties = new HashMap<>();

    /** Default constructor. A file should be loaded immediately. */
    public BasicProps() {
    }

    /**
     * Creates a {@link BasicProps} instance and
     * loads the properties from the specified file.
     *
     * @param filename the name of the properties file to load
     *
     * @throws FileNotFoundException if the specified file is not found
     */
    public BasicProps(final String filename) throws FileNotFoundException {
        load(filename);
    }

    /**
     * Gives the number of properties currently in storage.
     *
     * @return the current number of properties
     */
    public int size() {
        int ss = stringProperties.size();
        int is = intProperties.size();
        int ls = longProperties.size();
        int ds = doubleProperties.size();
        int fs = floatProperties.size();
        return ss + is + ls + ds + fs;
    }

    /** Clears all the loaded properties. */
    public void clear() {
        stringProperties.clear();
        intProperties.clear();
        longProperties.clear();
        floatProperties.clear();
        doubleProperties.clear();
    }

    /**
     * Get the {@link Integer} value associated with the given property.
     *
     * @param property the property to look for
     *
     * @return the integer value associated with that property
     */
    public int getInt(final String property) {
        if (!intProperties.containsKey(property)) {
            String message = "No integer property \"" + property + "\"";
            throw new NullPointerException(message);
        }

        return intProperties.get(property);
    }

    /**
     * Get the {@link Long} value associated with the given property.
     * This method will check for an integer value if no long value is found.
     *
     * @param property the property to look for
     *
     * @return the long value associated with that property
     */
    public long getLong(final String property) {
        if (!longProperties.containsKey(property)) {
            if (!intProperties.containsKey(property)) {
                String message = "No long property \"" + property + "\"";
                throw new NullPointerException(message);
            }
            return intProperties.get(property);
        }

        return longProperties.get(property);
    }

    /**
     * Get the {@link Float} value associated with the given property.
     *
     * @param property the property to look for
     *
     * @return the double value associated with that property
     */
    public float getFloat(final String property) {
        if (!floatProperties.containsKey(property)) {
            String message = "No float property \"" + property + "\"";
            throw new NullPointerException(message);
        }

        return floatProperties.get(property);
    }

    /**
     * Get the {@link Double} value associated with the given property.
     * This method will check for a float value if no double value is found.
     *
     * @param property the property to look for
     *
     * @return the long value associated with that property
     */
    public double getDouble(final String property) {
        if (!doubleProperties.containsKey(property)) {
            if (!floatProperties.containsKey(property)) {
                String message = "No double property \"" + property + "\"";
                throw new NullPointerException(message);
            }
            return floatProperties.get(property);
        }

        return doubleProperties.get(property);
    }

    /**
     * Get the number associated with the given property.
     * This method checks for an {@link Integer}, {@link Long}, {@link Float},
     * and {@link Double} in this order.
     * Once found, it is cast to integer and returned.
     *
     * @param property the property to look for
     *
     * @return the value associated with that property
     * after being cast to integer
     */
    public int getAnyInt(final String property) {
        if (intProperties.containsKey(property)) {
            return intProperties.get(property);
        }

        if (longProperties.containsKey(property)) {
            return longProperties.get(property).intValue();
        }

        if (floatProperties.containsKey(property)) {
            return floatProperties.get(property).intValue();
        }

        if (doubleProperties.containsKey(property)) {
            return doubleProperties.get(property).intValue();
        }

        String message = "No number property \"" + property + "\"";
        throw new NullPointerException(message);
    }

    /**
     * Checks if the {@link String} associated with the given property
     * is equal to "true".
     *
     * @param property the property to look for
     *
     * @return true if and only if the {@link String}
     * associated with this property is equal to "true".
     */
    public boolean isTrue(final String property) {
        if (!stringProperties.containsKey(property)) {
            String message = "No string property \"" + property + "\"";
            throw new NullPointerException(message);
        }

        return stringProperties.get(property).equals("true");
    }

    /**
     * Get the {@link String} value associated with the given property.
     *
     * @param property the property to look for
     *
     * @return the string value associated with that property
     */
    public String getString(final String property) {
        if (!stringProperties.containsKey(property)) {
            String message = "No string property \"" + property + "\"";
            throw new NullPointerException(message);
        }

        return stringProperties.get(property);
    }

    /**
     * Loads the default properties file.
     *
     * @throws FileNotFoundException if the default properties file is not found
     * @see BasicProps#DEFAULT_PROPS_FILE
     */
    public void load() throws FileNotFoundException {
        load(DEFAULT_PROPS_FILE);
    }

    /**
     * Load properties from the specified file.
     *
     * @param filename the name of the file containing properties.
     *                 This can be absolute or relative path.
     *
     * @throws FileNotFoundException if there is no file with the given name
     */
    public void load(final String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename), "utf-8");
        scanner.useDelimiter(NEW_LINE);
        String line;
        do {
            try {
                line = scanner.nextLine();
                loadLine(line);
            } catch (NoSuchElementException e) {
                break;
            }
        } while (true);
    }

    /**
     * Parses the specified line as a key-value property.
     * The line should contain two Strings separated by the
     * {@link BasicProps#KEY_VALUE_SEPARATOR}.
     * If the separator appears multiple times in the line,
     * the string before the first separator will be used as key
     * and the string between the first and second separator - as value.
     *
     * @param line the line to parse
     */
    private void loadLine(final String line) {
        // Skip empty lines and comments
        if (line.isEmpty() || line.startsWith(COMMENT_SYMBOL)) {
            return;
        }

        String[] kv = line.split(KEY_VALUE_SEPARATOR);
        String key = kv[KEY_INDEX];
        String val = kv[VALUE_INDEX];

        try {  // Is this an integer?
            int value = Integer.parseInt(val);
            intProperties.put(key, value);
            return;
        } catch (NumberFormatException ignored) {
        }

        try {  // Is this a long?
            long value = Long.parseLong(val);
            longProperties.put(key, value);
            return;
        } catch (NumberFormatException ignored) {
        }

        try {  // Is this a float?
            float value = Float.parseFloat(val);
            if (value == Float.POSITIVE_INFINITY) {
                throw new NumberFormatException();
            }
            if (value == Float.NEGATIVE_INFINITY) {
                throw new NumberFormatException();
            }
            floatProperties.put(key, value);
            return;
        } catch (NumberFormatException ignored) {
        }

        try {  // Is this a double?
            double value = Double.parseDouble(val);
            doubleProperties.put(key, value);
            return;
        } catch (NumberFormatException ignored) {
        }

        stringProperties.put(key, val);

    }

    /**
     * Applies the specified operation to each {@link Integer} property.
     * The operation must accept a {@link String} (the key)
     * and an {@link Integer} (the value).
     *
     * @param operation the operation to apply
     */
    public void forEachInteger(final BiConsumer<String, Integer> operation) {
        intProperties.forEach(operation);
    }

    /**
     * Applies the specified operation to each {@link String} property.
     * The operation must accept a {@link String} (the key)
     * and a {@link String} (the value).
     *
     * @param operation the operation to apply
     */
    public void forEachString(final BiConsumer<String, String> operation) {
        stringProperties.forEach(operation);
    }

}
