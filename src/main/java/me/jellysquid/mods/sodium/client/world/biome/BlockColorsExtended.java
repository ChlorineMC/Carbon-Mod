package me.jellysquid.mods.sodium.client.world.biome;

import net.minecraft.block.state.IBlockState;
import net.chlorinemc.carbon.compat.mc.IBlockColor;

public interface BlockColorsExtended {
    IBlockColor getColorProvider(IBlockState state);
}
