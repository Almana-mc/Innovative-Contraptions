package me.almana.innovativecontraptions.content.reactor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ReactorStructure {
    public static final int SHELL = 2, CONFLICT_RANGE = 4;

    public static int chambers(Level level, BlockPos reactor) {
        int count = 0;
        for (Direction side : Direction.values())
            if (level.getBlockState(reactor.relative(side)).is(ICBlocks.REACTOR_CHAMBER.get())) count++;
        return count;
    }

    public static boolean powered(Level level, BlockPos reactor, boolean casing) {
        if (level.hasNeighborSignal(reactor)) return true;
        for (Direction side : Direction.values()) {
            BlockPos at = reactor.relative(side);
            if (level.getBlockState(at).is(ICBlocks.REACTOR_CHAMBER.get()) && level.hasNeighborSignal(at)) return true;
        }
        if (!casing) return false;
        for (BlockPos at : shell(reactor))
            if (level.getBlockState(at).is(ICBlocks.REACTOR_REDSTONE_PORT.get()) && level.hasNeighborSignal(at)) return true;
        return false;
    }

    public static boolean casingComplete(Level level, BlockPos reactor) {
        for (BlockPos at : shell(reactor)) if (!casing(level.getBlockState(at))) return false;
        return true;
    }

    public static boolean conflict(Level level, BlockPos reactor) {
        for (BlockPos at : BlockPos.betweenClosed(reactor.offset(-CONFLICT_RANGE, -CONFLICT_RANGE, -CONFLICT_RANGE),
                reactor.offset(CONFLICT_RANGE, CONFLICT_RANGE, CONFLICT_RANGE))) {
            if (at.equals(reactor) || !level.getBlockState(at).is(ICBlocks.NUCLEAR_REACTOR.get())) continue;
            BlockPos other = at.immutable();
            if (chambers(level, other) == ReactorGrid.MAX_COLS - ReactorGrid.BASE_COLS && casingComplete(level, other)) return true;
        }
        return false;
    }

    @Nullable
    public static NuclearReactorBlockEntity adjacent(Level level, BlockPos pos) {
        for (Direction side : Direction.values())
            if (level.getBlockEntity(pos.relative(side)) instanceof NuclearReactorBlockEntity reactor) return reactor;
        return null;
    }

    @Nullable
    public static NuclearReactorBlockEntity enclosing(Level level, BlockPos casing) {
        for (BlockPos at : BlockPos.betweenClosed(casing.offset(-SHELL, -SHELL, -SHELL), casing.offset(SHELL, SHELL, SHELL)))
            if (onShell(at, casing) && level.getBlockEntity(at) instanceof NuclearReactorBlockEntity reactor && reactor.fluidMode()) return reactor;
        return null;
    }

    public static InteractionResult open(Level level, Player player, @Nullable NuclearReactorBlockEntity reactor) {
        if (reactor == null) return InteractionResult.PASS;
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(reactor, reactor.getBlockPos());
        return InteractionResult.SUCCESS;
    }

    static void markGrid(Level level, BlockPos reactor) {
        EuGrids.markDirty(level, reactor);
        for (Direction side : Direction.values()) EuGrids.markDirty(level, reactor.relative(side));
    }

    static void invalidate(Level level, BlockPos reactor) {
        level.invalidateCapabilities(reactor);
        for (BlockPos at : shell(reactor)) level.invalidateCapabilities(at);
    }

    static boolean casing(BlockState state) {
        return state.getBlock() instanceof ReactorCasingBlock || state.is(ICBlocks.REACTOR_FLUID_PORT.get());
    }

    private static List<BlockPos> shell(BlockPos center) {
        List<BlockPos> positions = new ArrayList<>();
        for (BlockPos at : BlockPos.betweenClosed(center.offset(-SHELL, -SHELL, -SHELL), center.offset(SHELL, SHELL, SHELL)))
            if (onShell(center, at)) positions.add(at.immutable());
        return positions;
    }

    private static boolean onShell(BlockPos center, BlockPos at) {
        int dx = Math.abs(at.getX() - center.getX()), dy = Math.abs(at.getY() - center.getY()), dz = Math.abs(at.getZ() - center.getZ());
        return Math.max(dx, Math.max(dy, dz)) == SHELL;
    }

    private ReactorStructure() {}
}
