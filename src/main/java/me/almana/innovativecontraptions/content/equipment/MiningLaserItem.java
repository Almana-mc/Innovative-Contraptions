package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MiningLaserItem extends BatteryItem {
    private static final int[] COSTS = {1250, 100, 5000, 3000, 2500, 10000, 5000, 7500};
    public MiningLaserItem(Properties props) { super(props, 300_000, EuTier.HV, true, 512, false); }
    @Override
    //? if >=26.1 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        ItemStack stack = player.getItemInHand(hand);
        if (EquipmentControls.mode(player)) {
            if (!level.isClientSide()) {
                int mode = (stack.getOrDefault(EquipmentData.MODE.get(), 0) + 1) % 8;
                stack.set(EquipmentData.MODE.get(), mode);
                EquipmentControls.message(player, "laser_mode_" + mode);
            }
            return InteractionResult.SUCCESS;
        }
        int mode = stack.getOrDefault(EquipmentData.MODE.get(), 0);
        if (mode == 3 || mode == 7) return InteractionResult.PASS;
        return shoot(level, player, stack, player.getEyePosition(), player.getLookAngle(), mode, COSTS[mode]);
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        int mode = stack.getOrDefault(EquipmentData.MODE.get(), 0);
        //? if >=26.1 {
        if (EquipmentControls.mode(player)) return use(context.getLevel(), player, context.getHand());
        //?} else
        //if (EquipmentControls.mode(player)) return useResult(context.getLevel(), player, context.getHand());
        if (mode != 3 && mode != 7) return InteractionResult.PASS;
        Vec3 look = player.getLookAngle();
        Vec3 direction = new Vec3(look.x, 0, look.z).normalize();
        if (direction.lengthSqr() < 0.5) return InteractionResult.FAIL;
        Vec3 origin = new Vec3(player.getX(), context.getClickedPos().getY() + 0.5, player.getZ());
        return shoot(context.getLevel(), player, stack, origin, direction, mode, COSTS[mode]);
    }
    private InteractionResult shoot(Level level, Player player, ItemStack stack, Vec3 origin, Vec3 direction, int mode, int cost) {
        if (!(level instanceof ServerLevel server)) return InteractionResult.SUCCESS;
        if (!PoweredUse.spend(stack, player, cost)) return InteractionResult.FAIL;
        int radius = mode == 5 ? 2 : mode == 7 ? 1 : 0;
        Vec3 right = direction.cross(new Vec3(0, 1, 0)).normalize();
        if (right.lengthSqr() < 0.5) right = new Vec3(1, 0, 0);
        Vec3 up = right.cross(direction).normalize();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                Vec3 offset = right.scale(x).add(up.scale(y));
                Vec3 from = mode == 7 ? origin.add(offset) : origin;
                Vec3 heading = mode == 5 ? direction.add(offset.scale(0.12)).normalize() : direction;
                LaserBolt projectile = new LaserBolt(EquipmentContent.LASER_BOLT.get(), server);
                projectile.configure(player, from, heading, mode);
                server.addFreshEntity(projectile);
            }
        }
        server.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.6F, 2);
        return InteractionResult.SUCCESS;
    }
}
