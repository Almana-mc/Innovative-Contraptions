package me.almana.innovativecontraptions.content.tool;

import me.almana.innovativecontraptions.content.energy.net.EnergySettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
//? if >=26.1 {
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
//?} else
//import net.neoforged.neoforge.event.level.BlockEvent;

public final class MachineLoss {
    public static boolean rolls(ItemStack tool, ServerLevel level) {
        if (tool.getItem() instanceof ElectricWrenchItem) return false;
        if (tool.getItem() instanceof WrenchItem) return level.getRandom().nextDouble() < EnergySettings.WRENCH_LOSS_CHANCE.get();
        return EnergySettings.MACHINE_LOSS_WITHOUT_WRENCH.get();
    }
    public static void destroy(ServerLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof Container container) container.clearContent();
        level.destroyBlock(pos, false);
    }
    //? if >=26.1 {
    public static void onBreak(BreakBlockEvent event) {
    //?} else
    //public static void onBreak(BlockEvent.BreakEvent event) {
        if (probing || !(event.getLevel() instanceof ServerLevel level) || !(event.getState().getBlock() instanceof Wrenchable)
                || event.getPlayer() instanceof FakePlayer || event.getPlayer().hasInfiniteMaterials()
                || !rolls(event.getPlayer().getMainHandItem(), level)) return;
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
