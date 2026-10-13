package me.almana.innovativecontraptions.content.machine;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.TransparentBlock;

public final class ReinforcedGlassBlock extends TransparentBlock {
    public static final MapCodec<ReinforcedGlassBlock> CODEC = simpleCodec(ReinforcedGlassBlock::new);

    public ReinforcedGlassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<ReinforcedGlassBlock> codec() { return CODEC; }
}
