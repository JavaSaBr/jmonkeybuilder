package com.ss.editor.manager;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.Config;
import com.ss.editor.manager.AsyncEventManager.CombinedAsyncEventHandlerBuilder;
import com.ss.editor.manager.ClasspathManager.Scope;
import com.ss.editor.ui.event.impl.CoreClassesScannedEvent;
import com.ss.editor.ui.event.impl.FxContextCreatedEvent;
import com.ss.editor.ui.event.impl.JmeContextCreatedEvent;
import com.ss.editor.util.TimeTracker;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.network.NetworkConfig;
import com.ss.rlib.common.network.NetworkFactory;
import com.ss.rlib.common.network.packet.ReadablePacket;
import com.ss.rlib.common.network.packet.ReadablePacketRegistry;
import com.ss.rlib.common.network.server.AcceptHandler;
import com.ss.rlib.common.network.server.ServerNetwork;
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

    /**
     * The packet registry.
     */
    @Nullable
    private volatile ReadablePacketRegistry packetRegistry;

    /**
     * The control server.
     */
    @Nullable
    private volatile ServerNetwork serverNetwork;

    /**
     * True if jME context is already created.
     */
    private volatile boolean canStart;

    private RemoteControlManager() {
        InitializeManager.valid(getClass());

        if (Config.REMOTE_CONTROL_PORT == -1) {
            return;
        }

        CombinedAsyncEventHandlerBuilder.of(this::createPacketRegistry)
                .add(CoreClassesScannedEvent.EVENT_TYPE)
                .buildAndRegister();

        CombinedAsyncEventHandlerBuilder.of(this::start)
                .add(JmeContextCreatedEvent.EVENT_TYPE)
                .add(FxContextCreatedEvent.EVENT_TYPE)
                .buildAndRegister();
    }

    /**
     * Get a packet registry.
     *
     * @return the packet registry.
     */
    @FromAnyThread
    private @Nullable ReadablePacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    /**
     * Create a packet registry when the {@link ClasspathManager} will be initialized.
     */
    @BackgroundThread
    private synchronized void createPacketRegistry() {

        var classpathManager = ClasspathManager.getInstance();

        Class<ReadablePacket>[] packets = unsafeCast(classpathManager
                .findImplements(ReadablePacket.class, Scope.ONLY_CORE)
                .toArray(Class.class));

        this.packetRegistry = ReadablePacketRegistry.of(packets);

        if (canStart) {
            start();
        }
    }

    /**
     * Start the remote control in background.
     */
    @BackgroundThread
    private void startInBackground() {

        var serverNetwork = NetworkFactory.newDefaultAsyncServerNetwork(NETWORK_CONFIG,
                packetRegistry, AcceptHandler.newDefault());

        try {
            serverNetwork.bind(new InetSocketAddress(Config.REMOTE_CONTROL_PORT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.serverNetwork = serverNetwork;
    }

    /**
     * Start the remote control if need.
     */
    @FromAnyThread
    private synchronized void start() {
        canStart = true;

        var packetRegistry = getPacketRegistry();
        if (packetRegistry == null) {
            return;
        }

        ExecutorManager.getInstance()
                .addBackgroundTask(this::startInBackground);
    }
}
