package me.jellysquid.mods.sodium.mixin.features.sky;

import me.jellysquid.mods.sodium.common.util.CameraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Shadow
    @Final
    private Minecraft mc;

    /**
     * <p>Prevents the sky layer from rendering when the fog distance is reduced
     * from the default. This helps prevent situations where the sky can be seen
     * through chunks culled by fog occlusion. This also fixes the vanilla issue
     * <a href="https://bugs.mojang.com/browse/MC-152504">MC-152504</a> since it
     * is also caused by being able to see the sky through invisible chunks.</p>
     * 
     * <p>However, this fix comes with some caveats. When underwater, it becomes 
     * impossible to see the sun, stars, and moon since the sky is not rendered.
     * While this does not exactly match the vanilla game, it is consistent with
     * what Bedrock Edition does, so it can be considered vanilla-style. This is
     * also more "correct" in the sense that underwater fog is applied to chunks
     * outside of water, so the fog should also be covering the sun and sky.</p>
     * 
     * <p>When updating Sodium to new releases of the game, please check for new
     * ways the fog can be reduced in {@link BackgroundRenderer#applyFog()}.</p>
     */
    @Inject(method = "renderSky(FI)V", at = @At("HEAD"), cancellable = true)
    private void preRenderSky(float tickDelta, int type, CallbackInfo callbackInfo) {
        Vec3 cameraPosition = CameraUtil.getCameraPosition(tickDelta);
        Entity cameraEntity = mc.getRenderViewEntity();

        boolean hasBlindness = cameraEntity instanceof EntityLivingBase && ((EntityLivingBase) cameraEntity).getActivePotionEffect(Potion.blindness) != null;
        boolean useThickFog = this.mc.theWorld.provider.doesXZShowFog(MathHelper.floor_double(cameraPosition.xCoord), MathHelper.floor_double(cameraPosition.yCoord));

        if (hasBlindness || useThickFog) {
            callbackInfo.cancel();
        }
    }
}