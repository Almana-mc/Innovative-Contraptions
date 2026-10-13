package me.almana.innovativecontraptions.compat.transfer.fluid;

import com.mojang.logging.LogUtils;
import me.almana.innovativecontraptions.compat.Capabilities;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandlerUtil;
import me.almana.innovativecontraptions.compat.transfer.access.ItemAccess;
import me.almana.innovativecontraptions.compat.transfer.resource.ResourceStack;
import me.almana.innovativecontraptions.compat.transfer.transaction.RootCommitJournal;
import me.almana.innovativecontraptions.compat.transfer.transaction.Transaction;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class FluidUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side, @Nullable TransactionContext transaction) {
        var fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pos, side);
        if (fluidHandler == null) return false;
        var handHandler = ItemAccess.forPlayerInteraction(player, hand).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (handHandler == null) return false;
        return moveWithSound(fluidHandler, handHandler, player.level(), pos, player, transaction, true) != null
                || moveWithSound(handHandler, fluidHandler, player.level(), pos, player, transaction, false) != null;
    }

    @Nullable
    private static ResourceStack<FluidResource> moveWithSound(ResourceHandler<FluidResource> from, ResourceHandler<FluidResource> to, Level level, @Nullable BlockPos pos,
            @Nullable Player player, @Nullable TransactionContext transaction, boolean pickup) {
        var moved = ResourceHandlerUtil.moveFirst(from, to, fr -> true, Integer.MAX_VALUE, transaction);
        if (moved != null) playSoundAndGameEvent(moved.resource(), level, pos, player, transaction, pickup);
        return moved;
    }

    private static void playSoundAndGameEvent(FluidResource resource, Level level, @Nullable BlockPos blockPos, @Nullable Player player, @Nullable TransactionContext transaction, boolean pickup) {
        Vec3 position = blockPos != null ? Vec3.atCenterOf(blockPos) : new Vec3(player.getX(), player.getY() + 0.5, player.getZ());
        if (transaction == null) {
            triggerSoundAndGameEvent(resource, level, position, player, pickup);
        } else {
            new RootCommitJournal(() -> triggerSoundAndGameEvent(resource, level, position, player, pickup)).updateSnapshots(transaction);
        }
    }

    private static void triggerSoundAndGameEvent(FluidResource resource, Level level, Vec3 position, @Nullable Player player, boolean pickup) {
        var soundEvent = resource.getFluidType().getSound(resource.toStack(FluidType.BUCKET_VOLUME), pickup ? SoundActions.BUCKET_FILL : SoundActions.BUCKET_EMPTY);
        if (soundEvent != null) {
            level.playSound(null, position.x, position.y, position.z, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        level.gameEvent(player, pickup ? GameEvent.FLUID_PICKUP : GameEvent.FLUID_PLACE, position);
    }

    public static FluidStack tryPickupFluid(@Nullable ResourceHandler<FluidResource> destination, @Nullable Player player, Level level, BlockPos pos, @Nullable Direction side, @Nullable TransactionContext transaction) {
        if (destination == null) return FluidStack.EMPTY;
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BucketPickup bucketPickup) {
            return tryPickupFluid(destination, player, level, pos, bucketPickup, transaction);
        }
        var fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pos, state, null, side);
        if (fluidHandler == null) return FluidStack.EMPTY;
        var moved = moveWithSound(fluidHandler, destination, level, pos, player, transaction, true);
        return moved != null ? moved.resource().toStack(moved.amount()) : FluidStack.EMPTY;
    }

    private static FluidStack tryPickupFluid(ResourceHandler<FluidResource> destination, @Nullable Player player, Level level, BlockPos pos, BucketPickup bucketPickup, @Nullable TransactionContext transaction) {
        Fluid fluid = level.getFluidState(pos).getType();
        if (fluid == Fluids.EMPTY) return FluidStack.EMPTY;
        try (var tx = Transaction.open(transaction)) {
            var resource = FluidResource.of(fluid);
            if (destination.insert(resource, FluidType.BUCKET_VOLUME, tx) != FluidType.BUCKET_VOLUME) return FluidStack.EMPTY;
            // Insertion may change the world
            if (level.getFluidState(pos).getType() != fluid) return FluidStack.EMPTY;
            ItemStack pickedUpStack = bucketPickup.pickupBlock(player, level, pos, level.getBlockState(pos));
            if (!(pickedUpStack.getItem() instanceof BucketItem bucket)) {
                if (!pickedUpStack.isEmpty()) {
                    LOGGER.warn("Picked up stack is not a bucket. Fluid {} at {} in {} picked up as {}.",
                            BuiltInRegistries.FLUID.getKey(fluid), pos, level.dimension().location(), pickedUpStack);
                }
                return FluidStack.EMPTY;
            }
            FluidStack extracted = new FluidStack(bucket.content, FluidType.BUCKET_VOLUME);
            if (!resource.matches(extracted)) {
                LOGGER.warn("Fluid removed without successfully being picked up. Fluid {} at {} in {} matched requested type, but after performing pickup was {}.",
                        BuiltInRegistries.FLUID.getKey(fluid), pos, level.dimension().location(), BuiltInRegistries.FLUID.getKey(bucket.content));
                return FluidStack.EMPTY;
            }
            playSoundAndGameEvent(resource, level, pos, player, tx, true);
            tx.commit();
            return extracted;
        }
    }

    public static FluidStack tryPlaceFluid(@Nullable ResourceHandler<FluidResource> source, @Nullable Player player, Level level, BlockPos pos, boolean validatePlaced, @Nullable TransactionContext transaction) {
        if (source == null) return FluidStack.EMPTY;
        for (int index = 0; index < source.size(); ++index) {
            var resource = source.getResource(index);
            if (resource.isEmpty()) continue;
            try (var tx = Transaction.open(transaction)) {
                if (source.extract(index, resource, FluidType.BUCKET_VOLUME, tx) != FluidType.BUCKET_VOLUME) continue;
                if (tryPlaceFluid(resource, player, level, pos, validatePlaced, tx)) {
                    tx.commit();
                    return resource.toStack(FluidType.BUCKET_VOLUME);
                }
            }
        }
        return FluidStack.EMPTY;
    }

    private static boolean tryPlaceFluid(FluidResource resource, @Nullable Player player, Level level, BlockPos pos, boolean validatePlaced, TransactionContext transaction) {
        var stack = resource.toStack(FluidType.BUCKET_VOLUME);
        var fluidType = resource.getFluidType();
        if (stack.isEmpty() || !fluidType.canBePlacedInLevel(level, pos, stack)) return false;
        BlockState destBlockState = level.getBlockState(pos);
        boolean isDestReplaceable = destBlockState.canBeReplaced(resource.getFluid());
        boolean canDestContainFluid = destBlockState.getBlock() instanceof LiquidBlockContainer lbc
                && lbc.canPlaceLiquid(player, level, pos, destBlockState, resource.getFluid());
        if (!destBlockState.isAir() && !isDestReplaceable && !canDestContainFluid) return false;
        if (fluidType.isVaporizedOnPlacement(level, pos, stack)) {
            fluidType.onVaporize(player, level, pos, stack);
            return true;
        }
        if (canDestContainFluid) {
            LiquidBlockContainer lbc = (LiquidBlockContainer) destBlockState.getBlock();
            if (!lbc.placeLiquid(level, pos, destBlockState, fluidType.getStateForPlacement(level, pos, stack)) && validatePlaced) return false;
        } else {
            if (!level.isClientSide() && isDestReplaceable && !destBlockState.liquid()) level.destroyBlock(pos, true);
            level.setBlock(pos, fluidType.getBlockForFluidState(level, pos, resource.getFluid().defaultFluidState()), Block.UPDATE_ALL_IMMEDIATE);
        }
        playSoundAndGameEvent(resource, level, pos, player, transaction, false);
        return true;
    }

    private FluidUtil() {}
}
