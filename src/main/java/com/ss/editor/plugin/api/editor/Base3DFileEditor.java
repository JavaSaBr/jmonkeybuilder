package com.ss.editor.plugin.api.editor;

import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.part3d.editor.impl.AbstractEditor3DPart;
import com.ss.editor.ui.component.editor.state.impl.Editor3DEditorState;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The base implementation of {@link com.ss.editor.ui.component.editor.FileEditor} with 3D scene.
 *
 * @author JavaSaBr
 */
public abstract class Base3DFileEditor<T extends AbstractEditor3DPart, S extends Editor3DEditorState> extends
        BaseFileEditor<S> {

    /**
     * The 3D part of this editor.
     */
    @NotNull
    private final T editor3DPart;

    public Base3DFileEditor() {
        this.editor3DPart = create3DEditorPart();
        addEditor3DPart(editor3DPart);
    }

    @Override
    @FxThread
    public void openFile(@NotNull final Path file) {
        super.openFile(file);
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
    @FxThread
    protected abstract @NotNull T create3DEditorPart();

    /**
     * Get the 3D part of this editor.
     *
     * @return the 3D part of this editor.
     */
    @FromAnyThread
    protected @NotNull T getEditor3DPart() {
        return editor3DPart;
    }

    @Override
    @FxThread
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
    @FxThread
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
