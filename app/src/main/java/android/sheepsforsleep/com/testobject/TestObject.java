package android.sheepsforsleep.com.testobject;

import android.location.Location;
import android.sheepsforsleep.com.fakepojo.IFace;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestObject {

    private String string;
    private int anInt;
    private double aDouble;
    private boolean aBoolean;
    private long aLong;
    private float aFloat;
    private boolean isaBoolean;
    private int[] intList;
    private String[] strings;
    private List<Integer> stringList;
    private LinkedList<TestListItem> testList;
    private Map<Integer, TestListItem> testMap;
    private Set<TestListItem> testSet;


    public TestObject() {
    }

    public String getString() {
        return string;
    }

    public int getAnInt() {
        return anInt;
    }

    public double getaDouble() {
        return aDouble;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public long getaLong() {
        return aLong;
    }

    public float getaFloat() {
        return aFloat;
    }

    public boolean isIsaBoolean() {
        return isaBoolean;
    }

    public int[] getIntList() {
        return intList;
    }

    public String[] getStrings() {
        return strings;
    }

    public List<Integer> getStringList() {
        return stringList;
    }

    public LinkedList<TestListItem> getTestList() {
        return testList;
    }

    public Map<Integer, TestListItem> getTestMap() {
        return testMap;
    }

    public Set<TestListItem> getTestSet() {
        return testSet;
    }
}
