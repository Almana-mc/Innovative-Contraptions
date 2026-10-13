package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class ReactorLanguage {
    public static void add(BiConsumer<String, String> add) {
        add.accept("tooltip.innovativecontraptions.reactor.durability", "Durability: %s/%s");
        add.accept("tooltip.innovativecontraptions.reactor.heat_warning_1", "Can not be used in Fluid Reactor");
        add.accept("tooltip.innovativecontraptions.reactor.heat_warning_2", "Please cool down in the EU Reactor");
        add.accept("gui.innovativecontraptions.reactor.temperature", "Core Temperature: %s%%");
        add.accept("gui.innovativecontraptions.reactor.eu_output", "Output: %s EU/t");
        add.accept("gui.innovativecontraptions.reactor.hu_output", "Output: %s HU/s");
        add.accept("gui.innovativecontraptions.reactor.mode.electric", "Nuclear Reactor in EU mode");
        add.accept("gui.innovativecontraptions.reactor.mode.fluid", "Nuclear Reactor in Fluid Cooling mode");
        add.accept("gui.innovativecontraptions.injector.energy", "%s / %s EU");
        add.accept("gui.innovativecontraptions.exchanger.heat", "Heat: %s / %s HU/t");
        add.accept("fluid_type.innovativecontraptions.coolant", "IC2 Coolant");
        add.accept("fluid_type.innovativecontraptions.hot_coolant", "IC2 Hot Coolant");
        add.accept("jei.innovativecontraptions.category.liquid_heat_exchanger", "Liquid Heat Exchanger");
        add.accept("jei.innovativecontraptions.category.coolant_injector", "Reactor Coolant Injector");
        add.accept("jei.innovativecontraptions.exchanger_rate", "1 mB Hot Coolant becomes 1 mB IC2 Coolant and %s HU");
        add.accept("jei.innovativecontraptions.exchanger_conductors", "%s HU/t per Heat Conductor, up to %s; needs at least 2");
        add.accept("jei.innovativecontraptions.exchanger_front", "Heats the machine its front faces");
        add.accept("jei.innovativecontraptions.injector_refill", "Refills condensators more than %s%% used: 1 block and %s EU each");
        add.accept("jei.innovativecontraptions.injector_behind", "Back face against the reactor or a reactor chamber");
        add.accept("jei.innovativecontraptions.info.nuclear_reactor", "Runs while it gets a redstone signal. Every neutron pulse makes %s EU/t; fuel rods pulse themselves and their four neighbours, so touching rods make more power and more heat. Heat goes to neighbouring heat holders first and the rest into the hull. A hull over 40%% sets things on fire, over 50%% boils water, over 70%% irradiates, over 85%% turns blocks to lava, and at 100%% the reactor explodes. Reactor platings shrink the explosion.");
        add.accept("jei.innovativecontraptions.info.reactor_chamber", "Place against a Nuclear Reactor to add one column, up to %s chambers. Opens the reactor and passes on redstone, hopper items and EU (through cables).");
        add.accept("jei.innovativecontraptions.info.reactor_vessel", "A complete 5x5x5 shell of Pressure Vessels, Access Hatches, Fluid Ports and Redstone Ports around a reactor with 6 chambers makes a fluid reactor: no EU, vented heat turns IC2 Coolant into Hot Coolant at %s HU per mB.");
        add.accept("jei.innovativecontraptions.info.reactor_access_hatch", "Opens a fluid reactor and gives hoppers its inventory.");
        add.accept("jei.innovativecontraptions.info.reactor_redstone_port", "A redstone signal here turns a fluid reactor on.");
        add.accept("jei.innovativecontraptions.info.reactor_fluid_port", "Moves IC2 Coolant into and Hot Coolant out of a fluid reactor. Use a Fluid Ejector or Fluid Pulling Upgrade on it to fit it; use it with an empty hand to take the upgrade back.");
        add.accept("jei.innovativecontraptions.info.compact_item_buffer", "Crafting component for Reactor Coolant Injectors.");
        add.accept("jei.innovativecontraptions.info.rsh_condensator", "Soaks up heat and never releases it. Craft it with Redstone to restore %s each, or let an RSH Coolant Injector refill it.");
        add.accept("jei.innovativecontraptions.info.lzh_condensator", "Soaks up heat and never releases it. Craft it with Redstone (%s each) or Lapis Lazuli (%s each), or let an LZH Coolant Injector refill it.");
        add.accept("jei.innovativecontraptions.info.coolant_injector", "Refills matching condensators in the reactor or reactor chamber behind it when they are more than %s%% used, using one refill block and %s EU each. Takes MV power on every face except the back.");
    }

    private ReactorLanguage() {}
}
