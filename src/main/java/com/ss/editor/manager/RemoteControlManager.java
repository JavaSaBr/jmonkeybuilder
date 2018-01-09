package com.ss.editor.manager;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.Config;
import com.ss.rlib.network.NetworkFactory;
import com.ss.rlib.network.packet.ReadablePacket;
import com.ss.rlib.network.packet.ReadablePacketRegistry;
import com.ss.rlib.network.server.ServerNetwork;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The manager to process remote control.
 *
 * @author JavaSaBr
 */
public class RemoteControlManager {

    @Nullable
    private static RemoteControlManager instance;

    public static @NotNull RemoteControlManager getInstance() {
        if (instance == null) instance = new RemoteControlManager();
        return instance;
    }

    @NotNull
    private final ReadablePacketRegistry packetRegistry;

    @Nullable
    private volatile ServerNetwork serverNetwork;

    private RemoteControlManager() {

        final ClasspathManager classpathManager = ClasspathManager.getInstance();
        final Class<ReadablePacket>[] packets = classpathManager.findImplements(ReadablePacket.class, ClasspathManager.Scope.ONLY_CORE)
                .toArray(Class.class);

        this.packetRegistry = ReadablePacketRegistry.of(packets);

        final InitializationManager initializationManager = InitializationManager.getInstance();
        initializationManager.addOnAfterCreateJMEContext(this::start);
    }

    /**
     * Start the remote control if need.
     */
    @FromAnyThread
    private synchronized void start() {

        if (Config.REMOTE_CONTROL_PORT == -1) {
            return;
        }

        serverNetwork = NetworkFactory.newDefaultAsyncServerNetwork(packetRegistry);
        try {
            serverNetwork.bind(new InetSocketAddress(Config.REMOTE_CONTROL_PORT));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
