package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuSource;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class GeneratorBlockEntityBase extends SavedBlockEntity implements EuSource, Container, MenuProvider {
    protected final int bufferMax;
    protected final NonNullList<ItemStack> items;
    protected int euBuffer = 0;
    private int syncedOutput;
    private int syncedState;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> euBuffer;
                case 1 -> bufferMax;
                case 2 -> burnTicks();
                case 3 -> burnTicksMax();
                case 4 -> level != null && level.isClientSide() ? syncedOutput : Math.round(currentOutput() * 100.0F);
                case 5 -> Math.round(currentOutput() * 100.0F) >>> 16;
                case 6 -> level != null && level.isClientSide() ? syncedState : operatingState();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> euBuffer = value;
                case 2 -> setBurnTicks(value);
                case 3 -> setBurnTicksMax(value);
                case 4 -> syncedOutput = (syncedOutput & 0xffff0000) | (value & 0xffff);
                case 5 -> syncedOutput = (syncedOutput & 0xffff) | ((value & 0xffff) << 16);
                case 6 -> syncedState = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    protected int burnTicks() { return 0; }
    protected int burnTicksMax() { return 0; }
    protected void setBurnTicks(int value) {}
    protected void setBurnTicksMax(int value) {}
    protected int operatingState() { return 0; }

    /** EU produced per tick while active, for the GUI's power-bar tooltip. */
    protected float currentOutput() { return 0.0F; }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new GeneratorMenu(containerId, inventory, this);
    }

    public static InteractionResult openMenu(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof GeneratorBlockEntityBase be) {
            serverPlayer.openMenu(be, pos);
        }
        return InteractionResult.SUCCESS;
    }

    protected GeneratorBlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state, int bufferMax, int slotCount) {
        super(type, pos, state);
        this.bufferMax = bufferMax;
        this.items = NonNullList.withSize(slotCount, ItemStack.EMPTY);
    }

    protected void chargeBattery() {
        if (euBuffer <= 0) return;
        ItemStack battery = items.get(0);
        int accepted = BatteryItem.receiveEnergy(battery, Math.min(euBuffer, sourceVoltage()),
                EuTier.fromVolts(sourceVoltage()).ordinal() + 1, false);
        if (accepted > 0) {
            euBuffer -= accepted;
            setChanged();
        }
    }

    @Override
    public int sourceVoltage() { return EuTier.LV.volts; }

    @Override
    public int availableEu() { return euBuffer; }

    @Override
    public int extractEu(int amount) {
        int taken = Math.min(euBuffer, Math.max(0, amount));
        euBuffer -= taken;
        if (taken > 0) setChanged();
        return taken;
    }

    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.clear(); setChanged(); }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot != 0 || stack.getItem() instanceof BatteryItem battery && battery.rechargeable()
                && battery.tier().volts <= sourceVoltage() && BatteryItem.stored(stack) < battery.capacity();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("eu_buffer", euBuffer);
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) output.store("item_" + i, ItemStack.CODEC, items.get(i));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        euBuffer = input.getIntOr("eu_buffer", 0);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, input.read("item_" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        }
    }
}
