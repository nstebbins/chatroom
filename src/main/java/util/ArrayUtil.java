package util;

import java.util.Arrays;

public class ArrayUtil {

    /**
     * subset from start index to length - 1 of array
     * @param arr array
     * @param start start index for subset
     * @return
     */
    public static String joinArraySubsetBySpace(String[] arr, int start) {
        return String.join(" ", Arrays.copyOfRange(arr, start, arr.length));
    }
}
