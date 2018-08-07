package com.ss.editor.part3d.editor;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.state.AppState;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.control.Editor3dPartControl;
import com.ss.editor.ui.component.editor.FileEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement a 3d part of editor.
 *
 * @author JavaSaBr
 */
public interface Editor3dPart extends AppState {

    /**
     * Get the root node of this part.
     *
     * @return the root node of this part.
     */
    @FromAnyThread
    @NotNull Node getRootNode();

    /**
     * Get the file editor.
     *
     * @return the file editor.
     */
    @FromAnyThread
    @NotNull FileEditor getFileEditor();

    /**
     * Get a control by the type.
     *
     * @param type the control's type.
     * @param <C>  the control's type.
     * @return the control or null.
     */
    @JmeThread
    <C extends Editor3dPartControl> @Nullable C getControl(@NotNull Class<C> type);

    /**
     * Get a control by the type.
     *
     * @param type the control's type.
     * @param <C>  the control's type.
     * @return the control.
     */
    @JmeThread
    default <C extends Editor3dPartControl> @NotNull C requireControl(@NotNull Class<C> type) {
        return notNull(getControl(type));
    }
}
