package me.jellysquid.mods.sodium.mixin.core.pipeline;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gl.attribute.BufferVertexFormat;
import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.VertexSink;
import me.jellysquid.mods.sodium.client.model.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.client.model.vertex.type.BlittableVertexType;
import me.jellysquid.mods.sodium.client.model.vertex.type.VertexType;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements VertexBufferView, VertexDrain {

    @Shadow
    private ByteBuffer byteBuffer;

    private static Logger LOGGER = LogManager.getLogger("WR");

    @Shadow
    private VertexFormat vertexFormat;

    @Shadow
    private int vertexCount;

    @Unique
    private static int roundBufferSize(int amount) {
        int i = 2097152;
        if (amount == 0) {
            return i;
        } else {
            if (amount < 0) {
                i *= -1;
            }

            int j = amount % i;
            return j == 0 ? amount : amount + i - j;
        }
    }
    
    @Override
    public boolean ensureBufferCapacity(int bytes) {
    	if(vertexFormat != null) {
            // Ensure that there is always space for 1 more vertex; see BufferBuilder.next()
            bytes += vertexFormat.getIntegerSize();
        }

        if (this.vertexCount * this.vertexFormat.getIntegerSize() + bytes <= this.byteBuffer.capacity()) {
            return false;
        }

        int newSize = this.byteBuffer.capacity() + roundBufferSize(bytes);

        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", this.byteBuffer.capacity(), newSize);

        this.byteBuffer.position(0);

        ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(newSize);
        byteBuffer.put(this.byteBuffer);
        byteBuffer.rewind();

        this.byteBuffer = byteBuffer;

        return true;
    }

    @Override
    public ByteBuffer getDirectBuffer() {
        return this.byteBuffer;
    }

    @Override
    public int getWriterPosition() {
        return this.vertexCount * this.vertexFormat.getIntegerSize();
    }

    @Override
    public BufferVertexFormat getVertexFormat() {
        return BufferVertexFormat.from(this.vertexFormat);
    }

    @Override
    public void flush(int vertexCount, BufferVertexFormat format) {
        if (BufferVertexFormat.from(this.vertexFormat) != format) {
            throw new IllegalStateException("Mis-matched vertex format (expected: [" + format + "], currently using: [" + this.vertexFormat + "])");
        }

        this.vertexCount += vertexCount;
        //this.elementOffset += vertexCount * format.getStride();
    }

    @Override
    public <T extends VertexSink> T createSink(VertexType<T> factory) {
        BlittableVertexType<T> blittable = factory.asBlittable();

        if (blittable != null && blittable.getBufferVertexFormat() == this.getVertexFormat())  {
            return blittable.createBufferWriter(this, SodiumClientMod.isDirectMemoryAccessEnabled());
        }

        return factory.createFallbackWriter((WorldRenderer) (Object) this);
    }
}
