package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.world.entity.EquipmentSlot;
//? if <26.1 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
*///?}

//? if >=26.1 {
public final class WearableBatteryItem extends BatteryItem {
//?} else
//public final class WearableBatteryItem extends BatteryItem implements Equipable {
    public WearableBatteryItem(Properties props, EquipmentSlot slot, int capacity, EuTier tier,
                               int transfer, boolean supplies) {
        super(GearProperties.worn(props, slot, "utility"), capacity, tier, true, transfer, supplies);
    }
    //? if <26.1 {
    /*@Override
    public EquipmentSlot getEquipmentSlot() { return Gear.of(this).slot(); }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return swapWithEquipmentSlot(this, level, player, hand);
    }
    *///?}
}
