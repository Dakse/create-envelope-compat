package io.github.rober.envelopecreatecompat;

import io.github.rober.envelopecreatecompat.item.ModItems;
import io.github.rober.envelopecreatecompat.item.ModCreativeModeTabs;
import io.github.rober.envelopecreatecompat.recipe.ModRecipeSerializers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(EnvelopeCreateCompat.MOD_ID)
public class EnvelopeCreateCompat {
    public static final String MOD_ID = "envelope_create_compat";

    public EnvelopeCreateCompat(IEventBus modEventBus) {
        modEventBus.addListener(ModCreativeModeTabs::addItems);
        ModItems.REGISTER.register(modEventBus);
        ModRecipeSerializers.REGISTER.register(modEventBus);
    }
}
