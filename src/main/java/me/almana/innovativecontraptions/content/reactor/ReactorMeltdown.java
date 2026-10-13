package me.almana.innovativecontraptions.content.reactor;

import com.mojang.logging.LogUtils;
import me.almana.innovativecontraptions.content.energy.net.EnergySettings;
import me.almana.innovativecontraptions.content.equipment.EquipmentContent;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if >=26.1
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public final class ReactorMeltdown {
    public static final int THRESHOLD = 4_000;
    private static final Logger LOGGER = LogUtils.getLogger();

    static boolean tick(NuclearReactorBlockEntity reactor, ReactorGrid grid) {
        double limit = EnergySettings.REACTOR_EXPLOSION_LIMIT.get();
        if (grid.heat() < THRESHOLD || limit <= 0) return false;
        Level level = reactor.getLevel();
        BlockPos pos = reactor.getBlockPos();
        RandomSource random = level.getRandom();
        float power = (float) grid.heat() / grid.maxHeat(), hem = grid.hem();
        if (power >= 1F) {
            explode(reactor, grid.explosionPower(), limit);
            return true;
        }
        if (power >= 0.85F && random.nextFloat() <= 0.2F * hem) ignite(level, near(pos, random));
        if (power >= 0.7F) irradiate(level, pos, random, hem);
        if (power >= 0.5F && random.nextFloat() <= hem) {
            BlockPos at = near(pos, random);
            if (level.getBlockState(at).is(Blocks.WATER)) level.setBlockAndUpdate(at, Blocks.AIR.defaultBlockState());
        }
        if (power >= 0.4F && random.nextFloat() <= hem) {
            BlockPos at = near(pos, random);
            if (level.getBlockEntity(at) == null && level.getBlockState(at).ignitedByLava()) level.setBlockAndUpdate(at, Blocks.FIRE.defaultBlockState());
        }
        return false;
    }

    private static void ignite(Level level, BlockPos at) {
        BlockState state = level.getBlockState(at);
        if (state.is(ICBlocks.REACTOR_CHAMBER.get()) || ReactorStructure.casing(state)) return;
        if (state.isAir()) {
            level.setBlockAndUpdate(at, Blocks.FIRE.defaultBlockState());
            return;
        }
        if (state.getDestroySpeed(level, at) < 0F || level.getBlockEntity(at) != null) return;
        boolean hard = state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
        level.setBlockAndUpdate(at, hard ? Blocks.LAVA.defaultBlockState() : Blocks.FIRE.defaultBlockState());
    }

    private static void irradiate(Level level, BlockPos pos, RandomSource random, float hem) {
        AABB box = new AABB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            int damage = (int) (random.nextInt(4) * hem);
            if (damage <= 0) continue;
            //? if >=26.1 {
            entity.hurtServer((ServerLevel) level, entity.damageSources().source(EquipmentContent.RADIATION_DAMAGE), damage);
            //?} else
            //entity.hurt(entity.damageSources().source(EquipmentContent.RADIATION_DAMAGE), damage);
        }
    }

    private static BlockPos near(BlockPos pos, RandomSource random) {
        BlockPos at;
        do at = pos.offset(random.nextInt(5) - 2, random.nextInt(5) - 2, random.nextInt(5) - 2);
        while (at.equals(pos));
        return at;
    }

    private static void explode(NuclearReactorBlockEntity reactor, float raw, double limit) {
        Level level = reactor.getLevel();
        BlockPos pos = reactor.getBlockPos();
        LOGGER.info("Nuclear Reactor at {} melted (raw explosion power {})", pos.toShortString(), raw);
        reactor.clearContent();
        for (Direction side : Direction.values())
            if (level.getBlockState(pos.relative(side)).is(ICBlocks.REACTOR_CHAMBER.get())) level.removeBlock(pos.relative(side), false);
        level.removeBlock(pos, false);
        level.explode(null, null, null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                (float) Math.min(raw, limit), false, Level.ExplosionInteraction.BLOCK);
    }

    private ReactorMeltdown() {}
}
