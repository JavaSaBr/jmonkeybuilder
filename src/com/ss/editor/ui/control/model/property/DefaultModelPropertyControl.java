package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация свойства отображения свойства объекта.
 *
 * @author Ronn
 */
public class DefaultModelPropertyControl<T> extends ModelPropertyControl<Object, T> {

    /**
     * Надпись со значением свойства.
     */
    private Label propertyValueLabel;

    /**
     * Функция конвертации объекта в строку.
     */
    private Function<T, String> toStringFunction;

    public DefaultModelPropertyControl(final Consumer<EditorOperation> changeHandler, final T element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(changeHandler, element, paramName, modelChangeConsumer);
    }

    /**
     * @param toStringFunction функция конвертации объекта в строку.
     */
    public void setToStringFunction(final Function<T, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    /**
     * @return функция конвертации объекта в строку.
     */
    private Function<T, String> getToStringFunction() {
        return toStringFunction;
    }

    /**
     * @return надпись со значением свойства.
     */
    private Label getPropertyValueLabel() {
        return propertyValueLabel;
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        propertyValueLabel = new Label();
        propertyValueLabel.setAlignment(Pos.CENTER);

        FXUtils.addClassTo(propertyValueLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addToPane(propertyValueLabel, container);
        FXUtils.bindFixedWidth(propertyValueLabel, container.widthProperty());
    }

    @Override
    protected void reload() {
        super.reload();

        final Function<T, String> func = getToStringFunction();

        final Label propertyValueLabel = getPropertyValueLabel();
        propertyValueLabel.setText(func == null ? String.valueOf(getPropertyValue()) : func.apply(getPropertyValue()));
    }
}
