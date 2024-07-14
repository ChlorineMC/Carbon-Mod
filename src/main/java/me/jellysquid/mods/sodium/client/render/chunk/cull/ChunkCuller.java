package me.jellysquid.mods.sodium.client.render.chunk.cull;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.jellysquid.mods.sodium.client.util.math.FrustumExtended;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.Vec3;

public interface ChunkCuller {
    IntArrayList computeVisible(Vec3 cameraPos, FrustumExtended frustum, int frame, boolean spectator);

    void onSectionStateChanged(int x, int y, int z, SetVisibility occlusionData);
    void onSectionLoaded(int x, int y, int z, int id);
    void onSectionUnloaded(int x, int y, int z);

    boolean isSectionVisible(int x, int y, int z);
}
