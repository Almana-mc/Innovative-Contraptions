package me.almana.innovativecontraptions.content.equipment;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.content.tool.MachineLoss;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
//? if >=26.1
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.ExplosionEvent;
//? if >=26.1 {
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
//?} else
//import net.neoforged.neoforge.event.level.BlockEvent;

//? if >=26.1 {
public final class LaserBlast extends ExplosionDamageCalculator implements Explosion {
//?} else
//public final class LaserBlast extends ExplosionDamageCalculator {
    private final LaserBolt source;
    private final ServerLevel level;
    private final Vec3 center;
    private final List<BlockPos> blocks = new ArrayList<>();
    public LaserBlast(LaserBolt source, ServerLevel level) {
        this.source = source;
        this.level = level;
        center = source.position();
    }
    public void detonate() {
        level.explode(source, null, this, center.x, center.y, center.z, 5, false, Level.ExplosionInteraction.BLOCK);
        //? if <26.1
        //Explosion blockBlast = new Explosion(level, source, center.x, center.y, center.z, 5, false, Explosion.BlockInteraction.DESTROY);
        for (BlockPos pos : blocks) {
            //? if >=26.1 {
            level.getBlockState(pos).onExplosionHit(level, pos, this, (stack, at) -> {
            //?} else
            //level.getBlockState(pos).onExplosionHit(level, pos, blockBlast, (stack, at) -> {
                if (level.getRandom().nextFloat() < 0.85F) Block.popResource(level, at, stack);
            });
        }
    }
    public static void collect(ExplosionEvent.Detonate event) {
        if (!(event.getExplosion().getDirectSourceEntity() instanceof LaserBolt bolt) || bolt.blast() == null) return;
        LaserBlast blast = bolt.blast();
        if (bolt.getOwner() instanceof Player player) {
            for (BlockPos pos : event.getAffectedBlocks()) {
                var state = event.getLevel().getBlockState(pos);
                if (event.getLevel().mayInteract(player, pos)
                        //? if >=26.1 {
                        && MachineLoss.permits(new BreakBlockEvent(event.getLevel(), pos, state, player))) {
                        //?} else
                        //&& MachineLoss.permits(new BlockEvent.BreakEvent(event.getLevel(), pos, state, player))) {
                    blast.blocks.add(pos.immutable());
                }
            }
        }
        event.getAffectedBlocks().clear();
    }
    @Override
    //? if >=26.1 {
    public float getEntityDamageAmount(Explosion explosion, Entity entity, float exposure) {
        return super.getEntityDamageAmount(explosion, entity, exposure) * 0.55F;
    }
    //?} else {
    /*public float getEntityDamageAmount(Explosion explosion, Entity entity) {
        return super.getEntityDamageAmount(explosion, entity) * 0.55F;
    }
    *///?}
    //? if >=26.1 {
    @Override public ServerLevel level() { return level; }
    @Override public BlockInteraction getBlockInteraction() { return BlockInteraction.DESTROY; }
    @Override public LivingEntity getIndirectSourceEntity() { return source.getOwner() instanceof LivingEntity living ? living : null; }
    @Override public Entity getDirectSourceEntity() { return source; }
    @Override public float radius() { return 5; }
    @Override public Vec3 center() { return center; }
    @Override public boolean canTriggerBlocks() { return true; }
    @Override public boolean shouldAffectBlocklikeEntities() { return true; }
    //?}
}
