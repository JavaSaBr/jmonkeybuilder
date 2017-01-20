package com.ss.extension.scene.app.state.impl;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.util.clone.Cloner;
import com.ss.extension.scene.app.state.EditableSceneAppState;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * The base wrapper implementation of app states to editable it.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditableWrappedSceneAppState<T extends AppState> implements EditableSceneAppState {

    /**
     * The wrapped app state.
     */
    @NotNull
    protected T appState;

    public AbstractEditableWrappedSceneAppState() {
        this.appState = createNewStateInstance();
    }

    /**
     * @return the new instance of wrapped state.
     */
    @NotNull
    protected T createNewStateInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application app) {
        appState.initialize(stateManager, app);
    }

    @Override
    public boolean isInitialized() {
        return appState.isInitialized();
    }

    @Override
    public void setEnabled(final boolean active) {
        appState.setEnabled(active);
    }

    @Override
    public boolean isEnabled() {
        return appState.isEnabled();
    }

    @Override
    public void stateAttached(@NotNull final AppStateManager stateManager) {
        appState.stateAttached(stateManager);
    }

    @Override
    public void stateDetached(@NotNull final AppStateManager stateManager) {
        appState.stateDetached(stateManager);
    }

    @Override
    public void update(final float tpf) {
        appState.update(tpf);
    }

    @Override
    public void render(@NotNull final RenderManager renderManager) {
        appState.render(renderManager);
    }

    @Override
    public void postRender() {
        appState.postRender();
    }

    @Override
    public void cleanup() {
        appState.cleanup();
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        final T newAppState = createNewStateInstance();
        cloneFields(appState, newAppState, cloner);
        appState = newAppState;
    }

    @NotNull
    @Override
    public String getName() {
        return appState.getClass().getSimpleName();
    }

    /**
     * Clone app state parameters from an original to a new instance.
     *
     * @param originalAppState the original app state.
     * @param newAppState      the new app state.
     * @param cloner           the cloner.
     */
    protected void cloneFields(@NotNull final T originalAppState, @NotNull final T newAppState, @NotNull final Cloner cloner) {
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

    }
}
