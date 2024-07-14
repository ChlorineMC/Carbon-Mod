package me.jellysquid.mods.sodium.client.render.chunk.tasks;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkMeshData;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import me.jellysquid.mods.sodium.client.render.pipeline.context.ChunkRenderCacheLocal;
import me.jellysquid.mods.sodium.client.util.BlockRenderType;
import me.jellysquid.mods.sodium.client.util.MathUtil;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import me.jellysquid.mods.sodium.common.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldType;

/**
 * Rebuilds all the meshes of a chunk for each given render pass with non-occluded blocks. The result is then uploaded
 * to graphics memory on the main thread.
 *
 * This task takes a slice of the world from the thread it is created on. Since these slices require rather large
 * array allocations, they are pooled to ensure that the garbage collector doesn't become overloaded.
 */
public class ChunkRenderRebuildTask<T extends ChunkGraphicsState> extends ChunkRenderBuildTask<T> {
    private static final EnumWorldBlockLayer[] LAYERS = EnumWorldBlockLayer.values();
    private final ChunkRenderContainer<T> render;
        
    private final BlockPos offset;

    private final ChunkRenderContext context;

    private Vec3 camera;

    private final boolean translucencySorting;

    public ChunkRenderRebuildTask(ChunkRenderContainer<T> render, ChunkRenderContext context, BlockPos offset) {
        this.render = render;
        this.offset = offset;
        this.context = context;
        this.camera = new Vec3(0.0F, 0.0F, 0.0F);
        this.translucencySorting = SodiumClientMod.options().advanced.translucencySorting;
    }

    public ChunkRenderRebuildTask<T> withCameraPosition(Vec3 camera) {
        this.camera = camera;
        return this;
    }

    @Override
    public ChunkBuildResult<T> performBuild(ChunkRenderCacheLocal cache, ChunkBuildBuffers buffers, CancellationSource cancellationSource) {
        // COMPATIBLITY NOTE: Oculus relies on the LVT of this method being unchanged, at least in 16.5
        ChunkRenderData.Builder renderData = new ChunkRenderData.Builder();
        VisGraph occluder = new VisGraph();
        ChunkRenderBounds.Builder bounds = new ChunkRenderBounds.Builder();

        buffers.init(renderData);

        cache.init(this.context);

        WorldSlice slice = cache.getWorldSlice();

        int baseX = this.render.getOriginX();
        int baseY = this.render.getOriginY();
        int baseZ = this.render.getOriginZ();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos renderOffset = this.offset;

        try {
            for (int relY = 0; relY < 16; relY++) {
                if (cancellationSource.isCancelled()) {
                    return null;
                }

                for (int relZ = 0; relZ < 16; relZ++) {
                    for (int relX = 0; relX < 16; relX++) {
                        IBlockState blockState = slice.getBlockStateRelative(relX + 16, relY + 16, relZ + 16);
                        Block block = blockState.getBlock();

                        // If the block is vanilla air, assume it renders nothing. Don't use isAir because mods
                        // can abuse it for all sorts of things
                        if (block.getMaterial() == Material.air) {
                            continue;
                        }

                        int renderType = block.getRenderType();

                        pos.set(baseX + relX, baseY + relY, baseZ + relZ);
                        buffers.setRenderOffset(pos.getX() - renderOffset.getX(), pos.getY() - renderOffset.getY(), pos.getZ() - renderOffset.getZ());

                        if(!BlockRenderType.isInvisible(renderType)) {
                            if (slice.getWorldType() != WorldType.DEBUG_WORLD) {
                                blockState = block.getActualState(blockState, slice, pos);
                            }

                            for(EnumWorldBlockLayer layer : LAYERS) {
                                if(!(block.getBlockLayer() == layer)) {
                                    continue;
                                }

//                                ForgeHooksClient.setRenderLayer(layer);

                                if (BlockRenderType.isModel(renderType) && WorldUtil.toFluidBlock(block) == null) {
                                    IBakedModel model = cache.getBlockModels()
                                            .getModelForState(blockState);

                                    final long seed = MathUtil.hashPos(pos);

                                    if (cache.getBlockRenderer().renderModel(cache.getLocalSlice(), blockState.getBlock().getActualState(blockState, slice, pos), pos, model, buffers.get(layer), true, seed)) {
                                        bounds.addBlock(relX, relY, relZ);
                                    }

                                } else if (WorldUtil.toFluidBlock(block) != null) {
                                    if (cache.getFluidRenderer().render(cache.getLocalSlice(), blockState, pos, buffers.get(layer))) {
                                        bounds.addBlock(relX, relY, relZ);
                                    }
                                }
                            }
                        }

                        if (block.hasTileEntity()) {
                            TileEntity entity = slice.getTileEntity(pos);

                            if (entity != null) {
                                TileEntitySpecialRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(entity);

                                if (renderer != null) {
                                    renderData.addBlockEntity(entity, !renderer.forceTileEntityRender());

                                    bounds.addBlock(relX, relY, relZ);
                                }
                            }
                        }

                        if (block.isOpaqueCube()) {
                            occluder.func_178606_a(pos);
                        }
                    }
                }
            }
        } catch (ReportedException ex) {
            // Propagate existing crashes (add context)
            throw fillCrashInfo(ex.getCrashReport(), slice, pos);
        } catch (Throwable ex) {
            // Create a new crash report for other exceptions (e.g. thrown in getQuads)
            throw fillCrashInfo(CrashReport.makeCrashReport(ex, "Encountered exception while building chunk meshes"), slice, pos);
        }

        
//        ForgeHooksClient.setRenderLayer(null);

        render.setRebuildForTranslucents(false);
        for (BlockRenderPass pass : BlockRenderPass.VALUES) {
            ChunkMeshData mesh = buffers.createMesh(pass, (float)camera.xCoord - offset.getX(), (float)camera.yCoord - offset.getY(), (float)camera.zCoord - offset.getZ(), this.translucencySorting);

            if (mesh != null) {
                renderData.setMesh(pass, mesh);
                if(this.translucencySorting && pass.isTranslucent())
                    render.setRebuildForTranslucents(true);
            }
        }

        renderData.setOcclusionData(occluder.computeVisibility());
        renderData.setBounds(bounds.build(this.render.getChunkPos()));

//        MinecraftForge.EVENT_BUS.post(new ChunkDataBuiltEvent(renderData));

        return new ChunkBuildResult<>(this.render, renderData.build());
    }

    private ReportedException fillCrashInfo(CrashReport report, WorldSlice slice, BlockPos pos) {
        CrashReportCategory crashReportSection = report.makeCategoryDepth("Block being rendered", 1);

        IBlockState state = null;
        try {
            state = slice.getBlockState(pos);
        } catch (Exception ignored) {}
        CrashReportCategory.addBlockInfo(crashReportSection, pos, state);

        crashReportSection.addCrashSection("Chunk section", render);
        if (context != null) {
            crashReportSection.addCrashSection("Render context volume", context.getVolume());
        }

        return new ReportedException(report);
    }

    @Override
    public void releaseResources() {
        this.context.releaseResources();
    }
}
