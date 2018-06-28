package android.sheepsforsleep.com.testobject;

import android.sheepsforsleep.com.fakepojo.FakePOJO;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        try {
            TestObject testObject = FakePOJO.create(TestObject.class);
            TestObject testObject1 = FakePOJO.create(TestObject.class);
            String s = testObject.getString();
            int l = s.length();
            TestObject testObject2 = FakePOJO.create(TestObject.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        assertEquals(4, 2 + 2);
    }
}