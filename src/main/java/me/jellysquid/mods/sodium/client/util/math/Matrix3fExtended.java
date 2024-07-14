package me.jellysquid.mods.sodium.client.util.math;

import net.minecraft.util.EnumFacing;
import repack.joml.Vector3f;

public interface Matrix3fExtended {
    float transformVecX(float x, float y, float z);

    float transformVecY(float x, float y, float z);

    float transformVecZ(float x, float y, float z);

    default float transformVecX(Vector3f dir) {
        return this.transformVecX(dir.x, dir.y, dir.z);
    }

    default float transformVecY(Vector3f dir) {
        return this.transformVecY(dir.x, dir.y, dir.z);
    }

    default float transformVecZ(Vector3f dir) {
        return this.transformVecZ(dir.x, dir.y, dir.z);
    }
}
