package de.neemann.digital.draw.elements;

import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.element.PinDescription;
import de.neemann.digital.core.element.PinInfo;
import de.neemann.digital.draw.graphics.Vector;

/**
 * Puts the pins name and the pins x-y-position together!
 *
 * @author hneemann
 */
public class Pin extends PinInfo {

    private final Vector pos;
    private ObservableValue value;
    private ObservableValue readerValue;  // reader for bidirectional pins


    /**
     * Creates a new pin
     *
     * @param pos the position
     * @param pin the PinDescription
     */
    public Pin(Vector pos, PinDescription pin) {
        super(pin);
        this.pos = pos;
    }

    /**
     * @return the pins position
     */
    public Vector getPos() {
        return pos;
    }

    /**
     * @return the value which represents the pin state
     */
    public ObservableValue getValue() {
        return value;
    }

    /**
     * Sets the value which represents the pins state
     *
     * @param value the ObservableValue
     */
    public void setValue(ObservableValue value) {
        this.value = value;
    }

    /**
     * If the pin is bidirectional there are two values, one which can be used to read the pins state
     * and one to write the pins state. The readers value is generated so that all the writers are checked to
     * find and select the one writer which is not in high Z state. If more then one writer is in an not high Z
     * state an exception is thrown.
     *
     * @return returns the bidirectional reader
     */
    public ObservableValue getReaderValue() {
        return readerValue;
    }

    /**
     * Sets the bidirectional reader.
     *
     * @param readerValue the bidirectional reader
     * @see Pin#getReaderValue()
     */
    public void setReaderValue(ObservableValue readerValue) {
        this.readerValue = readerValue;
    }

}
