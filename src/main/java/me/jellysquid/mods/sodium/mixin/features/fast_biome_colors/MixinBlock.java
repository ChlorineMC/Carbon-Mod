package me.jellysquid.mods.sodium.mixin.features.fast_biome_colors;

import me.jellysquid.mods.sodium.client.model.quad.blender.BlockColorSettings;
import me.jellysquid.mods.sodium.client.model.quad.blender.DefaultBlockColorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class MixinBlock implements BlockColorSettings<IBlockState> {
    @Override
    public boolean useSmoothColorBlending(IBlockAccess view, IBlockState state, BlockPos pos) {
        return DefaultBlockColorSettings.isSmoothBlendingAvailable(state.getBlock());
    }
}