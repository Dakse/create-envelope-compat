package io.github.rober.envelopecreatecompat.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import io.github.rober.envelopecreatecompat.recipe.EnvelopePackageOpening;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SawBlockEntity.class)
public abstract class SawBlockEntityMixin {
    @Shadow
    public ProcessingInventory inventory;

    @Inject(method = "applyRecipe", at = @At("HEAD"), cancellable = true)
    private void envelope_create_compat$openEnvelopePackage(CallbackInfo ci) {
        ItemStack input = inventory.getStackInSlot(0);
        if (!EnvelopePackageOpening.canOpen(input)) {
            return;
        }

        SawBlockEntity saw = (SawBlockEntity) (Object) this;
        List<ItemStack> outputs = EnvelopePackageOpening.convertOutputs(input, saw.getLevel(), Vec3.atCenterOf(saw.getBlockPos()));

        inventory.clear();
        for (int slot = 0; slot < outputs.size() && slot + 1 < inventory.getSlots(); slot++) {
            inventory.setStackInSlot(slot + 1, outputs.get(slot));
        }

        saw.award(AllAdvancements.SAW_PROCESSING);
        ci.cancel();
    }
}
