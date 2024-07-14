package me.jellysquid.mods.sodium.mixin.features.entity.smooth_lighting;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.model.light.EntityLighter;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPainting.class)
public abstract class MixinPaintingEntityRenderer extends Render<EntityPainting> {

    @Unique
    private EntityPainting entity;

    @Unique
    private float tickDelta;

    protected MixinPaintingEntityRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityPainting;DDDFF)V", at = @At(value = "HEAD"))
    public void preRender(EntityPainting paintingEntity, double x, double y, double z, float p_76986_8_, float partialTicks, CallbackInfo ci) {
        this.entity = paintingEntity;
        this.tickDelta = partialTicks;
    }

    @Redirect(method = "setLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCombinedLight(Lnet/minecraft/util/BlockPos;I)I"))
    public int redirectLightmapCoord(World instance, BlockPos p_175626_0_, int pos) {
        if (SodiumClientMod.options().quality.smoothLighting == SodiumGameOptions.LightingQuality.HIGH && this.entity != null) {
            return EntityLighter.getBlendedLight(this.entity, tickDelta);
        } else {
            return instance.getCombinedLight(p_175626_0_, pos);
        }
    }

}