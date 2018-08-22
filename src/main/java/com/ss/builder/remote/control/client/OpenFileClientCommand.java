package com.ss.builder.remote.control.client;

import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.fx.component.bar.action.OpenAssetAction;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.AssetComponentLoadedEvent;
import com.ss.builder.fx.event.impl.RequestedOpenFileEvent;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.network.ConnectionOwner;
import com.ss.rlib.common.network.annotation.PacketDescription;
import javafx.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The command to open a file.
 *
 * @author JavaSaBr
 */
@PacketDescription(id = 1)
public class OpenFileClientCommand extends ClientCommand {

    @Override
    @BackgroundThread
    protected void readImpl(@NotNull ConnectionOwner owner, @NotNull ByteBuffer buffer) {

        var assetPath = Paths.get(readString(buffer));
        var fileToOpen = Paths.get(readString(buffer));

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();

        if (assetPath.equals(currentAsset)) {
            openFileInCurrentAsset(fileToOpen);
        } else {

            var executorManager = ExecutorManager.getInstance();
            executorManager.addFxTask(() -> {

                var action = new OpenAssetAction();
                action.openAssetFolder(assetPath);

                var eventManager = FxEventManager.getInstance();
                var eventHandler = new EventHandler<AssetComponentLoadedEvent>() {

                    @Override
                    public void handle(@NotNull AssetComponentLoadedEvent event) {
                        eventManager.removeEventHandler(AssetComponentLoadedEvent.EVENT_TYPE, this);
                        openFile(fileToOpen);
                    }
                };

                eventManager.addEventHandler(AssetComponentLoadedEvent.EVENT_TYPE, eventHandler);
            });

        }
    }

    @FromAnyThread
    private void openFileInCurrentAsset(@NotNull Path fileToOpen) {
        ExecutorManager.getInstance()
                .addFxTask(() -> openFile(fileToOpen));
    }

    /**
     * Open the file.
     *
     * @param fileToOpen the file.
     */
    @FxThread
    private void openFile(@NotNull Path fileToOpen) {

        FxEventManager.getInstance()
                .notify(new RequestedOpenFileEvent(fileToOpen));

        EditorUtils.requestFxFocus();
    }
}
