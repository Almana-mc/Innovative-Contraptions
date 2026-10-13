package me.almana.innovativecontraptions.content.energy.storage;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.DischargeFuel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class ChargeStorageBlockEntity extends ElectricalBlockEntity {
    private Player contactedPlayer;
    private long contactTime;
    public ChargeStorageBlockEntity(BlockPos pos, BlockState state) { super(pos, state); }
    public void contact(Player player) {
        contactedPlayer = player;
        contactTime = level.getGameTime();
    }
    public Player chargingPlayer() {
        var player = contactedPlayer;
        contactedPlayer = null;
        return level.getGameTime() - contactTime <= 2 ? player : null;
    }
    @Override public boolean accepts(Direction side) { return side != facing() && (!kind.pad || side != Direction.UP); }
    @Override public boolean emits(Direction side) { return side == facing(); }
    @Override public int modeCount() { return kind.pad ? 2 : 7; }
    @Override public int sourceVoltage() { return kind.packet(); }
    @Override public int maxInputVoltage() { return kind.packet(); }
    @Override public int signal() {
        return (kind.pad ? active == (mode == 0) : StorageMode.signal(mode, energy, kind.capacity, kind.packet())) ? 15 : 0;
    }
    @Override public int availableEu() {
        return StorageMode.emit(mode, energy, kind.capacity, kind.packet(), level.hasNeighborSignal(worldPosition))
                ? super.availableEu() : 0;
    }
    @Override protected void tickEnergy() {
        int charged = BatteryItem.receiveEnergy(items.get(0), energy, kind.tier, false);
        energy -= charged;
        int supplied = DischargeFuel.burn(items.get(1), demandedEu(), true);
        if (supplied == 0) supplied = BatteryItem.extractEnergy(items.get(1), demandedEu(), kind.tier, false);
        energy += supplied;
        if (kind.pad && (level.getGameTime() & 1) == 0) active = ChargePad.charge(this);
        if (charged > 0 || supplied > 0) setChanged();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 1 && DischargeFuel.value(stack) > 0) return true;
        if (!(stack.getItem() instanceof BatteryItem item) || item.tier().ordinal() + 1 > kind.tier) return false;
        return slot == 0 ? item.rechargeable() && BatteryItem.stored(stack) < item.capacity()
                : slot == 1 && item.canSupplyEnergy() && BatteryItem.stored(stack) > 0;
    }
}
