package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Map;

public class ICLanguageProvider extends LanguageProvider {
    private static final Map<String, String> OVERRIDES = Map.ofEntries(
            Map.entry("tree_tap", "Treetap"),
            Map.entry("uu_matter", "UU-Matter"),
            Map.entry("uranium_235", "Uranium 235"),
            Map.entry("uranium_238", "Uranium 238"),
            Map.entry("small_uranium_235", "Tiny Pile of Uranium 235"),
            Map.entry("small_uranium_238", "Tiny Pile of Uranium 238"),
            Map.entry("mox", "MOX Nuclear Fuel"),
            Map.entry("mox_pellet", "Pellets of MOX Nuclear Fuel"),
            Map.entry("depleted_mox", "Fuel Rod (Depleted MOX)"),
            Map.entry("depleted_dual_mox", "Dual Fuel Rod (Depleted MOX)"),
            Map.entry("depleted_quad_mox", "Quad Fuel Rod (Depleted MOX)"),
            Map.entry("rtg_pellet", "Pellets of RTG Fuel"),
            Map.entry("batbox", "BatBox"),
            Map.entry("cesu", "CESU"),
            Map.entry("mfe", "MFE"),
            Map.entry("mfsu", "MFSU"),
            Map.entry("batbox_pad", "BatBox Charge Pad"),
            Map.entry("cesu_pad", "CESU Charge Pad"),
            Map.entry("mfe_pad", "MFE Charge Pad"),
            Map.entry("mfsu_pad", "MFSU Charge Pad"),
            Map.entry("lv_transformer", "LV Transformer"),
            Map.entry("mv_transformer", "MV Transformer"),
            Map.entry("hv_transformer", "HV Transformer"),
            Map.entry("ev_transformer", "EV Transformer"),
            Map.entry("double_insulated_gold_cable", "Double-Insulated Gold Cable"),
            Map.entry("hv_cable", "HV Cable"),
            Map.entry("insulated_hv_cable", "Insulated HV Cable"),
            Map.entry("re_battery", "RE Battery"),
            Map.entry("advanced_re_battery", "Advanced RE Battery"),
            Map.entry("charging_re_battery", "Charging RE Battery"),
            Map.entry("advanced_charging_re_battery", "Advanced Charging RE Battery"),
            Map.entry("uu_matter_cell", "UU-Matter Cell"),
            Map.entry("od_scanner", "OD Scanner"),
            Map.entry("ov_scanner", "OV Scanner"),
            Map.entry("lzh_condensator", "LZH-Condensator"),
            Map.entry("rsh_condensator", "RSH-Condensator"),
            Map.entry("cf_pack", "CF Pack"),
            Map.entry("fuel_rod_uranium", "Fuel Rod (Uranium)"),
            Map.entry("dual_fuel_rod_uranium", "Dual Fuel Rod (Uranium)"),
            Map.entry("quad_fuel_rod_uranium", "Quad Fuel Rod (Uranium)"),
            Map.entry("fuel_rod_mox", "Fuel Rod (MOX)"),
            Map.entry("dual_fuel_rod_mox", "Dual Fuel Rod (MOX)"),
            Map.entry("quad_fuel_rod_mox", "Quad Fuel Rod (MOX)"),
            Map.entry("uranium", "Enriched Uranium Nuclear Fuel"),
            Map.entry("uranium_pellet", "Pellets of Enriched Uranium Nuclear Fuel"),
            Map.entry("small_plutonium", "Tiny Pile of Plutonium"),
            Map.entry("depleted_uranium", "Fuel Rod (Depleted Uranium)"),
            Map.entry("depleted_dual_uranium", "Dual Fuel Rod (Depleted Uranium)"),
            Map.entry("depleted_quad_uranium", "Quad Fuel Rod (Depleted Uranium)"),
            Map.entry("heat_storage", "10k Coolant Cell"),
            Map.entry("tri_heat_storage", "30k Coolant Cell"),
            Map.entry("hex_heat_storage", "60k Coolant Cell"),
            Map.entry("plating", "Reactor Plating"),
            Map.entry("heat_plating", "Heat-Capacity Reactor Plating"),
            Map.entry("containment_plating", "Containment Reactor Plating"),
            Map.entry("iridium_reflector", "Iridium Neutron Reflector"),
            Map.entry("heatpack", "Heating Cell"),
            Map.entry("reactor_vessel", "Reactor Pressure Vessel"),
            Map.entry("rsh_coolant_injector", "Reactor Coolant Injector (RSH)"),
            Map.entry("lzh_coolant_injector", "Reactor Coolant Injector (LZH)"),
            Map.entry("energy_blade", "Nano Saber"),
            Map.entry("crop_treatment_powder", "Grin Powder"),
            Map.entry("crop_treatment_cell", "Weed-EX Cell"),
            Map.entry("ground_coffee", "Coffee Powder"),
            Map.entry("mug", "Stone Mug"),
            Map.entry("fermenting_barrel", "Booze Barrel"),
            Map.entry("genetic_seeds", "Seed Bag"),
            Map.entry("hazmat_helmet", "Scuba Helmet"),
            Map.entry("alloy_chestplate", "Composite Vest"),
            Map.entry("electric_tap", "Electric Treetap"),
            Map.entry("toolbox", "Tool Box"),
            Map.entry("iron_saw_disc", "Block Cutting Blade (Iron)"),
            Map.entry("steel_saw_disc", "Block Cutting Blade (Steel)"),
            Map.entry("diamond_saw_disc", "Block Cutting Blade (Diamond)"),
            Map.entry("block_slicer", "Block Cutting Machine"),
            Map.entry("canning_machine", "Fluid/Solid Canning Machine"),
            Map.entry("copper_casing", "Copper Item Casing"),
            Map.entry("tin_casing", "Tin Item Casing"),
            Map.entry("bronze_casing", "Bronze Item Casing"),
            Map.entry("gold_casing", "Gold Item Casing"),
            Map.entry("iron_casing", "Iron Item Casing"),
            Map.entry("steel_casing", "Steel Item Casing"),
            Map.entry("lead_casing", "Lead Item Casing"),
            Map.entry("machine_casing", "Basic Machine Casing"),
            Map.entry("alloy_ingot", "Advanced Alloy"),
            Map.entry("redstone_inverter_upgrade", "Redstone Signal Inverter Upgrade"),
            Map.entry("fuel_rod", "Fuel Rod (Empty)"),
            Map.entry("bio_compound", "Bio Chaff"),
            Map.entry("bio_digester", "Fermenter"),
            Map.entry("liquid_fuel_generator", "Semifluid Generator"),
            Map.entry("pattern_scanner", "Scanner"),
            Map.entry("raw_crystal_memory", "Crystal Memory (raw)"),
            Map.entry("structural_stone", "Reinforced Stone"),
            Map.entry("empty_cell", "Universal Fluid Cell")
    );

    public ICLanguageProvider(PackOutput output) {
        super(output, InnovativeContraptions.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("tooltip.innovativecontraptions.upgrade.output", "Automatically output to the %s side");
        add("tooltip.innovativecontraptions.upgrade.output_any", "Automatically output to the first valid side");
        add("tooltip.innovativecontraptions.upgrade.input", "Automatically input from the %s side");
        add("tooltip.innovativecontraptions.upgrade.input_any", "Automatically input from the first valid side");
        add("tooltip.innovativecontraptions.upgrade_direction_controls", "Sneak-use a block face to select it; use the same face again for any side");
        add("tooltip.innovativecontraptions.upgrade.time", "Decrease process time to %s%%");
        add("tooltip.innovativecontraptions.upgrade.power", "Increase power to %s%%");
        add("tooltip.innovativecontraptions.upgrade.tier", "Increase energy input tier by %s");
        add("tooltip.innovativecontraptions.upgrade.storage", "Increase energy storage by %s EU");
        add("tooltip.innovativecontraptions.upgrade.redstone", "Inverts the current redstone signal");
        add("tooltip.innovativecontraptions.blade_hardness", "Hardness: %s");
        for (var direction : Direction.values()) {
            String name = direction.getName();
            add("tooltip.innovativecontraptions.direction." + name, Character.toUpperCase(name.charAt(0)) + name.substring(1));
        }
        add("jei.innovativecontraptions.recycling_chance", "Scrap: 1 in 8");
        add("fluid_type.innovativecontraptions.biogas", "Biogas");
        EnergyPartTwoLanguage.add(this::add);
        BioenergyLanguage.add(this::add);
        MatterLanguage.add(this::add);
        GatheringLanguage.add(this::add);
        ICProcessingLanguage.add(this::add);
        ThermalLanguage.add(this::add);
        KineticLanguage.add(this::add);
        FarmingLanguage.add(this::add);
        EquipmentLanguage.add(this::add);
        ReactorLanguage.add(this::add);
        add("tooltip.innovativecontraptions.battery_energy", "Stored: %s / %s EU");
        add("tooltip.innovativecontraptions.automatic_charging", "Automatically charges compatible hotbar equipment");
        add("itemGroup." + InnovativeContraptions.MODID, "Innovative Contraptions");
        add("jei.innovativecontraptions.category.generator", "Generator");
        add("jei.innovativecontraptions.category.solar_panel", "Solar Panel");
        add("jei.innovativecontraptions.category.water_mill", "Water Mill");
        add("jei.innovativecontraptions.category.windmill", "Windmill");
        add("jei.innovativecontraptions.category.geothermal_generator", "Geothermal Generator");
        add("jei.innovativecontraptions.category.kinetic_generator", "Kinetic Generator");
        add("jei.innovativecontraptions.category.macerator", "Macerator");
        add("jei.innovativecontraptions.burn_time", "Duration: %s ticks (%s seconds)");
        add("jei.innovativecontraptions.energy_rate", "Energy: %s EU/t");
        add("jei.innovativecontraptions.potential_energy", "Potential energy: %s EU");
        add("jei.innovativecontraptions.total_energy", "Total energy: %s EU");
        add("jei.innovativecontraptions.duration_continuous", "Duration: Continuous while conditions hold");
        add("jei.innovativecontraptions.solar_condition", "Needs skylight 15 and no rain");
        add("jei.innovativecontraptions.water_bucket_independent",
                "Bucket generation works without surrounding water");
        add("jei.innovativecontraptions.water_modes_stack", "Bucket and passive generation stack");
        add("jei.innovativecontraptions.water_surrounding",
                "Needs connected water in the surrounding 3x3x3; up to %s blocks");
        add("jei.innovativecontraptions.water_passive", "Energy: %s EU/t per water; %s EU/t maximum");
        add("jei.innovativecontraptions.water_scan", "Water is checked every %s ticks");
        add("jei.innovativecontraptions.wind_duration", "Duration: Depends on wind and rotor wear");
        add("jei.innovativecontraptions.wind_energy", "Energy: 0-%s EU/t at normal speed");
        add("jei.innovativecontraptions.wind_overspeed", "Energy while overspeeding: %s EU/t");
        add("jei.innovativecontraptions.wind_no_water", "Water is not required");
        add("jei.innovativecontraptions.geothermal_condition", "Needs lava source blocks on adjacent faces");
        add("jei.innovativecontraptions.geothermal_energy", "Energy: %s EU/t per face; %s EU/t maximum");
        add("jei.innovativecontraptions.kinetic_condition", "Needs redstone signal strength 1-15");
        add("jei.innovativecontraptions.kinetic_energy", "Energy: %s EU/t per level; %s EU/t maximum");
        add("jei.innovativecontraptions.process_time", "Process time: %s ticks");
        for (var holder : ICItems.ITEMS.getEntries()) {
            String path = holder.getId().getPath();
            add(holder.get(), OVERRIDES.getOrDefault(path, titleCase(path)));
        }
    }

    private static String titleCase(String path) {
        String[] parts = path.split("_");
        StringBuilder name = new StringBuilder();
        for (String part : parts) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return name.toString();
    }
}
