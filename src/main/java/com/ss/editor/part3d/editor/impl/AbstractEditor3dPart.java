package com.ss.editor.part3d.editor.impl;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.part3d.editor.control.Editor3dPartControl;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of the {@link Editor3dPart} to use inside {@link FileEditor}.
 *
 * @param <T> the type of file editor
 * @author JavaSaBr
 */
public abstract class AbstractEditor3dPart<T extends FileEditor> extends AbstractAppState implements Editor3dPart {

    protected static final Logger LOGGER = LoggerManager.getLogger(Editor3dPart.class);

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

    public AbstractEditor3dPart(@NotNull T fileEditor) {
        this.fileEditor = fileEditor;
        this.stateNode = new Node(getClass().getSimpleName());
        this.controls = Array.ofType(Editor3dPartControl.class);
        initControls();
    }

    /**
     * Init all controls of this part.
     */
    @BackgroundThread
    protected void initControls() {
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
        return controls.anyMatchR(type, Class::isInstance);
    }
}
