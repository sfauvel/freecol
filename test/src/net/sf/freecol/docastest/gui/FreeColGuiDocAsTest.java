package net.sf.freecol.docastest.gui;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.io.FreeColDataFile;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.common.resources.ResourceMapping;
import net.sf.freecol.docastest.FreeColDocAsTest;
import org.junit.Before;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class FreeColGuiDocAsTest extends FreeColDocAsTest {

    public static final FreeColResources RESOURCES = new FreeColResources(spec());

    public FreeColClient client = new MockFreeColClient();

    @Before
    public void setUp() throws Exception {
        initResources();
    }

    private void initResources() throws IOException {
        File baseDirectory = new File("data/rules/classic");
        assertTrue(baseDirectory.exists() && baseDirectory.isDirectory());
        FreeColDataFile baseData = new FreeColDataFile(baseDirectory);
        final ResourceMapping resourceMapping = baseData.getResourceMapping();
        ResourceManager.addMapping("test", resourceMapping);
    }

}
