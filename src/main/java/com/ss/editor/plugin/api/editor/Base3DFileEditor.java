package com.ss.editor.plugin.api.editor;

import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.state.editor.Editor3DState;
import com.ss.editor.ui.component.editor.state.impl.Editor3DWithEditorToolEditorState;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The base implementation of {@link com.ss.editor.ui.component.editor.FileEditor} with 3D scene.
 *
 * @author JavaSaBr
 */
public abstract class Base3DFileEditor<T extends Editor3DState, S extends Editor3DWithEditorToolEditorState> extends
        BaseFileEditor<S> implements UndoableEditor, ChangeConsumer {

    /**
     * The operation control.
     */
    @NotNull
    private final EditorOperationControl operationControl;

    /**
     * The 3D part of this editor.
     */
    @NotNull
    private final T editor3DState;

    /**
     * The changes counter.
     */
    @NotNull
    private final AtomicInteger changeCounter;

    public Base3DFileEditor() {
        this.editor3DState = create3DEditorState();
        this.operationControl = createOperationControl();
        this.changeCounter = new AtomicInteger();
        addEditorState(editor3DState);
    }

    @Override
    @FXThread
    public void openFile(@NotNull final Path file) {
        super.openFile(file);
    }

    /**
     * Create an editor operation control.
     *
     * @return the editor operation control.
     */
    protected @NotNull EditorOperationControl createOperationControl() {
        return new EditorOperationControl(this);
    }

    @Override
    protected boolean needListenEventsFromPage() {
        return false;
    }

    /**
     * Create 3D part of this editor.
     *
     * @return the 3D part.
     */
    @FXThread
    protected abstract @NotNull T create3DEditorState();

    @Override
    @FromAnyThread
    public void execute(@NotNull final EditorOperation operation) {
        operationControl.execute(operation);
    }

    @Override
    @FXThread
    public void incrementChange() {
        final int result = changeCounter.incrementAndGet();
        setDirty(result != 0);
    }

    @Override
    @FXThread
    public void decrementChange() {
        final int result = changeCounter.decrementAndGet();
        setDirty(result != 0);
    }

    @Override
    @FromAnyThread
    public void redo() {
        operationControl.redo();
    }

    @Override
    @FromAnyThread
    public void undo() {
        operationControl.undo();
    }

    /**
     * Get the editor operation control.
     *
     * @return the editor operation control.
     */
    @FromAnyThread
    protected @NotNull EditorOperationControl getOperationControl() {
        return operationControl;
    }

    /**
     * Get the 3D part of this editor.
     *
     * @return the 3D part of this editor.
     */
    @FromAnyThread
    protected @NotNull T getEditor3DState() {
        return editor3DState;
    }

    @Override
    @FXThread
    public void notifyChangedCameraSettings(@NotNull final Vector3f cameraLocation, final float hRotation,
                                            final float vRotation, final float targetDistance,
                                            final float cameraSpeed) {
        super.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed);

        final S editorState = getEditorState();
        if (editorState == null) return;

        editorState.setCameraHRotation(hRotation);
        editorState.setCameraVRotation(vRotation);
        editorState.setCameraTDistance(targetDistance);
        editorState.setCameraLocation(cameraLocation);
        editorState.setCameraSpeed(cameraSpeed);
    }

    @Override
    @FXThread
    public boolean isInside(final double sceneX, final double sceneY, @NotNull final Class<? extends Event> eventType) {

        final Pane editorPage = getPage();
        final Pane editor3DPage = get3DArea() == null ? editorPage : get3DArea();

        final boolean only3D = eventType.isAssignableFrom(MouseEvent.class) ||
                eventType.isAssignableFrom(ScrollEvent.class);

        final Pane page = only3D ? editor3DPage : editorPage;

        final Point2D point2D = page.sceneToLocal(sceneX, sceneY);
        return page.contains(point2D);
    }
}
