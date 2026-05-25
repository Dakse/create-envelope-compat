package io.github.rober.envelopecreatecompat.item;

import io.github.rober.envelopecreatecompat.EnvelopeCreateCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(Registries.ITEM, EnvelopeCreateCompat.MOD_ID);

    public static final DeferredHolder<Item, Item> POSTAGE_STAMP =
            REGISTER.register("postage_stamp", () -> new Item(new Item.Properties()));

    private ModItems() {
    }
}
