package me.almana.innovativecontraptions.content.equipment;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;

public class WornItem extends Item implements Equipable {
    public WornItem(Properties props) { super(props); }

    @Override
    public EquipmentSlot getEquipmentSlot() { return Gear.of(this).slot(); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return swapWithEquipmentSlot(this, level, player, hand);
    }

    @Override
    public int getEnchantmentValue() { return Gear.of(this).enchantment(); }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return Gear.of(this).repair().filter(repair::is).isPresent();
    }

    public static void register(IEventBus bus) {
        bus.addListener(WornItem::dispensers);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, WornItem::wear);
    }

    private static void dispensers(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> BuiltInRegistries.ITEM.stream().filter(item -> item.components().has(EquipmentData.WORN.get()))
                .forEach(item -> DispenserBlock.registerBehavior(item, ArmorItem.DISPENSE_ITEM_BEHAVIOR)));
    }

    // mirrors 26.1 damageOnHurt
    private static void wear(ArmorHurtEvent event) {
        event.getArmorMap().values().forEach(entry -> {
            ItemStack stack = entry.armorItemStack;
            Gear gear = stack.get(EquipmentData.WORN.get());
            if (gear != null && gear.damageOnHurt() && stack.isDamageableItem() && stack.canBeHurtBy(event.getDamageSource())) {
                entry.newDamage = (int) Math.max(1, event.getEntity().damageContainers.peek().getNewDamage() / 4);
            }
        });
    }
}
