package com.ss.editor.plugin.api.editor;

import com.jme3.math.Vector3f;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.editor.part3d.impl.AbstractEditor3dPart;
import com.ss.editor.ui.component.editor.state.impl.Editor3dEditorState;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The base implementation of {@link com.ss.editor.ui.component.editor.FileEditor} with 3D scene.
 *
 * @author JavaSaBr
 */
public abstract class Base3dFileEditor<T extends AbstractEditor3dPart, S extends Editor3dEditorState> extends
        BaseFileEditor<S> {

    /**
     * The 3D part of this editor.
     */
    @NotNull
    protected final T editor3dPart;

    public Base3dFileEditor() {
        this.editor3dPart = create3dEditorPart();
        addEditor3dPart(editor3dPart);
    }

    @Override
    @BackgroundThread
    public void openFile(@NotNull Path file) {
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
    protected abstract @NotNull T create3dEditorPart();

    @Override
    @FxThread
    public void notifyChangedCameraSettings(
            @NotNull Vector3f cameraLocation,
            float hRotation,
            float vRotation,
            float targetDistance,
            float cameraSpeed
    ) {

        super.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed);

        var state = getEditorState();

        if (state != null) {
            state.setCameraHRotation(hRotation);
            state.setCameraVRotation(vRotation);
            state.setCameraTargetDistance(targetDistance);
            state.setCameraLocation(cameraLocation);
            state.setCameraFlySpeed(cameraSpeed);
        }
    }

    @Override
    @FxThread
    public boolean isInside(double sceneX, double sceneY, @NotNull Class<? extends Event> eventType) {

        var editorPage = getUiPage();
        var editor3dPage = get3dArea() == null ? editorPage : get3dArea();

        var only3D = eventType.isAssignableFrom(MouseEvent.class) ||
                eventType.isAssignableFrom(ScrollEvent.class);

        var page = only3D ? editor3dPage : editorPage;
        var point2d = page.sceneToLocal(sceneX, sceneY);

        return page.contains(point2d);
    }
}
