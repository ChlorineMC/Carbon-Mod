package net.chlorinemc.carbon.compat.embeddium;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.jellysquid.mods.sodium.mixin.features.chunk_rendering.AccessorBlockFluidRenderer;
import me.jellysquid.mods.sodium.mixin.features.chunk_rendering.AccessorBlockRenderDispatcher;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class EmbeddiumFluidSpriteCache {
    // Cache the sprites array to avoid reallocating it on every call
    private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[3];
    private final Object2ObjectOpenHashMap<ResourceLocation, TextureAtlasSprite> spriteCache = new Object2ObjectOpenHashMap<>();
    private final TextureAtlasSprite[] waterOverride, lavaOverride;

    public EmbeddiumFluidSpriteCache() {
        AccessorBlockFluidRenderer fluidRenderer = (AccessorBlockFluidRenderer)((AccessorBlockRenderDispatcher)Minecraft.getMinecraft().getBlockRendererDispatcher()).getFluidRenderer();
        waterOverride = new TextureAtlasSprite[3];
        TextureAtlasSprite[] water = fluidRenderer.getAtlasSpritesWater();
        waterOverride[0] = water[0];
        waterOverride[1] = water[1];
        // Construct array with 3 elements, vanilla only has 2
        lavaOverride = new TextureAtlasSprite[3];
        TextureAtlasSprite[] lava = fluidRenderer.getAtlasSpritesLava();
        lavaOverride[0] = lava[0];
        lavaOverride[1] = lava[1];
    }

    private TextureAtlasSprite getTexture(ResourceLocation identifier) {
        TextureAtlasSprite sprite = spriteCache.get(identifier);

        if (sprite == null) {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(identifier.toString());;
            spriteCache.put(identifier, sprite);
        }

        return sprite;
    }

    public TextureAtlasSprite[] getSprites(BlockLiquid fluid) {
        return fluid.getMaterial() == Material.water ? this.waterOverride : this.lavaOverride;
    }
}
