package me.jellysquid.mods.sodium.mixin.features.block;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.vertex.VanillaVertexTypes;
import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.QuadVertexSink;
import me.jellysquid.mods.sodium.client.render.texture.SpriteUtil;
import me.jellysquid.mods.sodium.client.util.ModelQuadUtil;
import me.jellysquid.mods.sodium.client.util.math.MatrixStack;
import me.jellysquid.mods.sodium.client.util.rand.XoRoShiRoRandom;
import me.jellysquid.mods.sodium.common.util.DirectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {
    private final XoRoShiRoRandom random = new XoRoShiRoRandom();

    /**
     * @reason Use optimized vertex writer intrinsics, avoid allocations
     * @author JellySquid
     */
    // TODO Light
    @Overwrite
    public boolean renderModel(IBlockAccess world, IBakedModel bakedModel, IBlockState blockState, BlockPos pos, WorldRenderer buffer) {
        boolean flag = false;
        QuadVertexSink drain = VertexDrain.of(buffer)
                .createSink(VanillaVertexTypes.QUADS);
        XoRoShiRoRandom random = this.random;
        MatrixStack matrixStack = new MatrixStack();

        random.setSeed(42L);
        for (EnumFacing direction : DirectionUtil.ALL_DIRECTIONS) {
            List<BakedQuad> quads = bakedModel.getFaceQuads(direction);

            if (!quads.isEmpty()) {
                renderQuad(matrixStack.peek(), drain, quads, 200, 0);
                flag = true;
            }
        }

        random.setSeed(42L);
        List<BakedQuad> quads = bakedModel.getGeneralQuads();

        if (!quads.isEmpty()) {
            renderQuad(matrixStack.peek(), drain, quads, 200, 0);
            flag = true;
        }

        drain.flush();

        return flag;
    }

    private static void renderQuad(MatrixStack.Entry entry, QuadVertexSink drain, List<BakedQuad> list, int light, int overlay) {
        if (list.isEmpty()) {
            return;
        }

        drain.ensureCapacity(list.size() * 4);

        for (BakedQuad bakedQuad : list) {
            int color = bakedQuad.hasTintIndex() ? bakedQuad.getTintIndex() : 0xFFFFFFFF;

            ModelQuadView quad = ((ModelQuadView) bakedQuad);

            for (int i = 0; i < 4; i++) {
                drain.writeQuad(entry, quad.getX(i), quad.getY(i), quad.getZ(i), color, quad.getTexU(i), quad.getTexV(i),
                        light, overlay, ModelQuadUtil.getFacingNormal(bakedQuad.getFace(), quad.getNormal(i)));
            }

            SpriteUtil.markSpriteActive(quad.rubidium$getSprite());
        }
    }
}
