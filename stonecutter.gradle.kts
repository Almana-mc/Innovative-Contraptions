plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.148" apply false
}

stonecutter active "26.1.2"

stonecutter parameters {
    properties.tags(current.project)
    replacements.regex(current.parsed >= "26.1") {
        replace("\\bResourceLocation\\b", "Identifier", "\\bIdentifier\\b", "ResourceLocation")
        replace("\\.noCollission\\(\\)", ".noCollision()", "\\.noCollision\\(\\)", ".noCollission()")
        // skips server/JEI getRecipeManager
        replace("(?<!getServer\\(\\)|runtime)\\.getRecipeManager\\(\\)", ".recipeAccess()", "\\.recipeAccess\\(\\)", ".getRecipeManager()")
        replace("net\\.minecraft\\.world\\.ItemInteractionResult useItemOn\\(", "InteractionResult useItemOn(", "\\bInteractionResult useItemOn\\(", "net.minecraft.world.ItemInteractionResult useItemOn(")
        replace("\\bClickType\\b", "ContainerInput", "\\bContainerInput\\b", "ClickType")
        replace("\\bUseAnim\\b", "ItemUseAnimation", "\\bItemUseAnimation\\b", "UseAnim")
        replace("\\bFarmBlock\\b", "FarmlandBlock", "\\bFarmlandBlock\\b", "FarmBlock")
        replace("\\.getMinBuildHeight\\(\\)", ".getMinY()", "\\.getMinY\\(\\)", ".getMinBuildHeight()")
        replace("\\.isDay\\(\\)", ".isBrightOutside()", "\\.isBrightOutside\\(\\)", ".isDay()")
        // ResourceKey only, not TagKey
        replace("(?<=\\.orElseThrow\\(\\))\\.location\\(\\)", ".identifier()", "(?<=\\.orElseThrow\\(\\))\\.identifier\\(\\)", ".location()")
        replace("\\.nonEmptyStream\\(\\)", ".nonEmptyItemCopyStream()", "\\.nonEmptyItemCopyStream\\(\\)", ".nonEmptyStream()")
        // 26.1 get returns Optional
        replace("(?<=BuiltInRegistries\\.[A-Z_]{1,64})\\.get\\(", ".getValue(", "(?<=BuiltInRegistries\\.[A-Z_]{1,64})\\.getValue\\(", ".get(")
        replace("MobEffects\\.CONFUSION\\b", "MobEffects.NAUSEA", "MobEffects\\.NAUSEA\\b", "MobEffects.CONFUSION")
        replace("MobEffects\\.DIG_SLOWDOWN\\b", "MobEffects.MINING_FATIGUE", "MobEffects\\.MINING_FATIGUE\\b", "MobEffects.DIG_SLOWDOWN")
        replace("MobEffects\\.MOVEMENT_SLOWDOWN\\b", "MobEffects.SLOWNESS", "MobEffects\\.SLOWNESS\\b", "MobEffects.MOVEMENT_SLOWDOWN")
        replace("MobEffects\\.DAMAGE_RESISTANCE\\b", "MobEffects.RESISTANCE", "MobEffects\\.RESISTANCE\\b", "MobEffects.DAMAGE_RESISTANCE")
        replace("MobEffects\\.HARM\\b", "MobEffects.INSTANT_DAMAGE", "MobEffects\\.INSTANT_DAMAGE\\b", "MobEffects.HARM")
        replace("MobEffects\\.DIG_SPEED\\b", "MobEffects.HASTE", "MobEffects\\.HASTE\\b", "MobEffects.DIG_SPEED")
        replace("MobEffects\\.MOVEMENT_SPEED\\b", "MobEffects.SPEED", "MobEffects\\.SPEED\\b", "MobEffects.MOVEMENT_SPEED")
        replace("MobEffects\\.DAMAGE_BOOST\\b", "MobEffects.STRENGTH", "MobEffects\\.STRENGTH\\b", "MobEffects.DAMAGE_BOOST")
        // JEI 19 has only RecipeType
        replace("//import mezz\\.jei\\.api\\.recipe\\.types\\.", "import mezz.jei.api.recipe.types.", "(?<!//)import mezz\\.jei\\.api\\.recipe\\.types\\.", "//import mezz.jei.api.recipe.types.")
        replace("\\bmezz\\.jei\\.api\\.recipe\\.RecipeType<net\\.minecraft\\.world\\.item\\.crafting\\.RecipeHolder<(\\w+)>>", "IRecipeHolderType<$1>", "(?<![.\\w])IRecipeHolderType<(\\w+)>", "mezz.jei.api.recipe.RecipeType<net.minecraft.world.item.crafting.RecipeHolder<$1>>")
        replace("\\bmezz\\.jei\\.api\\.recipe\\.RecipeType\\.createFromDeferredVanilla\\(", "IRecipeHolderType.createDeferred(", "(?<![.\\w])IRecipeHolderType\\.createDeferred\\(", "mezz.jei.api.recipe.RecipeType.createFromDeferredVanilla(")
        replace("\\bmezz\\.jei\\.api\\.recipe\\.RecipeType\\.createRecipeHolderType\\(", "IRecipeHolderType.create(", "(?<![.\\w])IRecipeHolderType\\.create\\(", "mezz.jei.api.recipe.RecipeType.createRecipeHolderType(")
        replace("\\bmezz\\.jei\\.api\\.recipe\\.RecipeType\\b(?!<net\\.minecraft\\.world\\.item\\.crafting\\.RecipeHolder<|\\.createFromDeferredVanilla\\(|\\.createRecipeHolderType\\()", "IRecipeType", "(?<![.\\w])IRecipeType\\b", "mezz.jei.api.recipe.RecipeType")
        // JEI slot builder add
        replace("(?<=\\.(?:addInputSlot|addOutputSlot|addSlot)\\([^;]{0,200})\\.addItemStack\\(", ".add(", "(?<=\\.(?:addInputSlot|addOutputSlot|addSlot)\\([^;]{0,200})\\.add\\(", ".addItemStack(")
    }
    replacements.string(current.parsed >= "26.1") {
        replace("me.almana.innovativecontraptions.compat.ValueOutput;", "net.minecraft.world.level.storage.ValueOutput;")
        replace("me.almana.innovativecontraptions.compat.ValueInput;", "net.minecraft.world.level.storage.ValueInput;")
        replace("me.almana.innovativecontraptions.compat.ContainerHelper;", "net.minecraft.world.ContainerHelper;")
        replace("me.almana.innovativecontraptions.compat.ItemStackTemplate;", "net.minecraft.world.item.ItemStackTemplate;")
        replace("me.almana.innovativecontraptions.compat.BlockEntityTypes.of(", "new BlockEntityType<>(")
        replace("me.almana.innovativecontraptions.compat.FluidStackTemplate;", "net.neoforged.neoforge.fluids.FluidStackTemplate;")
        replace("me.almana.innovativecontraptions.compat.transfer.", "net.neoforged.neoforge.transfer.")
        replace("me.almana.innovativecontraptions.compat.Capabilities", "net.neoforged.neoforge.capabilities.Capabilities")
        replace("me.almana.innovativecontraptions.compat.SingleItemRecipe;", "net.minecraft.world.item.crafting.SingleItemRecipe;")
        replace("me.almana.innovativecontraptions.compat.CommonInfo;", "net.minecraft.world.item.crafting.Recipe.CommonInfo;")
        replace("new me.almana.innovativecontraptions.compat.RecipeSerializer<>(", "new RecipeSerializer<>(")
        replace("me.almana.innovativecontraptions.compat.BaseEntityBlock;", "net.minecraft.world.level.block.BaseEntityBlock;")
        replace("new WornItem(GearProperties.", "new Item(GearProperties.")
        replace("me.almana.innovativecontraptions.compat.GuiGraphicsExtractor;", "net.minecraft.client.gui.GuiGraphicsExtractor;")
        replace("me.almana.innovativecontraptions.compat.AbstractContainerScreen;", "net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;")
        replace("me.almana.innovativecontraptions.compat.RenderPipelines;", "net.minecraft.client.renderer.RenderPipelines;")
        replace("me.almana.innovativecontraptions.compat.RecipeProvider;", "net.minecraft.data.recipes.RecipeProvider;")
        replace("me.almana.innovativecontraptions.compat.SimpleCookingRecipeBuilder;", "net.minecraft.data.recipes.SimpleCookingRecipeBuilder;")
        replace(".addRecipeCatalysts(", ".addCraftingStation(")
        replace("RecipeTypes.FUELING", "RecipeTypes.SMELTING_FUEL")
        replace("RecipeIngredientRole.CATALYST", "RecipeIngredientRole.CRAFTING_STATION")
    }
}
