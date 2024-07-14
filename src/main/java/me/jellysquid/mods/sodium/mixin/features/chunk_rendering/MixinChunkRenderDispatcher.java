package me.jellysquid.mods.sodium.mixin.features.chunk_rendering;

import com.google.common.collect.Queues;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ArrayBlockingQueue;

@Mixin(ChunkRenderDispatcher.class)
public class MixinChunkRenderDispatcher {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 2))
    public int modifyThreadPoolSize(int constant) {
        return 1;
    }
}
