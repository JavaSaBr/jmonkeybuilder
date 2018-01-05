package com.ss.editor.manager;

import com.ss.editor.config.Config;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.network.NetworkFactory;
import com.ss.rlib.network.server.ServerNetwork;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private static @NotNull RemoteControlManager getInstance() {
        if (instance == null) instance = new RemoteControlManager();
        return instance;
    }

    @Nullable
    private ServerNetwork serverNetwork;

    private RemoteControlManager() {
        if(Config.REMOTE_CONTROL_PORT == -1) {
            return;
        }

        serverNetwork = NetworkFactory.newDefaultAsynchronousServerNetwork();
    }
}
