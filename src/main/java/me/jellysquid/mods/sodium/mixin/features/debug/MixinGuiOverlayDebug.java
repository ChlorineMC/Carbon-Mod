package me.jellysquid.mods.sodium.mixin.features.debug;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderBackend;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiOverlayDebug.class)
public abstract class MixinGuiOverlayDebug {
    @Shadow
    private static long bytesToMb(long bytes) {
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "getDebugInfoRight", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"))
    private ArrayList<String> redirectRightTextEarly(Object[] elements) {
        ArrayList<String> strings = Lists.newArrayList((String[]) elements);
        strings.add("");
        strings.add(SodiumClientMod.MODNAME + " Renderer");
        strings.add(ChatFormatting.UNDERLINE + getFormattedVersionText());
        strings.add("");
        strings.addAll(getChunkRendererDebugStrings());

        if (SodiumClientMod.options().advanced.ignoreDriverBlacklist) {
            strings.add(ChatFormatting.RED + "(!!) Driver blacklist ignored");
        }

        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);

            if (str.startsWith("Allocated:")) {
                strings.add(i + 1, getNativeMemoryString());

                break;
            }
        }

        return strings;
    }

    private static String getFormattedVersionText() {
        String version = SodiumClientMod.getVersion();
        ChatFormatting color;

        if (version.contains("git.")) {
            color = ChatFormatting.RED;
        } else {
            color = ChatFormatting.GREEN;
        }

        return color + version;
    }

    private static List<String> getChunkRendererDebugStrings() {
        SodiumWorldRenderer renderer = SodiumWorldRenderer.getInstanceNullable();
        if (renderer == null)
            return ImmutableList.of();
        ChunkRenderBackend<?> backend = renderer.getChunkRenderer();

        List<String> strings = new ArrayList<>(5);
        strings.add("Chunk Renderer: " + backend.getRendererName());
        strings.addAll(backend.getDebugStrings());

        return strings;
    }

    private static String getNativeMemoryString() {
        return "Off-Heap: +" + bytesToMb(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()) + "MB";
    }
}
