package com.ss.editor.remote.control.client;

import com.ss.editor.JfxApplication;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.bar.action.OpenAssetAction;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.AssetComponentLoadedEvent;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.rlib.network.ConnectionOwner;
import com.ss.rlib.network.annotation.PacketDescription;
import javafx.event.Event;
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

    @NotNull
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @Override
    @BackgroundThread
    protected void readImpl(@NotNull final ConnectionOwner owner, @NotNull final ByteBuffer buffer) {

        final Path assetPath = Paths.get(readString(buffer));
        final Path fileToOpen = Paths.get(readString(buffer));

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        if (currentAsset != null && assetPath.equals(currentAsset)) {
            EXECUTOR_MANAGER.addFxTask(() -> openFile(fileToOpen));
        } else {
            EXECUTOR_MANAGER.addFxTask(() -> {

                final OpenAssetAction action = new OpenAssetAction();
                action.openAssetFolder(assetPath);

                final EventHandler<Event> eventHandler = new EventHandler<Event>() {

                    @Override
                    public void handle(final Event event) {
                        FX_EVENT_MANAGER.removeEventHandler(AssetComponentLoadedEvent.EVENT_TYPE, this);
                        openFile(fileToOpen);
                    }
                };

                FX_EVENT_MANAGER.addEventHandler(AssetComponentLoadedEvent.EVENT_TYPE, eventHandler);
            });

        }
    }

    /**
     * Open the file.
     *
     * @param fileToOpen the file.
     */
    private void openFile(@NotNull final Path fileToOpen) {
        FX_EVENT_MANAGER.notify(new RequestedOpenFileEvent(fileToOpen));
        JfxApplication.getInstance().requestFocus();
    }
}
