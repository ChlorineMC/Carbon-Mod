package net.chlorinemc.carbon.compat.mc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IBlockColor {
    int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex);
}