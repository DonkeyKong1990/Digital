package de.neemann.digital.core.memory;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.lang.Lang;

import java.io.File;
import java.io.IOException;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * A ROM module.
 *
 * @author hneemann
 */
public class ROM extends Node implements Element {
    /**
     * Key used to store the source file in the attribute set
     */
    public final static String LAST_DATA_FILE_KEY = "lastDataFile";

    /**
     * The ROMs {@link ElementTypeDescription}
     */
    public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(ROM.class,
            input("A", Lang.get("elem_ROM_pin_address")),
            input("sel", Lang.get("elem_ROM_pin_sel")))
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.BITS)
            .addAttribute(Keys.ADDR_BITS)
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.DATA)
            .addAttribute(Keys.IS_PROGRAM_MEMORY)
            .addAttribute(Keys.AUTO_RELOAD_ROM)
            .addAttribute(Keys.SHOW_LISTING);

    private DataField data;
    private final ObservableValue output;
    private final int addrBits;
    private final boolean showList;
    private final File hexFile;
    private final Observable romObservable = new Observable();
    private final boolean autoLoad;
    private ObservableValue addrIn;
    private ObservableValue selIn;
    private int addr;
    private boolean sel;
    private int romAddr;

    /**
     * Creates a new instance
     *
     * @param attr the elements attributes
     */
    public ROM(ElementAttributes attr) {
        int bits = attr.get(Keys.BITS);
        output = new ObservableValue("D", bits, true).setDescription(Lang.get("elem_ROM_pin_data"));
        data = attr.get(Keys.DATA);
        addrBits = attr.get(Keys.ADDR_BITS);
        showList = attr.get(Keys.SHOW_LISTING);
        autoLoad = attr.get(Keys.AUTO_RELOAD_ROM);
        if (showList || autoLoad) {
            hexFile = attr.getFile(LAST_DATA_FILE_KEY);
        } else
            hexFile = null;
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        addrIn = inputs.get(0).checkBits(addrBits, this).addObserverToValue(this);
        selIn = inputs.get(1).checkBits(1, this).addObserverToValue(this);
    }

    @Override
    public ObservableValues getOutputs() {
        return output.asList();
    }

    @Override
    public void readInputs() throws NodeException {
        addr = (int) addrIn.getValue();
        sel = selIn.getBool();
        if (sel) {
            romAddr = addr;
            romObservable.hasChanged();
        }
    }

    @Override
    public void writeOutputs() throws NodeException {
        output.set(data.getDataWord(addr), !sel);
    }

    /**
     * @return the last used input address
     */
    public long getRomAddress() {
        return romAddr;
    }

    /**
     * The file used to fill this ROM module.
     *
     * @return the file
     */
    public File getHexFile() {
        return hexFile;
    }

    @Override
    public void init(Model model) throws NodeException {
        if (autoLoad) {
            try {
                data = new DataField(hexFile);
            } catch (IOException e) {
                throw new NodeException(e.getMessage(), this, null);
            }
        }
    }

    /**
     * @return true if there is a listing to show
     */
    public boolean showListing() {
        return showList && hexFile != null;
    }

    /**
     * Adds an observer to this ROM
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        romObservable.addObserver(observer);
    }

    /**
     * Removes an observer from this ROM
     *
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        romObservable.removeObserver(observer);
    }
}
