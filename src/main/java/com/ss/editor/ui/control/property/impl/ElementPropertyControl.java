package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit an elements from scene.
 *
 * @param <C> the change consumer's type.
 * @param <D> the edited object's type.
 * @param <T> the element's type.
 * @author JavaSaBr
 */
public class ElementPropertyControl<C extends ChangeConsumer, D, T> extends PropertyControl<C, D, T> {

    /**
     * The constant NO_ELEMENT.
     */
    @NotNull
    protected static final String NO_ELEMENT = Messages.ABSTRACT_ELEMENT_PROPERTY_CONTROL_NO_ELEMENT;

    /**
     * The type of an element.
     */
    @NotNull
    protected final Class<T> type;

    /**
     * The label with name of the element.
     */
    @Nullable
    private Label elementLabel;

    public ElementPropertyControl(
            @NotNull Class<T> type,
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.type = type;
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        elementLabel = new Label(NO_ELEMENT);
        elementLabel.prefWidthProperty()
            .bind(container.widthProperty());

        var changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(event -> addElement());

        var editButton = new Button();
        editButton.setGraphic(new ImageView(Icons.REMOVE_12));
        editButton.setOnAction(event -> removeElement());
        editButton.disableProperty()
            .bind(elementLabel.textProperty().isEqualTo(NO_ELEMENT));

        FxUtils.addChild(container, elementLabel, changeButton, editButton);
        FxUtils.addClass(container, CssClasses.TEXT_INPUT_CONTAINER, CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER)
            .addClass(elementLabel, CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL)
            .addClass(changeButton, CssClasses.FLAT_BUTTON, CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON)
            .addClass(editButton, CssClasses.FLAT_BUTTON, CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(changeButton, editButton);
    }

    /**
     * Show a dialog to choose an element.
     */
    @FxThread
    protected void addElement() {
    }

    /**
     * Remove the current element.
     */
    @FxThread
    protected void removeElement() {
        changed(null, getPropertyValue());
    }

    /**
     * Get the element label.
     *
     * @return the element label.
     */
    @FxThread
    protected @NotNull Label getElementLabel() {
        return notNull(elementLabel);
    }
}
