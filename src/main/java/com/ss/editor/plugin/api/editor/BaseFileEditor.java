package com.ss.editor.plugin.api.editor;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import javafx.event.Event;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The base implementation of {@link com.ss.editor.ui.component.editor.FileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class BaseFileEditor<S extends EditorState> extends AbstractFileEditor<StackPane> {
    /**
     * The state of this editor.
     */
    @Nullable
    protected S editorState;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListeners;

    @Override
    @FXThread
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        try {
            doOpenFile(file);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
    }

    /**
     * Loading a state of this editor.
     */
    @FXThread
    protected void loadState() {

        final Supplier<EditorState> stateFactory = getEditorStateFactory();
        if (stateFactory == null) {
            return;
        }

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace currentWorkspace = notNull(workspaceManager.getCurrentWorkspace());

        editorState = currentWorkspace.getEditorState(getEditFile(), stateFactory);
    }

    /**
     * Get the factory to make an editor state.
     *
     * @return the factory to make an editor state.
     */
    @FXThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return null;
    }

    /**
     * Do main activities to open the file.
     *
     * @param file the file to open.
     * @throws IOException if was some problem with writing to the file.
     */
    @FXThread
    protected void doOpenFile(@NotNull final Path file) throws IOException {
    }

    /**
     * @param ignoreListeners the flag for ignoring listeners.
     */
    @FromAnyThread
    protected void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the flag for ignoring listeners.
     */
    @FromAnyThread
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    @Override
    @FXThread
    protected @NotNull StackPane createRoot() {
        return new StackPane();
    }

    @Override
    @FXThread
    public @Nullable BorderPane get3DArea() {
        return null;
    }

    /**
     * Get the editor state.
     *
     * @return the editor state.
     */
    @FromAnyThread
    protected @Nullable S getEditorState() {
        return editorState;
    }

    @FXThread
    @Override
    public boolean isInside(final double sceneX, final double sceneY, @NotNull final Class<? extends Event> eventType) {
        return false;
    }
}
