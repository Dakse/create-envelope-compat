package io.github.rober.envelopecreatecompat.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.BeltDeployerCallbacks;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import io.github.rober.envelopecreatecompat.recipe.CreatePackageToEnvelopeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(BeltDeployerCallbacks.class)
public abstract class BeltDeployerCallbacksMixin {
    @Inject(method = "activate", at = @At("HEAD"), cancellable = true)
    private static void envelope_create_compat$convertCreatePackage(TransportedItemStack transported,
                                                                    TransportedItemStackHandlerBehaviour handler,
                                                                    DeployerBlockEntity blockEntity,
                                                                    Recipe<?> recipe,
                                                                    CallbackInfo ci) {
        if (!(recipe instanceof CreatePackageToEnvelopeRecipe)) {
            return;
        }

        List<ItemStack> outputs = CreatePackageToEnvelopeRecipe.convertOutputs(transported.stack);
        if (outputs.isEmpty()) {
            return;
        }

        List<TransportedItemStack> convertedOutputs = outputs.stream()
                .map(stack -> {
                    TransportedItemStack copy = transported.copy();
                    copy.stack = stack;
                    copy.locked = true;
                    copy.angle = BeltHelper.isItemUpright(stack) ? 180 : Create.RANDOM.nextInt(360);
                    return copy;
                })
                .map(copy -> {
                    copy.locked = false;
                    return copy;
                })
                .collect(Collectors.toList());

        transported.clearFanProcessingData();

        TransportedItemStack left = transported.copy();
        left.stack.shrink(1);

        handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(convertedOutputs, left));

        ItemStack heldItem = blockEntity.getPlayer().getMainHandItem();
        boolean keepHeld = recipe instanceof ItemApplicationRecipe itemApplicationRecipe
                && itemApplicationRecipe.shouldKeepHeldItem();

        if (!keepHeld) {
            if (heldItem.getMaxDamage() > 0) {
                heldItem.hurtAndBreak(1, blockEntity.getPlayer(), EquipmentSlot.MAINHAND);
            } else {
                Player player = blockEntity.getPlayer();
                ItemStack leftover = heldItem.getCraftingRemainingItem();
                heldItem.shrink(1);
                if (heldItem.isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, leftover);
                } else if (!leftover.isEmpty() && !player.getInventory().add(leftover)) {
                    player.drop(leftover, false);
                }
            }
        }

        BlockPos pos = blockEntity.getBlockPos();
        Level level = blockEntity.getLevel();
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.25f, 0.75f);
        blockEntity.notifyUpdate();
        ci.cancel();
    }
}
