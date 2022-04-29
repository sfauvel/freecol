package net.sf.freecol.client.gui;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.gui.label.ProductionLabel;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.docastest.gui.DocGenerator;
import net.sf.freecol.docastest.gui.FreeColGuiDocAsTest;
import org.junit.Test;

import java.util.function.BiFunction;

public class ProductionLabelDocTest extends FreeColGuiDocAsTest {

    @Test
    public void test_production_label_with_default_parameters() throws InterruptedException {

        final int minNumberOfGoods = 3;
        final int maxNumberOfGoods = 6;

        final ClientOptions options = client.getClientOptions();
        options.setInteger(ClientOptions.MAX_NUMBER_OF_GOODS_IMAGES, maxNumberOfGoods);
        options.setInteger(ClientOptions.MIN_NUMBER_FOR_DISPLAYING_GOODS_COUNT, minNumberOfGoods);


        String nameTemplate = "ProductionLabelWithDefaultParameters_%s.jpg";
        BiFunction<Integer, String, String> buildImage = (amount, name) -> {
            final GoodsType goodsType = RESOURCES.sugar;
            AbstractGoods ag = new AbstractGoods();
            ag.setType(goodsType);
            ag.setAmount(amount);

            final ProductionLabel productionLabel = new ProductionLabel(client, ag);

            final DocGenerator.ImageFile imageFile = DocGenerator.componentToImage(productionLabel, IMAGE_PATH, String.format(nameTemplate, name));
            return imageFile.imageWithChecksum() + String.format("\nWith amount %d", amount);
        };

        write("", String.format("Min value to display value: %d", minNumberOfGoods),
                "", String.format("Max items displayed: %d", maxNumberOfGoods),
                "", buildImage.apply(0, "zero"),
                "", buildImage.apply(1, "one"),
                "", buildImage.apply(2, "two"),
                "", buildImage.apply(5, "five"),
                "", buildImage.apply(9, "nine"),
                "", buildImage.apply(-2, "minus_two"),
                "", buildImage.apply(-5, "minus_five"),
                "", buildImage.apply(-9, "minus_nine")
        );
    }
}
