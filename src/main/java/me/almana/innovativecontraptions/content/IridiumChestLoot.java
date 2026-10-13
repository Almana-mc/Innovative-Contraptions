package me.almana.innovativecontraptions.content;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.event.LootTableLoadEvent;

public final class IridiumChestLoot {
    public static void load(LootTableLoadEvent event) {
        if (!event.getName().getNamespace().equals("minecraft")) return;
        int[] weights = switch (event.getName().getPath()) {
            case "chests/simple_dungeon" -> new int[]{64, 2, 1, 2, 10, 6, 14};
            case "chests/stronghold_corridor" -> new int[]{88, 1, 1, 4, 8, 4, 14};
            case "chests/stronghold_crossing", "chests/stronghold_library" -> new int[]{88, 1, 1, 1, 8, 2, 5};
            case "chests/abandoned_mineshaft", "chests/nether_bridge" -> new int[]{68, 0, 0, 0, 8, 2, 5};
            case "chests/desert_pyramid", "chests/end_city_treasure", "chests/igloo_chest", "chests/jungle_temple" ->
                    new int[]{86, 0, 0, 0, 8, 1, 2};
            case "chests/village/village_weaponsmith" -> new int[]{104, 0, 0, 0, 8, 3, 7};
            default -> null;
        };
        if (weights == null) return;
        var pool = LootPool.lootPool().name("innovativecontraptions_iridium").setRolls(UniformGenerator.between(1, 3))
                .add(EmptyLootItem.emptyItem().setWeight(weights[0] - weights[1] - weights[4]))
                .add(LootItem.lootTableItem(ICItems.IRIDIUM_SHARD.get()).setWeight(weights[4])
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(weights[5], weights[6]))));
        if (weights[1] > 0) pool.add(LootItem.lootTableItem(ICItems.IRIDIUM_ORE.get()).setWeight(weights[1])
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(weights[2], weights[3]))));
        event.getTable().addPool(pool.build());
    }
}
