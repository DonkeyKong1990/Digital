package de.neemann.digital.core.io;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.lang.Lang;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * The different outputs
 *
 * @author hneemann
 */
public class Out implements Element {

    /**
     * The Input description
     */
    public static final ElementTypeDescription DESCRIPTION
            = new ElementTypeDescription(Out.class, input("in")) {
        @Override
        public String getDescription(ElementAttributes elementAttributes) {
            String d = elementAttributes.get(Keys.DESCRIPTION);
            if (d.length() > 0)
                return d;
            else
                return Lang.get("elem_Out");
        }
    }
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.BITS)
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.DESCRIPTION);

    /**
     * The LED description
     */
    public static final ElementTypeDescription LEDDESCRIPTION
            = new ElementTypeDescription("LED", Out.class, input("in"))
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.SIZE)
            .addAttribute(Keys.COLOR);

    /**
     * The seven segment display description
     */
    public static final ElementTypeDescription SEVENDESCRIPTION
            = new ElementTypeDescription("Seven-Seg",
            attributes -> {
                return new Out(1, 1, 1, 1, 1, 1, 1, 1);
            },
            input("a"), input("b"), input("c"), input("d"), input("e"), input("f"), input("g"), input("dp"))
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.COLOR);

    /**
     * The seven segment hex display description
     */
    public static final ElementTypeDescription SEVENHEXDESCRIPTION
            = new ElementTypeDescription("Seven-Seg-Hex",
            attributes -> {
                return new Out(4, 1);
            }, input("d"), input("dp"))
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.COLOR);

    private final int[] bits;
    private final String label;
    private final String description;
    private ObservableValue value;

    /**
     * Creates a new instance
     *
     * @param attributes the attributes
     */
    public Out(ElementAttributes attributes) {
        bits = new int[]{attributes.getBits()};
        label = attributes.getCleanLabel();
        description = attributes.get(Keys.DESCRIPTION);
    }

    /**
     * Creates a new instance
     *
     * @param bits the bitcount of the different inputs
     */
    public Out(int... bits) {
        this.bits = bits;
        label = null;
        description = null;
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        if (inputs.size() != bits.length)
            throw new NodeException("wrong input count");
        value = inputs.get(0).checkBits(bits[0], null);
        for (int i = 1; i < bits.length; i++)
            inputs.get(i).checkBits(bits[i], null);
    }

    @Override
    public ObservableValues getOutputs() {
        return ObservableValues.EMPTY_LIST;
    }

    @Override
    public void registerNodes(Model model) {
        model.addOutput(new Signal(label, value).setDescription(description));
    }
}
