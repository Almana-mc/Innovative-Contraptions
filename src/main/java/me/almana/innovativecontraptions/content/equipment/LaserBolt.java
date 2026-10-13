package me.almana.innovativecontraptions.content.equipment;

import java.util.HashSet;
import java.util.Set;
import me.almana.innovativecontraptions.content.tool.MachineLoss;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
//? if <26.1
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockEvent;
//? if >=26.1
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public final class LaserBolt extends Projectile {
    private float power = 5;
    private int mode;
    private int remaining = 80;
    private LaserBlast blast;
    private final Set<BlockPos> crossed = new HashSet<>();
    public LaserBolt(EntityType<? extends Projectile> type, Level level) { super(type, level); }
    public LaserBlast blast() { return blast; }
    public void configure(Player owner, Vec3 origin, Vec3 direction, int mode) {
        setOwner(owner);
        setPos(origin);
        setDeltaMovement(direction);
        this.mode = mode;
        power = switch (mode) { case 2 -> 20; case 4 -> 8; case 5, 6 -> 12; default -> 5; };
        remaining = mode == 1 ? 4 : 80;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (--remaining < 0 || power <= 0 || !server.hasChunkAt(blockPosition())) { discard(); return; }
        Vec3 from = position();
        Vec3 step = getDeltaMovement().scale(0.1);
        for (int i = 0; i < 10 && !isRemoved(); i++) {
            Vec3 to = from.add(step);
            var hit = ProjectileUtil.getEntityHitResult(server, this, from, to,
                    getBoundingBox().expandTowards(getDeltaMovement()).inflate(0.2), this::canHitEntity);
            if (hit != null) { strikeEntity(server, hit.getEntity()); return; }
            BlockPos pos = BlockPos.containing(to);
            if (!crossed.contains(pos) && !server.getBlockState(pos).isAir()) {
                crossed.add(pos);
                strikeBlock(server, pos);
            }
            setPos(to);
            //? if >=26.1 {
            server.sendParticles(new DustParticleOptions(mode == 4 ? 0xFF9A37 : 0xFF3B40, 0.8F),
                    to.x, to.y, to.z, 1, 0, 0, 0, 0);
            //?} else {
            /*server.sendParticles(new DustParticleOptions(Vec3.fromRGB24(mode == 4 ? 0xFF9A37 : 0xFF3B40).toVector3f(), 0.8F),
                    to.x, to.y, to.z, 1, 0, 0, 0, 0);
            *///?}
            from = to;
        }
        power -= 0.5F;
    }
    private void strikeEntity(ServerLevel server, Entity target) {
        if (mode == 6) { explode(server); return; }
        int damage = (int) Math.floor(power);
        //? if >=26.1 {
        target.hurtServer(server, server.damageSources().thrown(this, getOwner()), damage);
        //?} else
        //target.hurt(server.damageSources().thrown(this, getOwner()), damage);
        target.igniteForSeconds(damage * (mode == 4 ? 2 : 1));
        discard();
    }
    private void strikeBlock(ServerLevel server, BlockPos pos) {
        BlockState state = server.getBlockState(pos);
        if (state.is(Tags.Blocks.GLASS_BLOCKS)
                || state.is(Tags.Blocks.GLASS_PANES)) return;
        float hardness = state.getDestroySpeed(server, pos);
        if (hardness < 0 || !(getOwner() instanceof Player player) || !server.mayInteract(player, pos)
                //? if >=26.1 {
                || !MachineLoss.permits(new BreakBlockEvent(server, pos, state, player))) {
                //?} else
                //|| !MachineLoss.permits(new BlockEvent.BreakEvent(server, pos, state, player))) {
            discard(); return;
        }
        if (mode == 6) { explode(server); return; }
        power -= hardness / 1.5F;
        if (power < 0) { discard(); return; }
        if (state.is(Blocks.TNT)) {
            state.onCaughtFire(server, pos, Direction.UP, player);
            server.removeBlock(pos, false);
        } else breakBlock(server, pos, state, player);
        if (mode == 1) discard();
    }
    private void breakBlock(ServerLevel server, BlockPos pos, BlockState state, Player player) {
        boolean wood = state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS);
        if (random.nextFloat() < 0.9F && !(mode == 4 && wood)) {
            for (ItemStack drop : Block.getDrops(state, server, pos, server.getBlockEntity(pos), player, new ItemStack(Items.DIAMOND_PICKAXE))) {
                if (mode == 4) {
                    var input = new SingleRecipeInput(drop);
                    var smelting = server.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, server);
                    //? if >=26.1 {
                    if (smelting.isPresent()) drop = smelting.get().value().assemble(input).copyWithCount(drop.getCount());
                    //?} else
                    //if (smelting.isPresent()) drop = smelting.get().value().assemble(input, server.registryAccess()).copyWithCount(drop.getCount());
                }
                Block.popResource(server, pos, drop);
            }
        }
        server.removeBlock(pos, false);
        if (wood && random.nextFloat() < 0.1F && Blocks.FIRE.defaultBlockState().canSurvive(server, pos)) {
            server.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
        }
    }
    private void explode(ServerLevel server) {
        blast = new LaserBlast(this, server);
        blast.detonate();
        discard();
    }
    @Override
    //? if >=26.1 {
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
    //?} else {
    /*protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ValueOutput output = new ValueOutput(tag, registryAccess());
    *///?}
        output.putFloat("power", power); output.putInt("mode", mode); output.putInt("remaining", remaining);
    }
    @Override
    //? if >=26.1 {
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
    //?} else {
    /*protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ValueInput input = new ValueInput(tag, registryAccess());
    *///?}
        power = input.getFloatOr("power", 5); mode = input.getIntOr("mode", 0); remaining = input.getIntOr("remaining", 0);
    }
}
