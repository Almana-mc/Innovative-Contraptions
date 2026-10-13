package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public final class DrillItem extends PoweredMiningItem {
    private final boolean iridium;
    public DrillItem(Properties props, int grade) {
        super(grade == 2 ? enchanted(props) : props, grade == 2 ? 300_000 : 30_000,
                grade == 2 ? EuTier.HV : EuTier.LV, grade == 2 ? 1_000 : 100,
                grade == 0 ? 50 : grade == 1 ? 80 : 800, 8 + grade * 8, true, grade > 0);
        iridium = grade == 2;
    }
    private static Properties enchanted(Properties props) {
        //? if >=26.1 {
        return props.delayedComponent(DataComponents.ENCHANTMENTS, context -> {
            var enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            enchantments.set(context.getOrThrow(Enchantments.FORTUNE), 3);
            return enchantments.toImmutable();
        });
        //?} else
        //return props;
    }
    //? if <26.1 {
    /*@Override
    public void onCraftedPostProcess(ItemStack stack, Level level) {
        if (iridium) stack.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), 3);
    }
    *///?}
    @Override
    //? if >=26.1 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        if (!iridium || !EquipmentControls.mode(player)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            boolean silk = !stack.getOrDefault(EquipmentData.ACTIVE.get(), false);
            stack.set(EquipmentData.ACTIVE.get(), silk);
            var enchantments = new ItemEnchantments.Mutable(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
            enchantments.set(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), silk ? 1 : 0);
            stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
            EquipmentControls.message(player, silk ? "silk_touch" : "fortune");
        }
        return InteractionResult.SUCCESS;
    }
}
