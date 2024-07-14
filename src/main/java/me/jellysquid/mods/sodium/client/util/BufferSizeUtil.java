package me.jellysquid.mods.sodium.client.util;

import net.minecraft.util.EnumWorldBlockLayer;

import java.util.HashMap;
import java.util.Map;

// Values was taken from RegionRenderCacheBuilder
public class BufferSizeUtil {

    public static final Map<EnumWorldBlockLayer, Integer> BUFFER_SIZES = new HashMap<>();

    static {
        BUFFER_SIZES.put(EnumWorldBlockLayer.SOLID, 2097152);
        BUFFER_SIZES.put(EnumWorldBlockLayer.CUTOUT, 131072);
        BUFFER_SIZES.put(EnumWorldBlockLayer.CUTOUT_MIPPED, 131072);
        BUFFER_SIZES.put(EnumWorldBlockLayer.TRANSLUCENT, 262144);
    }

}
