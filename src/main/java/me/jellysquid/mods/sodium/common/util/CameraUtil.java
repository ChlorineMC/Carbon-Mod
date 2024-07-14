package me.jellysquid.mods.sodium.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

public class CameraUtil {
    public static Vec3 getCameraPosition(float partialTicks) {
        return Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(partialTicks);
    }
}
