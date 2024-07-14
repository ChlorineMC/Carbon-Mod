package me.jellysquid.mods.sodium.mixin.features.chunk_rendering;

import com.llamalad7.mixinextras.sugar.Local;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {
    @Shadow @Final private RenderManager renderManager;

    @Shadow @Final private Minecraft mc;

    @Shadow private int countEntitiesRendered;

    @Shadow private WorldClient theWorld;

    /**
     * @author embeddedt
     * @reason reimplement entity render loop because vanilla's relies on the renderInfos list
     */
    @Inject(method = "renderEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderInfos:Ljava/util/List;", ordinal = 0))
    private void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        double renderViewX = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
        double renderViewY = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
        double renderViewZ = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
        int pass = 0;
        EntityPlayerSP player = this.mc.thePlayer;
        BlockPos.MutableBlockPos entityBlockPos = new BlockPos.MutableBlockPos();

        for (Entity entity : this.theWorld.getLoadedEntityList()) {
            if ((this.renderManager.shouldRender(entity, camera, renderViewX, renderViewY, renderViewZ) || entity.isRiding())
                    && SodiumWorldRenderer.getInstance().isEntityVisible(entity)) {
                boolean isSleeping = renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping();
                if ((entity != renderViewEntity || this.mc.gameSettings.thirdPersonView != 0 || isSleeping)
                        && (entity.posY < 0.0 || entity.posY >= 256.0 || this.theWorld.isBlockLoaded(entityBlockPos.set((int)entity.posX, (int)entity.posY, (int)entity.posZ)))) {
                    this.countEntitiesRendered++;
                    this.renderManager.renderEntityStatic(entity, partialTicks, false);
                }
            }
        }
    }
}
