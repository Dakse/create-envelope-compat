package io.github.rober.envelopecreatecompat.recipe;

import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.logistics.box.PackageItem;
import io.github.mortuusars.envelope.Envelope;
import io.github.rober.envelopecreatecompat.item.ModItems;
import io.github.mortuusars.envelope.world.item.component.PackageContents;
import io.github.mortuusars.envelope.world.item.mail.Mail;
import io.github.mortuusars.envelope.world.mail.address.type.CustomAddress;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public class CreatePackageToEnvelopeRecipe extends DeployerApplicationRecipe {
    public CreatePackageToEnvelopeRecipe(ItemApplicationRecipeParams params) {
        super(params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (!super.matches(inv, level)) {
            return false;
        }

        ItemStack createPackage = inv.getItem(0);
        ItemStack addressTag = inv.getItem(1);
        if (!PackageItem.isPackage(createPackage)) {
            return false;
        }
        if (!addressTag.is(ModItems.POSTAGE_STAMP.get())) {
            return false;
        }
        return !PackageItem.getAddress(createPackage).isBlank();
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv, HolderLookup.Provider provider) {
        List<ItemStack> outputs = convertOutputs(inv.getItem(0));
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.getFirst();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return new ItemStack(Envelope.Items.PACKAGE.get());
    }

    public static List<ItemStack> convertOutputs(ItemStack createPackage) {
        if (!PackageItem.isPackage(createPackage)) {
            return List.of();
        }

        String address = PackageItem.getAddress(createPackage).trim();
        if (address.isEmpty()) {
            return List.of();
        }

        ItemStackHandler originalContents = PackageItem.getContents(createPackage);
        ItemStackHandler compactedContents = new ItemStackHandler(PackageItem.SLOTS);
        for (int slot = 0; slot < originalContents.getSlots(); slot++) {
            ItemStack stack = originalContents.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                ItemHandlerHelper.insertItemStacked(compactedContents, stack.copy(), false);
            }
        }

        List<ItemStack> envelopeContents = new ArrayList<>(PackageContents.SLOTS);
        List<ItemStack> overflow = new ArrayList<>(PackageItem.SLOTS - PackageContents.SLOTS);
        for (int slot = 0; slot < compactedContents.getSlots(); slot++) {
            ItemStack stack = compactedContents.getStackInSlot(slot).copy();
            if (slot < PackageContents.SLOTS) {
                envelopeContents.add(stack);
            } else if (!stack.isEmpty()) {
                overflow.add(stack);
            }
        }

        ItemStack envelopePackage = Mail.createPackage(new PackageContents(envelopeContents))
                .recipient(new CustomAddress(Component.literal(address)))
                .get();

        List<ItemStack> outputs = new ArrayList<>(1 + overflow.size());
        outputs.add(envelopePackage);
        outputs.addAll(overflow);
        return outputs;
    }
}
