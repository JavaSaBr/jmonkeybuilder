package com.ss.editor.ui.control.model.property.generic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The interface for implementing editable property of generic object.
 *
 * @author JavaSabr
 */
public class EditablePropertyImpl<T, O> implements EditableProperty<T, O> {

    /**
     * The type of this property.
     */
    private final EditablePropertyType type;

    /**
     * The name of this property.
     */
    private final String name;

    /**
     * The getter of this property.
     */
    private final Function<O, T> getter;

    /**
     * The setter of this property.
     */
    private final BiConsumer<O, T> setter;

    /**
     * The edited object.
     */
    private final O object;

    public EditablePropertyImpl(@NotNull final EditablePropertyType type, @NotNull final String name,
                                @NotNull final O object, @NotNull final Function<O, T> getter,
                                @NotNull final BiConsumer<O, T> setter) {
        this.type = type;
        this.name = name;
        this.object = object;
        this.getter = getter;
        this.setter = setter;
    }

    @NotNull
    @Override
    public EditablePropertyType getType() {
        return type;
    }

    @NotNull
    @Override
    public O getObject() {
        return object;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    public T getValue() {
        return getter.apply(object);
    }

    @Override
    public void setValue(@Nullable final T value) {
        setter.accept(object, value);
    }

    @Override
    public String toString() {
        return "EditablePropertyImpl{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", getter=" + getter +
                ", setter=" + setter +
                ", object=" + object +
                '}';
    }
}
