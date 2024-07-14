package me.jellysquid.mods.sodium.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import repack.joml.Vector3d;

/**
 * Contains methods stripped from BlockState or FluidState that didn't actually need to be there. Technically these
 * could be a mixin to Block or Fluid, but that's annoying while not actually providing any benefit.
 */
public class WorldUtil {

    public static boolean method_15756(IBlockAccess world, BlockPos pos, BlockLiquid fluid) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                IBlockState block = world.getBlockState(pos);
                if (!block.getBlock().isOpaqueCube() && getFluid(block) != fluid) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Vector3d getVelocity(IBlockAccess world, BlockPos pos, IBlockState thizz) {
        Vector3d velocity = new Vector3d();
        int decay = getEffectiveFlowDecay(world, pos, thizz);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

        for (EnumFacing dire : DirectionUtil.HORIZONTAL_DIRECTIONS) {
            int adjX = pos.getX() + dire.getFrontOffsetX();
            int adjZ = pos.getZ() + dire.getFrontOffsetZ();
            mutable.set(adjX, pos.getY(), adjZ);

            int adjDecay = getEffectiveFlowDecay(world, mutable, thizz);

            if (adjDecay < 0) {
                if (!world.getBlockState(mutable).getBlock().getMaterial().blocksMovement()) {
                    adjDecay = getEffectiveFlowDecay(world, mutable.down(), thizz);

                    if (adjDecay >= 0) {
                        adjDecay -= (decay - 8);
                        velocity = velocity.add((adjX - pos.getX()) * adjDecay, 0, (adjZ - pos.getZ()) * adjDecay);
                    }
                }
            } else {
                adjDecay -= decay;
                velocity = velocity.add((adjX - pos.getX()) * adjDecay, 0, (adjZ - pos.getZ()) * adjDecay);
            }
        }

        IBlockState state = world.getBlockState(pos);
        if (state.getValue(BlockLiquid.LEVEL) >= 8) {
            if (thizz.getBlock().isBlockSolid(world, pos.north(), EnumFacing.NORTH)
                    || thizz.getBlock().isBlockSolid(world, pos.south(), EnumFacing.SOUTH)
                    || thizz.getBlock().isBlockSolid(world, pos.west(), EnumFacing.WEST)
                    || thizz.getBlock().isBlockSolid(world, pos.east(), EnumFacing.EAST)
                    || thizz.getBlock().isBlockSolid(world, pos.up().south(), EnumFacing.NORTH)
                    || thizz.getBlock().isBlockSolid(world, pos.up().west(), EnumFacing.SOUTH)
                    || thizz.getBlock().isBlockSolid(world, pos.up().west(), EnumFacing.WEST)
                    || thizz.getBlock().isBlockSolid(world, pos.up().east(), EnumFacing.EAST)) {
                velocity = velocity.normalize().add(0.0D, -6.0D, 0.0D);
            }
        }

        if (velocity.x == 0 && velocity.y == 0 && velocity.z == 0)
            return velocity.zero();
        return velocity.normalize();
    }

    /**
     * Returns true if any block in a 3x3x3 cube is not the same fluid and not an opaque full cube.
     * Equivalent to FluidState::method_15756 in modern.
     */
    public static boolean method_15749(IBlockAccess world, BlockLiquid thiz, BlockPos pos, EnumFacing dir) {
        IBlockState b = world.getBlockState(pos);
        BlockLiquid f = getFluid(b);
        if (f == thiz) {
            return false;
        } else {
            return dir == EnumFacing.UP ? true : b.getBlock().getMaterial() != Material.ice && b.getBlock().isBlockSolid(world, pos, dir);
        }
    }

    /**
     * Returns fluid height as a percentage of the block; 0 is none and 1 is full.
     */
    public static float getFluidHeight(BlockLiquid fluid, int meta) {
        return fluid == null ? 0 : 1 - BlockLiquid.getLiquidHeightPercent(meta);
    }

    /**
     * Returns the flow decay but converts values indicating falling liquid (values >=8) to their effective source block
     * value of zero
     */
    public static int getEffectiveFlowDecay(IBlockAccess world, BlockPos pos, IBlockState thiz) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().getMaterial() != thiz.getBlock().getMaterial()) {
            return -1;
        } else {
            int decay = state.getValue(BlockLiquid.LEVEL);
            return decay >= 8 ? 0 : decay;
        }
    }

    public static BlockLiquid getFluid(IBlockState b) {
        return toFluidBlock(b.getBlock());
    }

    /**
     * Equivalent to method_15748 in 1.16.5
     */
    public static boolean isEmptyOrSame(BlockLiquid fluid, BlockLiquid otherFluid) {
        return otherFluid == null || fluid == otherFluid;
    }

    public static BlockLiquid toFluidBlock(Block block) {
        return block instanceof BlockLiquid ? (BlockLiquid)block : null;
    }

    public static BlockLiquid getFluidOfBlock(Block block) {
        return toFluidBlock(block);
    }
}