package me.jellysquid.mods.sodium.mixin.features.entity.smooth_lighting;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.model.light.EntityLighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager<T extends Entity> {

    @Unique
    private float tickDelta;

    @Inject(method = "renderEntityStatic", at = @At("HEAD"))
    public void catchTickDelta(Entity p_147936_0_, float entity, boolean partialTicks, CallbackInfoReturnable<Boolean> cir) {
        this.tickDelta = entity;
    }

    @Redirect(method = "renderEntityStatic",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBrightnessForRender(F)I"))
    private int sodium$getBrightnessForRender(Entity instance, float pTicks) {
        if (Minecraft.getMinecraft().gameSettings.ambientOcclusion == SodiumGameOptions.LightingQuality.HIGH.ordinal()) {
            return EntityLighter.getBlendedLight(instance, tickDelta);
        }

        return instance.getBrightnessForRender(pTicks);
    }
}