package util;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilTest {

    @Test
    public void testJoinArraySubsetBySpace() {
        String[] arr = {"one", "two", "three"};
        Assert.assertEquals("two three", ArrayUtil.joinArraySubsetBySpace(arr, 1));
    }
}
