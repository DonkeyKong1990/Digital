package de.neemann.digital.core.basic;

import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.PartDescription;
import de.neemann.digital.gui.draw.shapes.GenericShape;

import java.util.ArrayList;

/**
 * @author hneemann
 */
public class Or extends Function {

    public Or(int bits) {
        super(bits);
    }

    public static PartDescription createFactory(int bits, int inputs) {
        return new FanInDescription(new GenericShape("\u22651", inputs), inputs, () -> new Or(bits));
    }

    @Override
    protected int calculate(ArrayList<ObservableValue> inputs) throws NodeException {
        int f = 0;
        for (ObservableValue i : inputs) {
            f |= i.getValue();
        }
        return f;
    }
}