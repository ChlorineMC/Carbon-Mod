package me.jellysquid.mods.sodium.mixin.features.chunk_rendering;

import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockRendererDispatcher.class)
public interface AccessorBlockRenderDispatcher {
    @Accessor
    BlockFluidRenderer getFluidRenderer();
}
