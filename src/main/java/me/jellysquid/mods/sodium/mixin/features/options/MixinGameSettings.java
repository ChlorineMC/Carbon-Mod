package me.jellysquid.mods.sodium.mixin.features.options;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameSettings.class)
public class MixinGameSettings {
    @Shadow
    public int renderDistanceChunks;

    @Shadow
    public boolean fancyGraphics;

    /**
     * @author Asek3
     * @reason Implemented cloud rendering option
     */
    @Overwrite
    public int shouldRenderClouds() {
        SodiumGameOptions options = SodiumClientMod.options();

        if (this.renderDistanceChunks < 4 || !options.quality.enableClouds) {
            return 0;
        }

        return options.quality.cloudQuality.isFancy(this.fancyGraphics) ? 2 : 1;
    }
}