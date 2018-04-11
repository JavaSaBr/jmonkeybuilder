package com.ss.editor.manager;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.Config;
import com.ss.editor.manager.ClasspathManager.Scope;
import com.ss.rlib.network.NetworkConfig;
import com.ss.rlib.network.NetworkFactory;
import com.ss.rlib.network.packet.ReadablePacket;
import com.ss.rlib.network.packet.ReadablePacketRegistry;
import com.ss.rlib.network.server.AcceptHandler;
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

    @NotNull
    private static final NetworkConfig NETWORK_CONFIG = new NetworkConfig() {

        @Override
        public int getReadBufferSize() {
            return Short.MAX_VALUE * 2;
        }

        @Override
        public int getWriteBufferSize() {
            return Short.MAX_VALUE * 2;
        }
    };

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
        final Class<ReadablePacket>[] packets = classpathManager.findImplements(ReadablePacket.class, Scope.ONLY_CORE)
                .toArray(Class.class);

        this.packetRegistry = ReadablePacketRegistry.of(packets);

        final InitializationManager initializationManager = InitializationManager.getInstance();
        initializationManager.addOnAfterCreateJmeContext(this::start);
    }

    /**
     * Start the remote control if need.
     */
    @FromAnyThread
    private synchronized void start() {

        if (Config.REMOTE_CONTROL_PORT == -1) {
            return;
        }

        serverNetwork = NetworkFactory.newDefaultAsyncServerNetwork(NETWORK_CONFIG, packetRegistry, AcceptHandler.newDefault());
        try {
            serverNetwork.bind(new InetSocketAddress(Config.REMOTE_CONTROL_PORT));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
