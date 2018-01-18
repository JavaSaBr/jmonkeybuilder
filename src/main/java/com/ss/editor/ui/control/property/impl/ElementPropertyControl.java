package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit an elements from scene.
 *
 * @param <C> the type parameter
 * @param <D> the type parameter
 * @param <T> the type parameter
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

    public ElementPropertyControl(@NotNull final Class<T> type, @Nullable final T propertyValue,
                                  @NotNull final String propertyName, @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
        this.type = type;
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        elementLabel = new Label(NO_ELEMENT);
        elementLabel.prefWidthProperty().bind(container.widthProperty());

        final Button changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(event -> processAdd());

        final Button editButton = new Button();
        editButton.setGraphic(new ImageView(Icons.REMOVE_12));
        editButton.disableProperty().bind(elementLabel.textProperty().isEqualTo(NO_ELEMENT));
        editButton.setOnAction(event -> processRemove());

        FXUtils.addToPane(elementLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(editButton, container);

        FXUtils.addClassesTo(container, CssClasses.TEXT_INPUT_CONTAINER,
                CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassTo(elementLabel, CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);
        FXUtils.addClassesTo(changeButton, editButton, CssClasses.FLAT_BUTTON,
                CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(changeButton, editButton);
    }

    /**
     * Show dialog to choose an element.
     */
    @FxThread
    protected void processAdd() {
    }

    /**
     * Open this material in the material editor.
     */
    @FxThread
    protected void processRemove() {
        changed(null, getPropertyValue());
    }

    /**
     * Gets element label.
     *
     * @return the label with name of the material.
     */
    @FxThread
    protected @NotNull Label getElementLabel() {
        return notNull(elementLabel);
    }
}
