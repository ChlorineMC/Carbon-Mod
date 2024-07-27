package me.jellysquid.mods.sodium.mixin.core;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.util.List;

@Mixin(Locale.class)
public abstract class MixinSimpleReloadableResourceManager {
    @Shadow
    protected abstract void loadLocaleData(InputStream stream);

    @Inject(method = "loadLocaleDataFiles", at = @At("RETURN"))
    public void sodium$loadLanguage(IResourceManager manager, List<String> locales, CallbackInfo ci) {
        InputStream stream = Locale.class.getResourceAsStream("/assets/sodium/lang/en_us.lang");
        loadLocaleData(stream);
    }
}