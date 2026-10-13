package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.energy.storage.ElectricalMenu;
import me.almana.innovativecontraptions.content.generator.GeneratorMenu;
import me.almana.innovativecontraptions.content.machine.MaceratorMenu;
import me.almana.innovativecontraptions.content.tool.ToolboxMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<GeneratorMenu>> GENERATOR =
            MENUS.register("generator", () -> IMenuTypeExtension.create(GeneratorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<MaceratorMenu>> MACERATOR =
            MENUS.register("macerator", () -> IMenuTypeExtension.create(MaceratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ElectricalMenu>> ELECTRICAL =
            MENUS.register("electrical", () -> IMenuTypeExtension.create(ElectricalMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<ToolboxMenu>> TOOLBOX =
            MENUS.register("toolbox", () -> IMenuTypeExtension.create(ToolboxMenu::new));

    private ICMenus() {
    }
}
