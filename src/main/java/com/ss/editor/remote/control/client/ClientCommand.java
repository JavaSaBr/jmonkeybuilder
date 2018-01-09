package com.ss.editor.remote.control.client;

import com.ss.editor.annotation.BackgroundThread;
import com.ss.rlib.network.ConnectionOwner;
import com.ss.rlib.network.packet.impl.AbstractReadablePacket;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * The base implementation of a command.
 *
 * @author JavaSaBr
 */
public abstract class ClientCommand extends AbstractReadablePacket {

    @Override
    @BackgroundThread
    protected abstract void readImpl(@NotNull final ConnectionOwner owner, @NotNull final ByteBuffer buffer);
}
