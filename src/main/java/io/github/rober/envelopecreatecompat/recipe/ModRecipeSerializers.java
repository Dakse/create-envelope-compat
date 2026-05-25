package io.github.rober.envelopecreatecompat.recipe;

import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import io.github.rober.envelopecreatecompat.EnvelopeCreateCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnvelopeCreateCompat.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CreatePackageToEnvelopeRecipe>> CREATE_PACKAGE_TO_ENVELOPE =
            REGISTER.register("create_package_to_envelope",
                    () -> new ItemApplicationRecipe.Serializer<>(CreatePackageToEnvelopeRecipe::new));

    private ModRecipeSerializers() {
    }
}
