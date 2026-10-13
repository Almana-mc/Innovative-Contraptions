package me.almana.innovativecontraptions.content.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class KineticWindTurbineBlockEntity extends GeneratorBlockEntityBase implements WindRotorVisual {
    private long budgetTick = Long.MIN_VALUE;
    private int spent;
    private int wearTicks;
    private float wind;
    private int health;
    private int status;

    public KineticWindTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(KineticContent.TURBINE.get(), pos, state, 2048, 1);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KineticWindTurbineBlockEntity entity) {
        entity.updateOutput();
        if (entity.euBuffer > 0 && ++entity.wearTicks >= 20) {
            entity.wearTicks = 0;
            ItemStack rotor = entity.getItem(0);
            RotorTier tier = ((RotorItem) rotor.getItem()).tier();
            int damage = rotor.getDamageValue() + (entity.wind > tier.maxWind ? 4 : 1);
            if (damage >= rotor.getMaxDamage()) entity.setItem(0, ItemStack.EMPTY);
            else rotor.setDamageValue(damage);
            entity.updateOutput();
            entity.setChanged();
        }
        if (level.getGameTime() % 10 == 0) level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }

    private void updateOutput() {
        long now = level.getGameTime();
        if (budgetTick != now) {
            budgetTick = now;
            spent = 0;
        }
        ItemStack stack = getItem(0);
        euBuffer = 0;
        health = 0;
        status = 0;
        if (!(stack.getItem() instanceof RotorItem rotor) || stack.getDamageValue() >= stack.getMaxDamage()) return;
        health = (int) (10000L * (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage());
        wind = WindState.strength(level, worldPosition);
        status = 1;
        if (wind < rotor.tier().minWind) return;
        status = 2;
        if (!WindTurbineMath.clearRotor(level, worldPosition, facing(), rotor.tier().radius)) return;
        status = 3;
        euBuffer = Math.max(0, Math.round(wind * rotor.tier().efficiency * GeneratorRates.WIND_KU_MULTIPLIER));
    }

    public int extractKinetic(Direction side, int amount) {
        if (level == null || level.isClientSide() || side != facing().getOpposite() || amount <= 0) return 0;
        updateOutput();
        int taken = Math.min(amount, Math.max(0, euBuffer - spent));
        spent += taken;
        return taken;
    }

    public Direction facing() { return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING); }
    @Override public int availableEu() { return 0; }
    @Override public int extractEu(int amount) { return 0; }
    @Override public float speed() { return euBuffer / 12.0F; }
    @Override public boolean hasRotor() { return getItem(0).getItem() instanceof RotorItem; }
    @Override public float rotorRadius() { return hasRotor() ? ((RotorItem) getItem(0).getItem()).tier().radius + 0.5F : 1; }
    @Override protected float currentOutput() { return euBuffer; }
    @Override protected int operatingState() { return status; }
    @Override protected int burnTicks() { return health; }
    @Override protected int burnTicksMax() { return 10000; }
    @Override protected void setBurnTicks(int value) { health = value; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == 0 && stack.getItem() instanceof RotorItem; }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("rotor_wear_ticks", wearTicks);
        output.putInt("rotor_health", health);
        output.putLong("kinetic_budget_tick", budgetTick);
        output.putInt("kinetic_spent", spent);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        wearTicks = input.getIntOr("rotor_wear_ticks", 0);
        health = input.getIntOr("rotor_health", 0);
        budgetTick = input.getLongOr("kinetic_budget_tick", Long.MIN_VALUE);
        spent = input.getIntOr("kinetic_spent", 0);
    }
}
