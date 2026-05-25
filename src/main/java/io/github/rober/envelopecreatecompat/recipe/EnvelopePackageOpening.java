package io.github.rober.envelopecreatecompat.recipe;

import com.simibubi.create.foundation.item.ItemHelper;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.PackageItem;
import io.github.mortuusars.envelope.world.item.mail.Mail;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class EnvelopePackageOpening {
    private EnvelopePackageOpening() {
    }

    public static boolean canOpen(ItemStack stack) {
        return stack.is(Envelope.Tags.Items.PACKAGES) && stack.getItem() instanceof PackageItem;
    }

    public static List<ItemStack> convertOutputs(ItemStack stack, Level level, Vec3 pos) {
        if (!canOpen(stack)) {
            return List.of();
        }

        PackageItem packageItem = (PackageItem) stack.getItem();
        ItemStack packageCopy = stack.copy();

        List<ItemStack> outputs = new ArrayList<>();
        for (ItemStack content : packageItem.unpack(packageCopy, level, pos, null)) {
            if (!content.isEmpty()) {
                ItemHelper.addToList(content, outputs);
            }
        }

        ItemHelper.addToList(packageItem.createBoxReturnItem(stack), outputs);

        ItemStack addressTag = new ItemStack(Envelope.Items.ADDRESS_TAG.get());
        Mail.getRecipient(stack).ifPresent(address -> addressTag.set(Envelope.DataComponents.ADDRESS, address));
        outputs.add(addressTag);

        return outputs;
    }
}
