package me.jellysquid.mods.sodium.mixin.core;

import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ChatComponentTranslation.class)
public abstract class MixinChatComponentTranslation extends ChatComponentStyle {
    /**
     * @author refactoring
     * @reason dirty hack to make life easier
     */
    @Overwrite
    public String toString() {
        return this.getFormattedText();
    }
}
