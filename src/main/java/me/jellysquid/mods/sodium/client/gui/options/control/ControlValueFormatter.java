package me.jellysquid.mods.sodium.client.gui.options.control;

import net.minecraft.util.ChatComponentTranslation;

public interface ControlValueFormatter {
    static ControlValueFormatter guiScale() {
        return (v) -> (v == 0) ? new ChatComponentTranslation("options.guiScale.auto").getFormattedText() : new ChatComponentTranslation(v + "x").getFormattedText();
    }

    static ControlValueFormatter fpsLimit() {
        return (v) -> (v == 260) ? new ChatComponentTranslation("options.framerateLimit.max").getFormattedText() : new ChatComponentTranslation("options.framerate", v).getFormattedText();
    }

    static ControlValueFormatter brightness() {
        return (v) -> {
            if (v == 0) {
                return new ChatComponentTranslation("options.gamma.min").getFormattedText();
            } else if (v == 100) {
                return new ChatComponentTranslation("options.gamma.max").getFormattedText();
            } else {
                return v + "%";
            }
        };
    }

    String format(int value);

    static ControlValueFormatter percentage() {
        return (v) -> v + "%";
    }

    static ControlValueFormatter multiplier() {
        return (v) -> new ChatComponentTranslation(v + "x").getFormattedText();
    }

    static ControlValueFormatter quantity(String name) {
        return (v) -> new ChatComponentTranslation(name, v).getFormattedText();
    }

    static ControlValueFormatter quantityOrDisabled(String name, String disableText) {
        return (v) -> new ChatComponentTranslation(v == 0 ? disableText : name, v).getFormattedText();
    }

    static ControlValueFormatter number() {
        return String::valueOf;
    }
}
