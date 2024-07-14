package me.jellysquid.mods.sodium.mixin.features.particle_cull;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderer.class)
public class MixinEffectRenderer {
    private Frustum cullingFrustum;

    @Inject(method = {"renderParticles", "renderLitParticles"}, at = @At("HEAD"))
    private void preRenderParticles(Entity entity, float partialTicks, CallbackInfo ci) {
        Frustum frustum = SodiumWorldRenderer.getInstance().getFrustum();
        boolean useCulling = SodiumClientMod.options().advanced.useParticleCulling;

        // Set up the frustum state before rendering particles
        if (useCulling && frustum != null) {
            this.cullingFrustum = frustum;
        } else {
            this.cullingFrustum = null;
        }
    }

    @WrapWithCondition(method = {"renderParticles", "renderLitParticles"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EntityFX;renderParticle(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private boolean filterParticleList(EntityFX particle, WorldRenderer f8, Entity f9, float f10, float f11, float f12, float vec3d, float v, float buffer) {
        if(this.cullingFrustum == null) {
            return true;
        }

        AxisAlignedBB box = particle.getCollisionBoundingBox();

        // Hack: Grow the particle's bounding box in order to work around mis-behaved particles
        return this.cullingFrustum.isBoxInFrustum(box.minX - 1.0D, box.minY - 1.0D, box.minZ - 1.0D, box.maxX + 1.0D, box.maxY + 1.0D, box.maxZ + 1.0D);
    }
}