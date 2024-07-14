package me.jellysquid.mods.sodium.client.model.quad.blender;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.chlorinemc.carbon.compat.mc.IBlockColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

class ConfigurableColorBlender implements BiomeColorBlender {
    private final BiomeColorBlender defaultBlender;
    private final BiomeColorBlender smoothBlender;

    public ConfigurableColorBlender(Minecraft client) {
        this.defaultBlender = new FlatBiomeColorBlender();
        this.smoothBlender = isSmoothBlendingEnabled() ? new SmoothBiomeColorBlender() : this.defaultBlender;
    }

    private static boolean isSmoothBlendingEnabled() {
        return SodiumClientMod.options().quality.biomeBlendRadius > 0;
    }

    @Override
    public int[] getColors(IBlockColor colorizer, IBlockAccess world, IBlockState state, BlockPos origin,
                           ModelQuadView quad) {
    	BiomeColorBlender blender;

        if (BlockColorSettings.isSmoothBlendingEnabled(world, state, origin)) {
            blender = this.smoothBlender;
        } else {
            blender = this.defaultBlender;
        }

        return blender.getColors(colorizer, world, state, origin, quad);
    }

}