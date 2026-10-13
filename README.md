# Innovative Contraptions

NeoForge tech mod (IC2-style machines, EU cables, armor, crops) built from one source tree for two Minecraft versions with Stonecutter.

| Minecraft | NeoForge | Java | JEI |
|---|---|---|---|
| 1.21.1 | 21.1.256 | 21 | 19.53.0.426 |
| 26.1.2 | 26.1.2.81 | 25 | 29.29.0.81 |

## Build

```
./gradlew build
```

Builds both versions. Jars land in `versions/<v>/build/libs/`.

## Run

```
./gradlew :1.21.1:runClient
./gradlew :26.1.2:runClient
./gradlew :1.21.1:runServer
./gradlew :26.1.2:runServer
```

## Tests

```
./gradlew :1.21.1:runGameTestServer
./gradlew :26.1.2:runGameTestServer
```

201 GameTests, shared by both versions. Expect `All 201 required tests passed`. A single test or group can be selected with `-Ptests=innovativecontraptions:<glob>`, 26.1.2 only.

Client-only checks (GUIs, JEI, renderers, armor, fluids) are scenarios in `agent-tests/`; see `agent-tests/README.md`.

## Datagen

```
./gradlew :1.21.1:runData
./gradlew :26.1.2:runData
```

Writes `versions/<v>/src/generated/resources`. Commit both outputs. Recipes must come from datagen, never hand-written JSON.

## Stonecutter workflow

The VCS and active version is 26.1.2. All shared source (`src/main/java`) is written against the 26.1.2 API. 1.21.1 differences are handled, in order of preference, by:

1. Replacements in `stonecutter.gradle.kts`. Renames only, and they must be bijective. They also run over commented 1.21.1 branches, so use 26.1.2 tokens everywhere.
2. 1.21.1-only polyfills in `versions/1.21.1/src/main/java/me/almana/innovativecontraptions/compat/`. They mirror 26.1 APIs with the same simple names; an import swap replacement points shared code at them.
3. Inline conditionals for one or two sites, with the 1.21.1 branch commented out while 26.1.2 is active. From `content/tool/WrenchableSupport.java`:

```java
//? if >=26.1 {
public static InteractionResult useItemOnResult(ItemStack stack) {
    return stack.getItem() instanceof WrenchTool || stack.getItem() instanceof FluidCellItem
            ? InteractionResult.PASS : InteractionResult.TRY_WITH_EMPTY_HAND;
}
//?} else {
/*public static ItemInteractionResult useItemOnResult(ItemStack stack) {
    return stack.getItem() instanceof WrenchTool || stack.getItem() instanceof FluidCellItem
            ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
}
*///?}
```

4. Per-version files under `versions/<v>/src/main/java` when a class is more than 50% different (same FQN in both versions, removed from the shared tree).

`versions/*/src` is not processed by replacements. Write it in that version's own names (`ResourceLocation`, `level.dimension().location()` on 1.21.1).

Besides `compat/`, these classes exist only on 1.21.1: `content/equipment/Gear`, `content/equipment/WornItem`, `client/equipment/WornGearLayer`, `client/equipment/WornHeadLayer` and `client/ItemTints`. The 1.21.1 block item models (`models/item/*.json`) live in `versions/1.21.1/src/main/resources`; 26.1.2 uses the shared `items/*.json`, which point straight at block models.

Editing 1.21.1 code:

```
./gradlew "Set active project to 1.21.1"
# edit, compile with :1.21.1:compileJava
./gradlew "Reset active project"
```

Always reset before committing; `stonecutter.gradle.kts` must say `stonecutter active "26.1.2"`. After changing replacements, delete `versions/1.21.1/build/generated` before compiling 1.21.1. 1.21.1 errors point into that generated tree, which mirrors `src/main/java`.

Per-version access transformers: `versions/<v>/src/main/resources/META-INF/accesstransformer.cfg`.

## Known version differences

Accepted, not bugs.

| Difference | Detail |
|---|---|
| Rubber leaves | No falling pale-oak leaf particle on 1.21.1. |
| Iridium drill | Crafted on 1.21.1 it gets Fortune III; the creative/JEI copy shows none. |
| Cold coffee | On 1.21.1 only an unmodified full water cell works; 26.1.2 also accepts renamed ones. |
| Pale-oak slicing recipes | Do not exist on 1.21.1. |
| Foreign item/fluid handlers | On 1.21.1 machine automation simulates during a transaction and executes on commit, so no rollback inside other mods' handlers. Vanilla hoppers keep their cooldown. |
| Vanilla-derived recipe, fuel and matter counts | Differ where vanilla content differs (26.1.2 copper tools, armor, pale oak, etc.): furnace, slicer, generator fuel and matter scan/replication counts in JEI. |
| Vanilla look | GUI slot highlight, tooltip box, enchant glint, sun curve and keybind category order follow each version's vanilla. JEI 19 on 1.21.1 draws the empty fuel bar under the jetpack in its ingredient list; JEI 29 doesn't. |
| Mode button scroll | On 1.21.1 the mouse wheel over a mode cycle button cycles it, because 26.1.2's container screen doesn't forward scroll to widgets. Server state stays consistent. |
