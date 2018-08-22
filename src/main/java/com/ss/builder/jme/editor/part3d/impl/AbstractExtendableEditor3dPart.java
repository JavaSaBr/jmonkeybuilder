package com.ss.builder.jme.editor.part3d.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.Editor3dPart;
import com.ss.builder.jme.editor.part3d.ExtendableEditor3dPart;
import com.ss.builder.jme.editor.part3d.control.Editor3dPartControl;
import com.ss.builder.jme.editor.part3d.event.Editor3dPartEvent;
import com.ss.builder.fx.component.editor.FileEditor;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.control.Editor3dPartControl;
import com.ss.builder.jme.editor.part3d.Editor3dPart;
import com.ss.builder.jme.editor.part3d.ExtendableEditor3dPart;
import com.ss.builder.jme.editor.part3d.event.Editor3dPartEvent;
import com.ss.builder.fx.component.editor.FileEditor;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of the {@link Editor3dPart} to use inside {@link FileEditor}.
 *
 * @param <T> the type of file editor
 * @author JavaSaBr
 */
public abstract class AbstractExtendableEditor3dPart<T extends FileEditor> extends AbstractEditor3dPart
        implements ExtendableEditor3dPart {

    /**
     * The owner editor.
     */
    @NotNull
    protected final T fileEditor;

    /**
     * The root node.
     */
    @NotNull
    protected final Node stateNode;

    /**
     * The list of additional controls of this 3d part.
     */
    @NotNull
    protected final Array<Editor3dPartControl> controls;

    public AbstractExtendableEditor3dPart(@NotNull T fileEditor) {
        this.fileEditor = fileEditor;
        this.stateNode = new Node(getClass().getSimpleName());
        this.controls = Array.ofType(Editor3dPartControl.class);
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        controls.forEach(application, Editor3dPartControl::initialize);
    }

    @Override
    @JmeThread
    public void cleanup() {
        controls.forEach(requireApplication(), Editor3dPartControl::cleanup);
        super.cleanup();
    }

    /**
     * Get the current application.
     *
     * @return get the current application.
     */
    @JmeThread
    public @NotNull Application requireApplication() {
        return notNull(application);
    }

    /**
     * Add the new control.
     *
     * @param control the new control.
     */
    @JmeThread
    protected void addControl(@NotNull Editor3dPartControl control) {
        this.controls.add(control);
    }

    @Override
    @JmeThread
    public <C extends Editor3dPartControl> @Nullable C getControl(@NotNull Class<C> type) {
        return type.cast(controls.findAnyR(type, Class::isInstance));
    }

    @Override
    @JmeThread
    public boolean getBooleanProperty(@NotNull String propertyId) {
        var control = controls.findAny(propertyId, Editor3dPartControl::hasProperty);
        return control != null && control.getBooleanProperty(propertyId);
    }

    @Override
    @FromAnyThread
    public @NotNull T getFileEditor() {
        return fileEditor;
    }

    @Override
    @JmeThread
    public @NotNull Node getRootNode() {
        return stateNode;
    }

    @Override
    @JmeThread
    public void notify(@NotNull Editor3dPartEvent event) {
        controls.forEach(event, Editor3dPartControl::notify);
    }

    @Override
    @FromAnyThread
    public @NotNull Camera getCamera() {
        throw new UnsupportedOperationException();
    }
}
