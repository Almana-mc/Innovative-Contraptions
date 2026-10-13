package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class FermentingBarrelBlockEntity extends SavedBlockEntity {
    public static final int[] AGE_THRESHOLDS = {24_000, 72_000, 216_000, 648_000, 17_496_000};
    private int water, wheat, hops, cane, age, maturity;
    private boolean opened;
    private DrinkBlend blend = DrinkBlend.SPOILED;
    public FermentingBarrelBlockEntity(BlockPos pos, BlockState state) { super(FarmingContent.BARREL.get(), pos, state); }
    public int water() { return water; }
    public int wheat() { return wheat; }
    public int hops() { return hops; }
    public int age() { return age; }
    public int maturity() { return maturity; }
    public boolean opened() { return opened; }
    public boolean rum() { return cane > 0; }
    public boolean tapped() { return getBlockState().getValue(FermentingBarrelBlock.TAPPED); }
    public void tick() {
        if (tapped() || (water == 0 && cane == 0)) return;
        age++;
        if (cane == 0 && maturity < 5 && age >= AGE_THRESHOLDS[maturity]) { age = 0; maturity++; }
        setChanged();
    }
    public int addWater(int units) {
        if (opened || cane > 0) return 0;
        int accepted = Math.clamp(units, 0, 32 - water); water += accepted; if (accepted > 0) setChanged(); return accepted;
    }
    public int addSolid(ItemStack stack, int maximum) {
        if (opened || cane > 0) return 0;
        boolean hop = stack.is(ICItems.HOPS.get());
        if (!hop && !stack.is(Items.WHEAT)) return 0;
        int added = Math.min(Math.min(maximum, stack.getCount()), 64 - (hop ? hops : wheat));
        if (added == 0) return 0;
        if (hop) hops += added; else wheat += added;
        disturbed(); setChanged(); return added;
    }
    public int addCane(int requested) {
        if (opened || water > 0 || wheat > 0 || hops > 0 || age > 600) return 0;
        int added = Math.clamp(requested, 0, 32 - cane); cane += added; setChanged(); return added;
    }
    private void disturbed() {
        if (maturity == 0) age = 0;
        else if (maturity == 1) {
            int change = level.getRandom().nextInt(4);
            if (change < 2) { maturity = 0; age = 0; } else if (change == 2) maturity = 5;
        } else if (maturity >= 3 || level.getRandom().nextBoolean()) maturity = 5;
    }
    public ItemStack draw() {
        if (!tapped() || (water == 0 && cane == 0)) return ItemStack.EMPTY;
        if (!opened) {
            blend = cane > 0 ? new DrinkBlend(0, 0, age >= rumThreshold(cane) ? 4 : 5, true) : DrinkBlend.beer(hops, wheat, water, maturity);
            opened = true;
        }
        ItemStack result = new ItemStack(ICItems.BEER.get());
        result.set(FarmingContent.BLEND.get(), new DrinkBlend(blend.alcohol(), blend.strength(),
                blend.rum() ? (age >= rumThreshold(cane) ? 4 : 5) : Math.max(blend.maturity(), maturity), blend.rum()));
        discardUnit(); setChanged(); return result;
    }
    public static int rumThreshold(int amount) { return (int) (1200 * amount * Math.pow(.95, amount - 1)); }
    public void discardUnit() {
        if (cane > 0) cane--; else if (water > 0) water--;
        if (water == 0 && cane == 0) { wheat = 0; hops = 0; age = 0; maturity = 0; opened = false; blend = DrinkBlend.SPOILED; }
        setChanged();
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output); output.putInt("water", water); output.putInt("wheat", wheat); output.putInt("hops", hops);
        output.putInt("cane", cane); output.putInt("age", age); output.putInt("maturity", maturity); output.putBoolean("opened", opened);
        output.store("blend", DrinkBlend.CODEC, blend);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input); water = input.getIntOr("water", 0); wheat = input.getIntOr("wheat", 0); hops = input.getIntOr("hops", 0);
        cane = input.getIntOr("cane", 0); age = input.getIntOr("age", 0); maturity = input.getIntOr("maturity", 0); opened = input.getBooleanOr("opened", false);
        blend = input.read("blend", DrinkBlend.CODEC).orElse(DrinkBlend.SPOILED);
    }
}
