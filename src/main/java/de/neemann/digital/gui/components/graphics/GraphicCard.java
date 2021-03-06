package de.neemann.digital.gui.components.graphics;

import de.neemann.digital.core.Node;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.ObservableValues;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.core.memory.DataField;
import de.neemann.digital.core.memory.RAMInterface;
import de.neemann.digital.lang.Lang;

import javax.swing.*;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * Graphic card.
 * Mostly a RAM module with an additional input bit which selects the visible bank.
 * So you can use double buffering.
 *
 * @author hneemann
 */
public class GraphicCard extends Node implements Element, RAMInterface {

    /**
     * The terminal description
     */
    public static final ElementTypeDescription DESCRIPTION
            = new ElementTypeDescription(GraphicCard.class,
            input("A", Lang.get("elem_RAMSinglePort_pin_addr")),
            input("str", Lang.get("elem_RAMSinglePort_pin_str")),
            input("C", Lang.get("elem_RAMSinglePort_pin_c")),
            input("ld", Lang.get("elem_RAMSinglePort_pin_ld")),
            input("B", Lang.get("elem_GraphicCard_pin_B")))
            .addAttribute(Keys.GRAPHIC_WIDTH)
            .addAttribute(Keys.GRAPHIC_HEIGHT)
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.LABEL);

    private final DataField memory;
    private final int width;
    private final int height;
    private final int bankSize;

    private static GraphicDialog graphicDialog;
    private ObservableValue dataOut;
    private ObservableValue addrIn;
    private ObservableValue strIn;
    private ObservableValue clkIn;
    private ObservableValue ldIn;
    private ObservableValue dataIn;
    private ObservableValue bankIn;
    private boolean lastClk;
    private boolean ld;
    private int addr;
    private boolean lastBank;

    /**
     * Creates a new Graphics instance
     *
     * @param attr the attributes
     */
    public GraphicCard(ElementAttributes attr) {
        width = attr.get(Keys.GRAPHIC_WIDTH);
        height = attr.get(Keys.GRAPHIC_HEIGHT);
        bankSize = width * height;
        memory = new DataField(bankSize * 2);

        dataOut = new ObservableValue("D", 16, true)
                .setDescription(Lang.get("elem_RAMSinglePort_pin_d"))
                .setBidirectional(true);
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        addrIn = inputs.get(0).checkBits(16, this).addObserverToValue(this);
        strIn = inputs.get(1).checkBits(1, this).addObserverToValue(this);
        clkIn = inputs.get(2).checkBits(1, this).addObserverToValue(this);
        ldIn = inputs.get(3).checkBits(1, this).addObserverToValue(this);
        bankIn = inputs.get(4).checkBits(1, this).addObserverToValue(this);
        dataIn = inputs.get(5).checkBits(16, this).addObserverToValue(this); // additional input to read the port
    }

    @Override
    public ObservableValues getOutputs() {
        return dataOut.asList();
    }

    @Override
    public void readInputs() throws NodeException {
        long data = 0;
        boolean clk = clkIn.getBool();
        boolean str;
        if (!lastClk && clk) {
            str = strIn.getBool();
            if (str)
                data = dataIn.getValue();
        } else
            str = false;

        ld = ldIn.getBool();
        if (ld || str)
            addr = (int) addrIn.getValue();

        boolean bank = bankIn.getBool();

        if (str) {
            memory.setData(addr, data);
            if (addr >= bankSize == bank)
                updateGraphic(bank);
        }

        if (lastBank != bank)
            updateGraphic(bank);

        lastBank = bank;

        lastClk = clk;
    }

    @Override
    public void writeOutputs() throws NodeException {
        if (ld) {
            dataOut.set(memory.getDataWord(addr), false);
        } else {
            dataOut.setHighZ(true);
        }
    }

    @Override
    public DataField getMemory() {
        return memory;
    }

    private void updateGraphic(boolean bank) {
        SwingUtilities.invokeLater(() -> {
            if (graphicDialog == null || !graphicDialog.isVisible())
                graphicDialog = new GraphicDialog(width, height);
            graphicDialog.updateGraphic(memory, bank);
        });
    }

}
