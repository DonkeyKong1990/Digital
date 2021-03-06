package de.neemann.digital.integration;

import de.neemann.digital.core.NodeException;
import de.neemann.digital.draw.elements.Circuit;
import de.neemann.digital.draw.elements.PinException;
import de.neemann.digital.draw.graphics.*;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Loads the processor and exports it to the different export instances
 * Only checks that something is written without an error
 *
 * @author hneemann
 */
public class TestExport extends TestCase {

    private static ByteArrayOutputStream export(String file, ExportFactory creator) throws NodeException, PinException, IOException {
        Circuit circuit = new ToBreakRunner(file).getCircuit();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new Export(circuit, creator).export(baos);
        return baos;
    }

    public void testSVGExport() throws NodeException, PinException, IOException {
        ByteArrayOutputStream baos
                = export("dig/processor/Processor_fibonacci.dig",
                (out, min, max) -> new GraphicSVGIndex(out, min, max, null, 15));

        assertTrue(baos.size() > 20000);
    }

    public void testSVGExportLaTeX() throws NodeException, PinException, IOException {
        ByteArrayOutputStream baos
                = export("dig/processor/Processor_fibonacci.dig",
                (out, min, max) -> new GraphicSVGLaTeX(out, min, max, null, 15));

        assertTrue(baos.size() > 15000);
    }

    public void testPNGExport() throws NodeException, PinException, IOException {
        ByteArrayOutputStream baos
                = export("dig/processor/Processor_fibonacci.dig",
                (out, min, max) -> GraphicsImage.create(out, min, max, "PNG", 1));

        assertTrue(baos.size() > 45000);
    }
}
