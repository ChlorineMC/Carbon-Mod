package me.jellysquid.mods.sodium.mixin.features.chunk_rendering;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.world.SodiumBlockAccess;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BiomeColorHelper.class, priority = 1200)
public class MixinBiomeColorHelper {
    /**
     * @author embeddedt
     * @reason reduce allocation rate, use Sodium's biome cache, use configurable biome blending
     */
    @Overwrite
    private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver)
    {
        if (blockAccess instanceof SodiumBlockAccess) {
            // Use Sodium's more efficient biome cache
            return ((SodiumBlockAccess)blockAccess).getBlockTint(pos, colorResolver);
        }
        int radius = SodiumClientMod.options().quality.biomeBlendRadius;
        if (radius == 0) {
            return colorResolver.getColorAtPos(blockAccess.getBiomeGenForCoords(pos), pos);
        } else {
            int blockCount = (radius * 2 + 1) * (radius * 2 + 1);

            int i = 0;
            int j = 0;
            int k = 0;

            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for(int z = -radius; z <= radius; z++) {
                for(int x = -radius; x <= radius; x++) {
                    mutablePos.set(pos.getX() + x, pos.getY(), pos.getZ() + z);
                    int l = colorResolver.getColorAtPos(blockAccess.getBiomeGenForCoords(mutablePos), mutablePos);
                    i += (l & 16711680) >> 16;
                    j += (l & 65280) >> 8;
                    k += l & 255;
                }
            }

            return (i / blockCount & 255) << 16 | (j / blockCount & 255) << 8 | k / blockCount & 255;
        }

    }
}
