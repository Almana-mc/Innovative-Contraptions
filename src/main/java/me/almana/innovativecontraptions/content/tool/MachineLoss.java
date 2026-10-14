package me.almana.innovativecontraptions.content.tool;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.energy.net.EnergySettings;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
//? if >=26.1 {
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
//?} else
//import net.neoforged.neoforge.event.level.BlockEvent;

public final class MachineLoss {
    public static final TagKey<Block> SALVAGE_CASING = tag("salvage/machine_casing");
    public static final TagKey<Block> SALVAGE_ADVANCED_CASING = tag("salvage/advanced_machine_casing");
    public static final TagKey<Block> SALVAGE_GENERATOR = tag("salvage/generator");

    public static boolean rolls(ItemStack tool, ServerLevel level, BlockState state) {
        if (tool.getItem() instanceof ElectricWrenchItem || salvage(state).isEmpty()) return false;
        double chance = tool.getItem() instanceof WrenchItem ? EnergySettings.WRENCH_LOSS_CHANCE.get() : EnergySettings.NO_WRENCH_LOSS_CHANCE.get();
        return level.getRandom().nextDouble() < chance;
    }
    public static void destroy(ServerLevel level, BlockPos pos) {
        ItemStack salvage = salvage(level.getBlockState(pos));
        level.destroyBlock(pos, false);
        Block.popResource(level, pos, salvage);
    }
    private static ItemStack salvage(BlockState state) {
        if (state.is(SALVAGE_CASING)) return new ItemStack(ICItems.MACHINE_CASING.get());
        if (state.is(SALVAGE_ADVANCED_CASING)) return new ItemStack(ICItems.ADVANCED_MACHINE_CASING.get());
        if (state.is(SALVAGE_GENERATOR)) return new ItemStack(ICBlocks.GENERATOR.get());
        return ItemStack.EMPTY;
    }
    private static TagKey<Block> tag(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, path));
    }
    //? if >=26.1 {
    public static void onBreak(BreakBlockEvent event) {
    //?} else
    //public static void onBreak(BlockEvent.BreakEvent event) {
        if (probing || !(event.getLevel() instanceof ServerLevel level) || !(event.getState().getBlock() instanceof Wrenchable)
                || event.getPlayer() instanceof FakePlayer || event.getPlayer().hasInfiniteMaterials()
                || !rolls(event.getPlayer().getMainHandItem(), level, event.getState())) return;
        event.setCanceled(true);
        destroy(level, event.getPos());
    }
    private static boolean probing;
    public static <T extends Event & ICancellableEvent> boolean permits(T event) {
        probing = true;
        try {
            return !NeoForge.EVENT_BUS.post(event).isCanceled();
        } finally {
            probing = false;
        }
    }
    private MachineLoss() {}
}
