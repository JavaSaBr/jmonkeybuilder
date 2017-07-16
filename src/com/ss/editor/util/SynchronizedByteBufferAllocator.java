package com.ss.editor.util;

import com.jme3.util.LWJGLBufferAllocator;
import org.jetbrains.annotations.NotNull;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.StampedLock;

/**
 * @author JavaSaBr
 */
public class SynchronizedByteBufferAllocator extends LWJGLBufferAllocator {

    /**
     * The synchronizer.
     */
    @NotNull
    private final StampedLock stampedLock;

    public SynchronizedByteBufferAllocator() {
        this.stampedLock = new StampedLock();
    }

    @Override
    public void destroyDirectBuffer(@NotNull final Buffer buffer) {
        final long stamp = stampedLock.writeLock();
        try {
            super.destroyDirectBuffer(buffer);
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    @Override
    public ByteBuffer allocate(final int size) {
        final long stamp = stampedLock.writeLock();
        try {
            return super.allocate(size);
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }
}
