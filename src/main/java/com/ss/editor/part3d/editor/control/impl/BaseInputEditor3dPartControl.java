package com.ss.editor.part3d.editor.control.impl;

import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.part3d.editor.control.InputEditor3dPartControl;
import com.ss.rlib.common.function.BooleanFloatConsumer;
import com.ss.rlib.common.function.FloatFloatConsumer;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * Base implementation of editor control which wirks with input system.
 *
 * @param <T> the editor 3d part's type.
 * @author JavaSaBr
 */
public abstract class BaseInputEditor3dPartControl<T extends Editor3dPart> extends AbstractEditor3dPartControl<T> implements
        InputEditor3dPartControl {

    /**
     * The table of action handlers.
     */
    @NotNull
    protected final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers;

    /**
     * The table of analog handlers.
     */
    @NotNull
    protected final ObjectDictionary<String, FloatFloatConsumer> analogHandlers;

    protected BaseInputEditor3dPartControl(@NotNull T editor3dPart) {
        super(editor3dPart);
        this.actionHandlers = ObjectDictionary.ofType(String.class, BooleanFloatConsumer.class);
        this.analogHandlers = ObjectDictionary.ofType(String.class, FloatFloatConsumer.class);
    }

    @Override
    @JmeThread
    public void onAction(@NotNull String name, boolean isPressed, float tpf) {

        var handler = actionHandlers.get(name);

        if (handler == null) {
            LOGGER.warning(this, "Unknown action " + name);
            return;
        }

        handler.accept(isPressed, tpf);
    }

    @Override
    @JmeThread
    public void onAnalog(@NotNull String name, float value, float tpf) {

        var handler = analogHandlers.get(name);

        if (handler == null) {
            LOGGER.warning(this, "Unknown analog " + name);
            return;
        }

        handler.accept(value, tpf);
    }
}
