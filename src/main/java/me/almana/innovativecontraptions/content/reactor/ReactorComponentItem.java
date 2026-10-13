package me.almana.innovativecontraptions.content.reactor;

import java.util.function.Consumer;
import java.util.function.Supplier;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;

public final class ReactorComponentItem extends Item {
    private final ReactorPart part;
    private final Supplier<? extends Item> spent;

    public ReactorComponentItem(Properties properties, ReactorPart part) {
        this(properties, part, null);
    }

    public ReactorComponentItem(Properties properties, ReactorPart part, Supplier<? extends Item> spent) {
        super(properties);
        this.part = part;
        this.spent = spent;
    }

    public ReactorPart part() { return part; }
    public ItemStack spentStack() { return new ItemStack(spent.get()); }
    public boolean insertable() { return part != ReactorPart.INERT; }

    public static int damage(ItemStack stack) { return stack.getOrDefault(ICDataComponents.REACTOR_DAMAGE.get(), 0); }

    public static void storeDamage(ItemStack stack, int amount) {
        if (amount <= 0) stack.remove(ICDataComponents.REACTOR_DAMAGE.get());
        else stack.set(ICDataComponents.REACTOR_DAMAGE.get(), amount);
    }

    @Override public boolean isBarVisible(ItemStack stack) { return part.durability > 0; }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13F - 13F * damage(stack) / part.durability); }
    @Override public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(Math.max(0F, 1F - (float) damage(stack) / part.durability) / 3F, 1F, 1F);
    }

    //? if >=26.1 {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> lines, TooltipFlag flag) {
    //?} else {
    /*@Override public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> lines = components::add;
    *///?}
        if (part.durability == 0) return;
        lines.accept(Component.translatable("tooltip.innovativecontraptions.reactor.durability", part.durability - damage(stack), part.durability));
        if (part.kind != ReactorPart.Kind.STORAGE || damage(stack) == 0) return;
        lines.accept(Component.translatable("tooltip.innovativecontraptions.reactor.heat_warning_1"));
        lines.accept(Component.translatable("tooltip.innovativecontraptions.reactor.heat_warning_2"));
    }
}
