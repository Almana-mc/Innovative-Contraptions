package me.almana.innovativecontraptions.content.equipment;

//? if >=26.1
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public final class RadiationEffect extends MobEffect {
    public RadiationEffect() { super(MobEffectCategory.HARMFUL, 0x82AC45); }
    @Override
    public boolean shouldApplyEffectTickThisTick(int ticks, int amplifier) { return ticks % 20 == 0; }
    @Override
    //? if >=26.1 {
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        entity.hurtServer(level, entity.damageSources().source(EquipmentContent.RADIATION_DAMAGE), amplifier + 1);
    //?} else {
    /*public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().source(EquipmentContent.RADIATION_DAMAGE), amplifier + 1);
    *///?}
        return true;
    }
}
