package de.neemann.digital.core.memory;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * A simple register.
 *
 * @author hneemann
 */
public class Register extends Node implements Element {

    /**
     * The registers {@link ElementTypeDescription}
     */
    public static final ElementTypeDescription DESCRIPTION
            = new ElementTypeDescription(Register.class, input("D"), input("C"), input("en"))
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.BITS)
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.VALUE_IS_PROBE)
            .setShortName("Reg");

    private final int bits;
    private final boolean isProbe;
    private final String label;
    private ObservableValue dVal;
    private ObservableValue clockVal;
    private ObservableValue enableVal;
    private ObservableValue q;
    private boolean lastClock;
    private long value;

    /**
     * Creates a new instance
     *
     * @param attributes the elements attributes
     */
    public Register(ElementAttributes attributes) {
        super(true);
        bits = attributes.getBits();
        this.q = new ObservableValue("Q", bits);
        isProbe = attributes.get(Keys.VALUE_IS_PROBE);
        label = attributes.get(Keys.LABEL);
    }

    @Override
    public void readInputs() throws NodeException {
        boolean enable = enableVal.getBool();
        boolean clock = clockVal.getBool();
        if (clock && !lastClock && enable)
            value = dVal.getValue();
        lastClock = clock;
    }

    @Override
    public void writeOutputs() throws NodeException {
        q.setValue(value);
    }

    @Override
    public void setInputs(ObservableValues inputs) throws BitsException {
        dVal = inputs.get(0).addObserverToValue(this).checkBits(bits, this);
        clockVal = inputs.get(1).addObserverToValue(this).checkBits(1, this);
        enableVal = inputs.get(2).addObserverToValue(this).checkBits(1, this);
    }

    @Override
    public ObservableValues getOutputs() {
        return q.asList();
    }

    @Override
    public void registerNodes(Model model) {
        super.registerNodes(model);
        if (isProbe)
            model.addSignal(new Signal(label, q));
    }
}
