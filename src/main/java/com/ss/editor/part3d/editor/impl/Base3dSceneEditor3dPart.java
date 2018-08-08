package com.ss.editor.part3d.editor.impl;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.part3d.editor.SavableEditor3dPart;
import com.ss.editor.part3d.editor.UndoableEditor3dPart;
import com.ss.editor.part3d.editor.control.Editor3dPartControl;
import com.ss.editor.part3d.editor.control.impl.BaseHotKeysEditor3dPartControl;
import com.ss.editor.part3d.editor.control.impl.CameraEditor3dPartControl;
import com.ss.editor.part3d.editor.control.impl.InputStateEditor3dPartControl;
import com.ss.editor.plugin.api.RenderFilterRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.component.editor.UndoableFileEditor;
import com.ss.editor.util.EditorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The base implementation of the {@link Editor3dPart} for a file editor.
 *
 * @param <T> the type of a file editor.
 * @author JavaSaBr
 */
public abstract class Base3dSceneEditor3dPart<T extends FileEditor> extends AbstractEditor3dPart<T> implements
        UndoableEditor3dPart, SavableEditor3dPart {

    public Base3dSceneEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        controls.add(new InputStateEditor3dPartControl(this));
        controls.add(new BaseHotKeysEditor3dPartControl<>(this));
        createCameraControl().ifPresent(controls::add);
    }

    @BackgroundThread
    protected @NotNull Optional<CameraEditor3dPartControl> createCameraControl() {
        return Optional.of(new CameraEditor3dPartControl(this, EditorUtils.getGlobalCamera()));
    }

    @Override
    @JmeThread
    public void undo() {
        if (fileEditor instanceof UndoableFileEditor) {
            ((UndoableFileEditor) fileEditor).undo().join();
        }
    }

    @Override
    @JmeThread
    public void redo() {
        if (fileEditor instanceof UndoableFileEditor) {
            ((UndoableFileEditor) fileEditor).redo().join();
        }
    }

    @Override
    @JmeThread
    public @NotNull CompletableFuture<FileEditor> save() {
        return fileEditor.save();
    }

    @Override
    @JmeThread
    public boolean isDirty() {
        return fileEditor.isDirty();
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);

        var rootNode = EditorUtils.getRootNode(application);
        rootNode.attachChild(stateNode);

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.enableFilters();
    }

    @Override
    @JmeThread
    public void cleanup() {

        var filterRegistry = RenderFilterRegistry.getInstance();
        filterRegistry.disableFilters();

        var rootNode = EditorUtils.getGlobalRootNode();
        rootNode.detachChild(stateNode);

        super.cleanup();
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);
        controls.forEach(tpf, Editor3dPartControl::update);
        controls.forEach(tpf, Editor3dPartControl::preCameraUpdate);
        controls.forEach(tpf, Editor3dPartControl::cameraUpdate);
        controls.forEach(tpf, Editor3dPartControl::postCameraUpdate);
    }
}
