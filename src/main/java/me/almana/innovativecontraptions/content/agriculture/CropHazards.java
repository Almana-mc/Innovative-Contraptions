package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.content.equipment.HazardArmorItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class CropHazards {
    public static void touch(CropPlotBlockEntity crop, LivingEntity entity) {
        if (crop.species() != CropSpecies.VENOM_BLOOM || crop.age() != 5) return;
        if (entity instanceof Player player && player.isShiftKeyDown() && crop.getLevel().getRandom().nextInt(50) != 0) return;
        entity.addEffect(new MobEffectInstance(MobEffects.POISON, (5 + crop.getLevel().getRandom().nextInt(10)) * 20));
        crop.setAge(4);
    }
    public static void tick(CropPlotBlockEntity crop) {
        if (crop.species() != CropSpecies.PREDATOR_VINE) return;
        var level = (ServerLevel) crop.getLevel(); var pos = crop.getBlockPos();
        if (crop.pendingFood) { Block.popResource(level, pos, new ItemStack(Items.ROTTEN_FLESH)); crop.pendingFood = false; crop.setChanged(); }
        if (crop.age() <= 1) return;
        var targets = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(1, 0, 1).expandTowards(0, 2, 0),
                entity -> entity.isAlive() && !(entity instanceof Player player && (player.hasInfiniteMaterials() || player.isSpectator())));
        if (targets.isEmpty()) return;
        var target = targets.getFirst();
        target.addDeltaMovement(Vec3.atCenterOf(pos).subtract(target.position()).scale(.2));
        //? if >=26.1 {
        target.hurtServer(level, level.damageSources().cactus(), crop.age() * 2);
        //?} else
        //target.hurt(level.damageSources().cactus(), crop.age() * 2);
        if (!(target instanceof Player player) || !protectedByArmor(player)) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 64, 50));
            target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 64));
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 64));
        }
        if (CropEnvironment.canGrow(crop, crop.species())) crop.addGrowth(100);
        crop.pendingFood = true; crop.setChanged();
    }
    private static boolean protectedByArmor(Player player) {
        for (var slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            var stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && !stack.is(Items.LEATHER_HELMET) && !stack.is(Items.LEATHER_CHESTPLATE) && !stack.is(Items.LEATHER_LEGGINGS) && !stack.is(Items.LEATHER_BOOTS) && !stack.is(Items.TURTLE_HELMET)
                    && !(stack.getItem() instanceof HazardArmorItem)) return true;
        }
        return false;
    }
    private CropHazards() {}
}
