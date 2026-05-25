package io.github.rober.envelopecreatecompat.item;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class ModCreativeModeTabs {
    private ModCreativeModeTabs() {
    }

    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.POSTAGE_STAMP.get());
        }
    }
}
