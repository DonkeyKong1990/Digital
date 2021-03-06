package de.neemann.digital.testing;

import java.util.ArrayList;

/**
 * The test data.
 *
 * @author hneemann
 */
public class TestData {

    /**
     * the default instance
     */
    public static final TestData DEFAULT = new TestData("");

    private String dataString;
    private transient ArrayList<Value[]> lines;
    private transient ArrayList<String> names;

    TestData(String data) {
        this.dataString = data;
    }

    /**
     * creates a new instance
     *
     * @param valueToCopy the instance to copy
     */
    public TestData(TestData valueToCopy) {
        this(valueToCopy.dataString);
    }

     /**
     * @return the data string
     */
    public String getDataString() {
        return dataString;
    }

    /**
     * Sets the data and checks its validity
     *
     * @param data the data
     * @throws TestingDataException thrown if data is not valid
     */
    public void setDataString(String data) throws TestingDataException {
        if (!data.equals(dataString)) {
            TestDataParser tdp = new TestDataParser(data).parse();
            dataString = data;
            lines = tdp.getLines();
            names = tdp.getNames();
        }
    }

    private void check() {
        if (lines == null) {
            try {
                TestDataParser tdp = new TestDataParser(dataString).parse();
                lines = tdp.getLines();
                names = tdp.getNames();
            } catch (TestingDataException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the data lines
     */
    public ArrayList<Value[]> getLines() {
        check();
        return lines;
    }

    /**
     * @return the signal names
     */
    public ArrayList<String> getNames() {
        check();
        return names;
    }
}
