package util;

import sun.security.action.GetPropertyAction;

import java.security.AccessController;
import java.util.Collection;

/**
 * Provides a method to "pretty-print" multiple objects.
 *
 * @version 2.1
 */
public final class PrintFormatting {

    /** A reference to the system-specific line separator. */
    public static final String NEW_LINE = AccessController.doPrivileged(
            new GetPropertyAction("line.separator"));

    /** A constant String reference ot a comma. */
    public static final String SEPARATOR = ",";

    /**
     * Prints all objects to standard output.
     * Each object is printed on a separate line.
     * If a given object is an array, its elements are printed on separately
     * and are indented with tabs.
     * Elements of nested arrays are indented multiple times.
     *
     * @param objects the objects to print
     */
    public static void print(final Object... objects) {
        for (Object o : objects) {
            print(o, 0);
        }
    }

    /**
     * Prints the specified number of tab characters to standard output.
     * This method is used to indent the elements of arrays that are to be
     * printed.
     *
     * @param numberOfTabs the number of tabs to print
     */
    private static void printTabs(final int numberOfTabs) {
        for (int i = 0; i < numberOfTabs; i++) {
            System.out.print("\t");
        }
    }

    /**
     * Prints the given array to standard output.
     * Each element is indented with
     * a number of tabs equal to the nesting level.
     *
     * @param arr          the array whose elements are to be printed
     * @param nestingLevel the nesting level of the array
     */
    private static void printArray(final Object[] arr, final int nestingLevel) {
        printTabs(nestingLevel - 1);
        print("[");

        for (Object o : arr) {
            print(o, nestingLevel);
        }

        printTabs(nestingLevel - 1);
        print("]");
    }

    /**
     * Prints the given object to standard output.
     * The object is indented with a number of tabs equal to the nesting level.
     *
     * @param o            the object to print
     * @param nestingLevel the nesting level of the object
     */
    private static void print(final Object o, final int nestingLevel) {
        if (o instanceof int[]) {
            int[] x = ((int[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) {
                objs[i] = x[i];
            }
            printArray(objs, nestingLevel + 1);
        } else if (o instanceof double[]) {
            double[] x = ((double[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) {
                objs[i] = x[i];
            }
            printArray(objs, nestingLevel + 1);
        } else if (o instanceof char[]) {
            char[] x = ((char[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) {
                objs[i] = x[i];
            }
            printArray(objs, nestingLevel + 1);
        } else if (o instanceof float[]) {
            float[] x = ((float[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) {
                objs[i] = x[i];
            }
            printArray(objs, nestingLevel + 1);
        } else if (o instanceof boolean[]) {
            boolean[] x = ((boolean[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) {
                objs[i] = x[i];
            }
            printArray(objs, nestingLevel + 1);
        } else if (o instanceof byte[]) {
            byte[] x = ((byte[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) {
                objs[i] = x[i];
            }
            printArray(objs, nestingLevel + 1);
        } else if (o instanceof Object[]) {
            printArray(((Object[]) o), nestingLevel + 1);
        } else if (o instanceof Collection) {
            printArray(((Collection) o).toArray(), nestingLevel + 1);
        } else {
            printTabs(nestingLevel);
            System.out.println(o);
        }
    }

    /** Hides the default constructor for this utility class. */
    private PrintFormatting() {
    }

}
