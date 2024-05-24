package net.sf.freecol;

import net.sf.freecol.common.io.FreeColRules;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.docastest.FreeColDocAsTest;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class SpecificationDocTest extends FreeColDocAsTest {

    @Test
    public void buildings() throws XMLStreamException, IOException {

        final String resource = "default";
        final Properties prop = getResources(resource);
        Specification specification = FreeColRules.getFreeColRulesFile("freecol").getSpecification();

        final List<BuildingType> buildingTypeList = specification.getBuildingTypeList();
        final List<BuildingType> firstBuildings = buildingTypeList.stream()
                .filter(b -> b.getUpgradesFrom() == null)
                .collect(Collectors.toList());

        String doc = String.join("\n",
                "We list below all buildings with `" + resource + "` resources,",
                "Some of them can be improved to another building.",
                "",
                ""
        );
        for (BuildingType type : firstBuildings) {
            doc += getAdocImage(type, prop) + "\n";
            BuildingType upgradeTo = type.getUpgradesTo();
            while (upgradeTo != null) {
                doc += " -> " + getAdocImage(upgradeTo, prop) + "\n";
                upgradeTo = upgradeTo.getUpgradesTo();
            }
            doc += "\n";
        }
        write(doc);

    }

    private static Properties getResources(String resource) {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("data/" + resource + "/resources.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }

    private static String getAdocImage(BuildingType type, Properties prop) {
        String imageKey = "image.buildingicon." + type.getType();
        return "image:{RESOURCES_PATH}/" + prop.get(imageKey) + "[]";
    }
}
