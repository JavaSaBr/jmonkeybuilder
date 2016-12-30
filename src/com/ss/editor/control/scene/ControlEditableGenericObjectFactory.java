package com.ss.editor.control.scene;

import com.jme3.animation.AnimControl;
import com.ss.editor.control.scene.impl.AnimControlEditableGenericObject;
import com.ss.editor.ui.control.model.property.builder.impl.generic.EditableGenericObject;
import com.ss.editor.ui.control.model.property.builder.impl.generic.EditableGenericObjectFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The implementation of the {@link EditableGenericObjectFactory} for making an editable object of default jME
 * controls.
 *
 * @author JavaSaBr
 */
public class ControlEditableGenericObjectFactory implements EditableGenericObjectFactory {

    private static final ObjectDictionary<Class<?>, Function<Object, EditableGenericObject>> CONSTRUCTORS =
            DictionaryFactory.newObjectDictionary();

    static {
        CONSTRUCTORS.put(AnimControl.class, object -> new AnimControlEditableGenericObject((AnimControl) object));
    }

    private static final EditableGenericObjectFactory INSTANCE = new ControlEditableGenericObjectFactory();

    public static EditableGenericObjectFactory getInstance() {
        return INSTANCE;
    }

    @Nullable
    @Override
    public EditableGenericObject make(@NotNull final Object object) {
        final Function<Object, EditableGenericObject> constructor = CONSTRUCTORS.get(object.getClass());
        return constructor != null ? constructor.apply(object) : null;
    }
}
