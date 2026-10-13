package me.almana.innovativecontraptions.content.reactor;

import javax.annotation.Nullable;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class CoolantInjectorBlockEntity extends ThermalInventory implements EuSink {
    public static final int REFILLS = 9, UPGRADES = 4, SLOTS = REFILLS + UPGRADES, CAPACITY = 48_000, REFILL_EU = 1_000;
    public static final double REFILL_AT = 0.85;
    private static final int[] REFILL_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private int energy;
    private boolean active;

    public CoolantInjectorBlockEntity(BlockPos pos, BlockState state) {
        super(ReactorContent.INJECTOR.get(), pos, state, SLOTS);
    }

    public static ReactorPart condensator(boolean lzh) { return lzh ? ReactorPart.LZH_CONDENSATOR : ReactorPart.RSH_CONDENSATOR; }
    public static Item refill(boolean lzh) { return lzh ? Items.LAPIS_BLOCK : Items.REDSTONE_BLOCK; }
    private boolean lzh() { return getBlockState().is(ICBlocks.LZH_COOLANT_INJECTOR.get()); }
    boolean active() { return active; }

    @Override public void tick() {
        for (int slot = REFILLS; slot < SLOTS; slot++) ProcessingAutomation.transfer(this, this, items.get(slot), null, null);
        NuclearReactorBlockEntity reactor = target();
        active = reactor != null && energy >= REFILL_EU && hasRefill();
        if (active) refill(reactor);
    }

    private void refill(NuclearReactorBlockEntity reactor) {
        ReactorPart wanted = condensator(lzh());
        for (int slot = 0; slot < NuclearReactorBlockEntity.GRID && energy >= REFILL_EU; slot++) {
            ItemStack stack = reactor.getItem(slot);
            if (!(stack.getItem() instanceof ReactorComponentItem item) || item.part() != wanted) continue;
            if ((double) ReactorComponentItem.damage(stack) / wanted.durability <= REFILL_AT || !consumeRefill()) continue;
            ReactorComponentItem.storeDamage(stack, 0);
            energy -= REFILL_EU;
            reactor.setChanged();
            setChanged();
        }
    }

    @Nullable
    private NuclearReactorBlockEntity target() {
        BlockPos behind = worldPosition.relative(getBlockState().getValue(BlockStateProperties.FACING).getOpposite());
        if (level.getBlockEntity(behind) instanceof NuclearReactorBlockEntity reactor) return reactor;
        return level.getBlockState(behind).is(ICBlocks.REACTOR_CHAMBER.get()) ? ReactorStructure.adjacent(level, behind) : null;
    }

    private boolean hasRefill() {
        for (int slot = 0; slot < REFILLS; slot++) if (items.get(slot).is(refill(lzh()))) return true;
        return false;
    }

    private boolean consumeRefill() {
        for (int slot = 0; slot < REFILLS; slot++) {
            if (!items.get(slot).is(refill(lzh()))) continue;
            items.get(slot).shrink(1);
            return true;
        }
        return false;
    }

    @Override public int maxInputVoltage() { return EuTier.MV.volts; }
    @Override public int demandedEu() { return Math.max(0, CAPACITY - energy); }
    @Override public int receiveEu(int voltage, int amount) {
        int accepted = Math.max(0, Math.min(amount, CAPACITY - energy));
        if (accepted > 0) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < REFILLS) return stack.is(refill(lzh()));
        return stack.is(ICItems.PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get());
    }
    @Override public int[] getSlotsForFace(Direction side) { return REFILL_SLOTS; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
    @Override protected int displayValue(int index) {
        return switch (index) {
            case 0 -> energy & 0xFFFF;
            case 1 -> energy >>> 16;
            case 2 -> active ? 1 : 0;
            default -> 0;
        };
    }
    @Override protected void receiveDisplayValue(int index, int value) { }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new CoolantInjectorMenu(id, inventory, this); }

    @Override public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
    }

    @Override public void setRemoved() {
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
        super.setRemoved();
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", energy);
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = input.getIntOr("energy", 0);
    }
}
