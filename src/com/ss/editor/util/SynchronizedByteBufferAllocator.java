package com.ss.editor.util;

import com.jme3.util.LWJGLBufferAllocator;
import org.jetbrains.annotations.NotNull;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author JavaSaBr
 */
public class SynchronizedByteBufferAllocator extends LWJGLBufferAllocator {

    @Override
    public synchronized void destroyDirectBuffer(@NotNull final Buffer buffer) {
        super.destroyDirectBuffer(buffer);
    }

    @Override
    public synchronized ByteBuffer allocate(final int size) {
        return super.allocate(size);
    }
}
