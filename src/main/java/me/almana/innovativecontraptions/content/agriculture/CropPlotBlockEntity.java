package me.almana.innovativecontraptions.content.agriculture;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class CropPlotBlockEntity extends SavedBlockEntity {
    public static final int UPDATE_TICKS = 256;
    private CropGenome genome = CropGenome.EMPTY;
    private int age, progress, nutrients, water, treatment, ticks;
    private boolean crossing;
    private boolean harvestRolled;
    private int updates;
    int climateHumidity, humidityQuality, nutrientQuality, airQuality;
    boolean pendingFood;
    private List<ItemStack> pendingHarvest = List.of();

    public CropPlotBlockEntity(BlockPos pos, BlockState state) { super(FarmingContent.CROP.get(), pos, state); }
    public CropGenome genome() { return genome; }
    public CropSpecies species() { return genome.crop(); }
    public int age() { return age; }
    public int progress() { return progress; }
    public int nutrients() { return nutrients; }
    public int water() { return water; }
    public int treatment() { return treatment; }
    public boolean crossing() { return crossing; }
    public boolean empty() { return species() == CropSpecies.EMPTY; }
    public void tick() {
        if (!getBlockState().canSurvive(level, worldPosition)) { level.destroyBlock(worldPosition, true); return; }
        if (++ticks % UPDATE_TICKS == 0) updateCrop();
    }
    public void updateCrop() {
        CropEnvironment.refresh(this, ++updates);
        if (empty()) {
            if (crossing && CropBreeding.attempt(this)) return;
            if (treatment > 0) { if (level.getRandom().nextInt(10) == 0) treatment--; }
            else if (level.getRandom().nextInt(100) == 0) breed(new CropGenome("weed", 0, 0, 0, 0));
        } else {
            CropHazards.tick(this);
            wartMutation();
            if (age >= 2 && (species() == CropSpecies.WEED || genome.growth() >= 24
                    || species() == CropSpecies.VENOM_BLOOM && age == 5 && genome.growth() >= 8)
                    && level.getRandom().nextInt(50) - genome.growth() <= 2) CropBreeding.spreadWeed(this);
            if (age < species().stages && CropEnvironment.canGrow(this, species())) grow();
            if (nutrients > 0) nutrients--;
            if (water > 0) water--;
        }
        changed();
    }
    private void grow() {
        int speed = CropEnvironment.growth(this);
        if (speed < 0) { clearPlant(); return; }
        progress += speed;
        if (progress >= CropEnvironment.duration(this)) { age++; progress = 0; pendingHarvest = List.of(); harvestRolled = false; }
    }
    private void wartMutation() {
        if (species() == CropSpecies.NETHER_WART || species() == CropSpecies.TERRA_WART) {
            boolean nether = species() == CropSpecies.NETHER_WART;
            if (CropEnvironment.root(this, nether ? Blocks.SOUL_SAND : Blocks.SNOW_BLOCK)) progress += 100;
            else if (CropEnvironment.root(this, nether ? Blocks.SNOW_BLOCK : Blocks.SOUL_SAND)
                    && level.getRandom().nextInt(300) == 0) genome = genome.mutated(nether ? CropSpecies.TERRA_WART : CropSpecies.NETHER_WART);
        }
    }
    public boolean plant(CropGenome seed) {
        if (!empty() || crossing || seed.crop() == CropSpecies.EMPTY) return false;
        genome = seed; age = 1; progress = 0; pendingHarvest = List.of(); harvestRolled = false; changed(); return true;
    }
    public void breed(CropGenome seed) { crossing = false; plant(seed); }
    public boolean cross() {
        if (!empty() || crossing) return false;
        crossing = true; changed(); return true;
    }
    public void uncross() { crossing = false; changed(); }
    public void trample() {
        if (empty() || level.getRandom().nextInt(100) != 0 || level.getRandom().nextInt(40) <= genome.resistance()) return;
        clearPlant();
        if (level.getBlockState(worldPosition.below()).is(Blocks.FARMLAND)) level.setBlockAndUpdate(worldPosition.below(), Blocks.DIRT.defaultBlockState());
    }
    public void clearPlant() {
        genome = CropGenome.EMPTY; age = 0; progress = 0; pendingHarvest = List.of(); harvestRolled = false; pendingFood = false; changed();
    }
    public int fertilize(boolean automated) {
        if (nutrients >= 100) return 0;
        int amount = automated ? 90 : 100;
        nutrients += amount; changed(); return amount;
    }
    public int hydrate(int available) {
        int amount = Math.max(0, Math.min(200 - water, available));
        water += amount; if (amount > 0) changed(); return amount;
    }
    public int protect(int available, boolean automated) {
        int amount = Math.max(0, Math.min((automated ? 150 : 100) - treatment, available));
        treatment += amount; if (amount > 0) changed(); return amount;
    }
    public boolean resistWeed() {
        if (empty()) return false;
        if (level.getRandom().nextInt(32) < genome.resistance()) return true;
        if (treatment == 0) return false;
        treatment = Math.max(0, treatment - 5); changed(); return true;
    }
    public List<ItemStack> harvestPreview() {
        if (!species().harvestable(age)) return List.of();
        if (harvestRolled) return pendingHarvest.stream().map(ItemStack::copy).toList();
        double mean = species().yieldChance() * Math.pow(1.03, genome.gain());
        int count = Math.max(0, (int) Math.round(mean + level.getRandom().nextGaussian() * mean * .6827));
        List<ItemStack> drops = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            ItemStack drop = species().produce(age, level.getRandom());
            if (species() == CropSpecies.RED_GRAIN && !level.hasNeighborSignal(worldPosition) && level.getRandom().nextBoolean()) drop = new ItemStack(Items.WHEAT);
            List<ItemStack> rolled = new ArrayList<>(CropProducts.special(this));
            if (!drop.isEmpty()) rolled.addFirst(drop);
            for (ItemStack result : rolled) {
                if (level.getRandom().nextInt(100) <= genome.gain()) result.grow(1);
                drops.add(result);
            }
        }
        pendingHarvest = drops; harvestRolled = true;
        setChanged();
        return drops.stream().map(ItemStack::copy).toList();
    }
    public void finishHarvest() {
        if (!species().harvestable(age)) return;
        age = species() == CropSpecies.STICKREED && age == 4 ? 1 + level.getRandom().nextInt(3) : species().regrowth;
        progress = 0; pendingHarvest = List.of(); harvestRolled = false; changed();
    }
    public List<ItemStack> harvest() { var drops = harvestPreview(); finishHarvest(); return drops; }
    public ItemStack seed() {
        ItemStack stack = new ItemStack(ICItems.GENETIC_SEEDS.get());
        stack.set(FarmingContent.GENOME.get(), genome); return stack;
    }
    public List<ItemStack> pick() {
        if (empty() || species() == CropSpecies.WEED) return List.of();
        List<ItemStack> seeds = new ArrayList<>();
        double base = (age == 1 ? 0 : age == 2 ? .25 : .5) * Math.pow(.8, species().tier);
        double chance = base * Math.pow(1.1, genome.resistance());
        if (species().harvestable(age)) {
            if (level.getRandom().nextFloat() <= (chance + 1) * .8) seeds.add(pickedSeed());
            if (level.getRandom().nextFloat() <= (base + genome.growth() / 100.0) * Math.pow(.95, Math.max(0, genome.gain() - 23))) seeds.add(pickedSeed());
        } else if (level.getRandom().nextFloat() <= chance * 1.5) seeds.add(pickedSeed());
        clearPlant(); return seeds;
    }
    private ItemStack pickedSeed() {
        if (genome.growth() > 1 || genome.gain() > 1 || genome.resistance() > 1) return seed();
        return switch (species()) {
            case WHEAT -> new ItemStack(Items.WHEAT_SEEDS);
            case BEETROOT -> new ItemStack(Items.BEETROOT_SEEDS);
            case CARROT -> new ItemStack(Items.CARROT);
            case PUMPKIN -> new ItemStack(Items.PUMPKIN_SEEDS, 1 + level.getRandom().nextInt(3));
            case MELON -> new ItemStack(Items.MELON_SEEDS, 1 + level.getRandom().nextInt(2));
            default -> seed();
        };
    }
    public void addGrowth(int amount) { progress += amount; setChanged(); }
    public void setAge(int age) { this.age = Math.clamp(age, 0, species().stages); harvestRolled = false; changed(); }
    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide()) { ticks = level.getRandom().nextInt(UPDATE_TICKS); CropEnvironment.refresh(this, 0); } }
    public void changed() {
        setChanged();
        if (level == null || level.isClientSide()) return;
        BlockState state = getBlockState().setValue(CropPlotBlock.AGE, age)
                .setValue(CropPlotBlock.SPECIES, species().ordinal()).setValue(CropPlotBlock.CROSSING, crossing);
        if (state != getBlockState()) level.setBlock(worldPosition, state, 3);
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output); output.store("genome", CropGenome.CODEC, genome);
        output.putInt("age", age); output.putInt("progress", progress); output.putInt("nutrients", nutrients);
        output.putInt("water", water); output.putInt("treatment", treatment); output.putInt("ticks", ticks);
        output.putBoolean("crossing", crossing); output.store("pending_harvest", ItemStack.CODEC.listOf(), pendingHarvest);
        output.putBoolean("harvest_rolled", harvestRolled); output.putInt("updates", updates);
        output.putBoolean("pending_food", pendingFood);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input); genome = input.read("genome", CropGenome.CODEC).orElse(CropGenome.EMPTY);
        age = Math.clamp(input.getIntOr("age", 0), 0, species().stages); progress = input.getIntOr("progress", 0);
        nutrients = input.getIntOr("nutrients", 0); water = input.getIntOr("water", 0); treatment = input.getIntOr("treatment", 0);
        ticks = input.getIntOr("ticks", 0); crossing = input.getBooleanOr("crossing", false);
        pendingHarvest = input.read("pending_harvest", ItemStack.CODEC.listOf()).orElse(List.of());
        harvestRolled = input.getBooleanOr("harvest_rolled", false); updates = input.getIntOr("updates", 0);
        pendingFood = input.getBooleanOr("pending_food", false);
    }
}
