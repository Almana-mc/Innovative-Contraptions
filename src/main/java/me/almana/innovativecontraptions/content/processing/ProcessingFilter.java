package me.almana.innovativecontraptions.content.processing;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public final class ProcessingFilter {
    public static boolean advanced(ItemStack upgrade) {
        return upgrade.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get()) || upgrade.is(ICItems.ADVANCED_PULLING_UPGRADE.get());
    }
    public static boolean accepts(ItemStack upgrade, ItemStack candidate) {
        if (!advanced(upgrade)) return true;
        int flags = upgrade.getOrDefault(ProcessingContent.FILTER_FLAGS.get(), 0);
        int chargeMode = flags >> 4 & 3;
        int charge = BatteryItem.stored(candidate), threshold = upgrade.getOrDefault(ProcessingContent.FILTER_CHARGE.get(), 0);
        if (chargeMode != 0 && (!(candidate.getItem() instanceof BatteryItem)
                || chargeMode == 1 && charge < threshold || chargeMode == 2 && charge > threshold || chargeMode == 3 && charge != threshold)) return false;
        boolean matched = upgrade.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItemCopyStream().anyMatch(filter ->
                filter.is(candidate.getItem()) && ((flags & 2) == 0 || ItemStack.isSameItemSameComponents(filter, candidate))
                        && ((flags & 4) == 0 || filter.getDamageValue() == candidate.getDamageValue())
                        && ((flags & 8) == 0 || filter.getItem() instanceof BatteryItem && BatteryItem.stored(filter) == charge));
        return (flags & 1) == 0 ? matched : !matched;
    }
    private ProcessingFilter() { }
}
