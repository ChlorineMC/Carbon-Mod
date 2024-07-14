package me.jellysquid.mods.sodium.client.model.quad.blender;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Set;

public class DefaultBlockColorSettings {
    private static final Set<Block> BLENDED_BLOCKS = new ReferenceOpenHashSet<>(Sets.newHashSet(
            Blocks.grass, Blocks.tallgrass,
            Blocks.double_plant, Blocks.leaves, Blocks.leaves,
            Blocks.vine, Blocks.water, Blocks.cauldron, Blocks.reeds));

    public static boolean isSmoothBlendingAvailable(Block block) {
        return BLENDED_BLOCKS.contains(block);
    }
}