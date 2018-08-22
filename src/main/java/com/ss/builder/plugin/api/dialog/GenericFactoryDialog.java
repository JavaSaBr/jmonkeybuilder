package com.ss.builder.plugin.api.dialog;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.plugin.api.property.control.PropertyEditorControl;
import com.ss.builder.plugin.api.property.control.PropertyEditorControlFactory;
import com.ss.builder.fx.dialog.AbstractSimpleEditorDialog;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.plugin.api.property.control.PropertyEditorControl;
import com.ss.builder.plugin.api.property.control.PropertyEditorControlFactory;
import com.ss.builder.fx.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The implementation of a dialog to create an object.
 *
 * @author JavaSaBr
 */
public class GenericFactoryDialog extends AbstractSimpleEditorDialog {

    private static final Point DIALOG_SIZE = new Point(400, -1);

    /**
     * The result vars of the dialog.
     */
    @NotNull
    private final VarTable vars;

    /**
     * The list of all definitions.
     */
    @NotNull
    private final Array<PropertyDefinition> definitions;

    /**
     * The handler to handle result properties.
     */
    @NotNull
    private final Consumer<VarTable> handler;

    /**
     * THe callback to call re-validating.
     */
    @NotNull
    private final Runnable validateCallback;

    /**
     * The validator of all properties.
     */
    @NotNull
    private Predicate<VarTable> validator;

    /**
     * The root content container.
     */
    @Nullable
    private VBox root;

    public GenericFactoryDialog(@NotNull Array<PropertyDefinition> definitions, @NotNull Consumer<VarTable> handler) {
        this(definitions, handler, varTable -> true);
    }

    public GenericFactoryDialog(
            @NotNull Array<PropertyDefinition> definitions,
            @NotNull Consumer<VarTable> handler,
            @NotNull Predicate<VarTable> validator
    ) {
        this.definitions = definitions;
        this.handler = handler;
        this.validator = validator;
        this.vars = VarTable.newInstance();
        this.validateCallback = this::validate;
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();
        createControls();
        validate();
    }

    /**
     * Set the title.
     *
     * @param title the new title.
     */
    @FxThread
    public void setTitle(@NotNull String title) {
        getDialog().setTitle(title);
    }

    /**
     * Set the text to the OK button.
     *
     * @param text the new text.
     */
    @FxThread
    public void setButtonOkText(@NotNull String text) {
        notNull(getOkButton()).setText(text);
    }

    /**
     * Set the text to the close button.
     *
     * @param text the new text.
     */
    @FxThread
    public void setButtonCloseText(@NotNull String text) {
        notNull(getCloseButton()).setText(text);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CREATE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {
        super.createContent(root);
        this.root = root;
    }

    /**
     * Get the root to place controls.
     *
     * @return the root.
     */
    @FxThread
    private @NotNull VBox getRoot() {
        return notNull(root);
    }

    /**
     * Create controls.
     */
    @FxThread
    private void createControls() {
        getDefinitions().forEach(getRoot(), (definition, root) -> {

            var control = PropertyEditorControlFactory.build(vars, definition, validateCallback);
            control.prefWidthProperty()
                    .bind(widthProperty());

            FxUtils.addChild(root, control);
        });
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }

    /**
     * Get the list of all definitions.
     * 
     * @return the list of all definitions.
     */
    @FxThread
    private @NotNull Array<PropertyDefinition> getDefinitions() {
        return definitions;
    }

    /**
     * Validate current values.
     */
    @FxThread
    protected void validate() {

        getRoot().getChildren().stream()
                .filter(PropertyEditorControl.class::isInstance)
                .map(PropertyEditorControl.class::cast)
                .forEach(PropertyEditorControl::checkDependency);

        notNull(getOkButton()).setDisable(!validator.test(vars));
    }

    @Override
    @FxThread
    protected void processOk() {
        super.processOk();
        handler.accept(vars);
    }
}
