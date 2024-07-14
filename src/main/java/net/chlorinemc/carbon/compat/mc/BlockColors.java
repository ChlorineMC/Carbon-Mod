package net.chlorinemc.carbon.compat.mc;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorsExtended;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import org.jetbrains.annotations.Nullable;

public class BlockColors implements BlockColorsExtended {
    private final ObjectIntIdentityMap<IBlockColor> mapBlockColors = new ObjectIntIdentityMap<>();

    private final Reference2ReferenceMap<Block, IBlockColor> blocksToColor = new Reference2ReferenceOpenHashMap<>();
    private static final IBlockColor DEFAULT_PROVIDER = (state, view, pos, tint) -> -1;
    public static BlockColors colors;

    static {
        colors = init();
    }

    public static BlockColors init()
    {
        final BlockColors blockcolors = new BlockColors();
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = (BlockDoublePlant.EnumPlantType)state.getValue(BlockDoublePlant.VARIANT);
                return worldIn != null && pos != null && (blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS || blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.FERN) ? BiomeColorHelper.getGrassColorAtPos(worldIn, state.getValue(BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? pos.down() : pos) : -1;
            }
        }, Blocks.double_plant);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                if (worldIn != null && pos != null)
                {
                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntityFlowerPot)
                    {
                        Item item = ((TileEntityFlowerPot)tileentity).getFlowerPotItem();
                        IBlockState iblockstate = Block.getBlockFromItem(item).getDefaultState();
                        return blockcolors.colorMultiplier(iblockstate, worldIn, pos, tintIndex);
                    }
                    else
                    {
                        return -1;
                    }
                }
                else
                {
                    return -1;
                }
            }
        }, Blocks.flower_pot);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
            }
        }, Blocks.grass);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType)state.getValue(BlockOldLeaf.VARIANT);

                if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE)
                {
                    return ColorizerFoliage.getFoliageColorPine();
                }
                else if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH)
                {
                    return ColorizerFoliage.getFoliageColorBirch();
                }
                else
                {
                    return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
                }
            }
        }, Blocks.leaves);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
            }
        }, Blocks.leaves2);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return worldIn != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : -1;
            }
        }, Blocks.water, Blocks.flowing_water);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return redStoneColorMultiplier(((Integer)state.getValue(BlockRedstoneWire.POWER)).intValue());
            }
        }, Blocks.redstone_wire);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : -1;
            }
        }, Blocks.reeds);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                int i = ((Integer)state.getValue(BlockStem.AGE)).intValue();
                int j = i * 32;
                int k = 255 - i * 8;
                int l = i * 4;
                return j << 16 | k << 8 | l;
            }
        }, Blocks.melon_stem, Blocks.pumpkin_stem);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                if (worldIn != null && pos != null)
                {
                    return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
                }
                else
                {
                    return state.getValue(BlockTallGrass.TYPE) == BlockTallGrass.EnumType.DEAD_BUSH ? 16777215 : ColorizerGrass.getGrassColor(0.5D, 1.0D);
                }
            }
        }, Blocks.tallgrass);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
            }
        }, Blocks.vine);
        blockcolors.registerBlockColorHandler(new IBlockColor()
        {
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return worldIn != null && pos != null ? 2129968 : 7455580;
            }
        }, Blocks.waterlily);
        return blockcolors;
    }

    public static int redStoneColorMultiplier(int p_176337_0_)
    {
        float f = (float)p_176337_0_ / 15.0F;
        float f1 = f * 0.6F + 0.4F;

        if (p_176337_0_ == 0)
        {
            f1 = 0.3F;
        }

        float f2 = f * f * 0.7F - 0.5F;
        float f3 = f * f * 0.6F - 0.7F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f3 < 0.0F)
        {
            f3 = 0.0F;
        }

        int i = MathHelper.clamp_int((int)(f1 * 255.0F), 0, 255);
        int j = MathHelper.clamp_int((int)(f2 * 255.0F), 0, 255);
        int k = MathHelper.clamp_int((int)(f3 * 255.0F), 0, 255);
        return -16777216 | i << 16 | j << 8 | k;
    }


    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess blockAccess, @Nullable BlockPos pos, int renderPass)
    {
        IBlockColor iblockcolor = this.mapBlockColors.getByValue(Block.getIdFromBlock(state.getBlock()));
        return iblockcolor == null ? -1 : iblockcolor.colorMultiplier(state, blockAccess, pos, renderPass);
    }

    public void registerBlockColorHandler(IBlockColor blockColor, Block... blocksIn)
    {
        synchronized (this.blocksToColor) {
            for (Block block : blocksIn) {
                if (blockColor != null) {
                    this.blocksToColor.put(block, blockColor);
                }
            }
        }

        for (Block block : blocksIn) {
            this.mapBlockColors.put(blockColor, Block.getIdFromBlock(block));
        }
    }

    @Override
    public IBlockColor getColorProvider(IBlockState state) {
        return this.blocksToColor.get(state.getBlock());
    }
}