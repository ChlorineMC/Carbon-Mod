package me.jellysquid.mods.sodium.client.world;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;

/**
 * Contains extensions to the vanilla {@link IBlockAccess}.
 */
public interface SodiumBlockAccess extends IBlockAccess {
    int getBlockTint(BlockPos pos, BiomeColorHelper.ColorResolver resolver);
}
