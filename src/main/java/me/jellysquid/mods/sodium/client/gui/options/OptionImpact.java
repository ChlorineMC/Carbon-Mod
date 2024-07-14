package me.jellysquid.mods.sodium.client.gui.options;


import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.ChatComponentTranslation;

public enum OptionImpact {
    LOW(ChatFormatting.GREEN, new ChatComponentTranslation("sodium.option_impact.low").getFormattedText()),
    MEDIUM(ChatFormatting.YELLOW, new ChatComponentTranslation("sodium.option_impact.medium").getFormattedText()),
    HIGH(ChatFormatting.GOLD, new ChatComponentTranslation("sodium.option_impact.high").getFormattedText()),
    EXTREME(ChatFormatting.RED, new ChatComponentTranslation("sodium.option_impact.extreme").getFormattedText()),
    VARIES(ChatFormatting.WHITE, new ChatComponentTranslation("sodium.option_impact.varies").getFormattedText());

    private final ChatFormatting color;
    private final String text;

    OptionImpact(ChatFormatting color, String text) {
        this.color = color;
        this.text = text;
    }

    public String toDisplayString() {
        return this.color + this.text;
    }
}
