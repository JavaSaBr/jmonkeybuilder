package com.ss.editor.manager;

import com.ss.editor.config.Config;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
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

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(RemoteControlManager.class);

    @Nullable
    private static RemoteControlManager instance;

    public static @NotNull RemoteControlManager getInstance() {
        if (instance == null) instance = new RemoteControlManager();
        return instance;
    }

    @Nullable
    private ServerNetwork serverNetwork;

    private RemoteControlManager() {
        if(Config.REMOTE_CONTROL_PORT == -1) {
            return;
        }

        final ClasspathManager classpathManager = ClasspathManager.getInstance();
        final Class<ReadablePacket>[] packets = classpathManager.findImplements(ReadablePacket.class, ClasspathManager.Scope.ONLY_CORE)
                .toArray(Class.class);

        serverNetwork = NetworkFactory.newDefaultAsyncServerNetwork(ReadablePacketRegistry.of(packets));
        try {
            serverNetwork.bind(new InetSocketAddress(Config.REMOTE_CONTROL_PORT));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
