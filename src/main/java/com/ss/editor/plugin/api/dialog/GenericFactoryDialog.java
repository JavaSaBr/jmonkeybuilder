package com.ss.editor.plugin.api.dialog;

import static com.ss.editor.plugin.api.property.control.PropertyEditorControlFactory.build;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.plugin.api.property.control.PropertyEditorControl;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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

    @NotNull
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

    public GenericFactoryDialog(@NotNull final Array<PropertyDefinition> definitions,
                                @NotNull final Consumer<VarTable> handler) {
        this(definitions, handler, varTable -> true);
    }

    public GenericFactoryDialog(@NotNull final Array<PropertyDefinition> definitions,
                                @NotNull final Consumer<VarTable> handler,
                                @NotNull final Predicate<VarTable> validator) {
        this.definitions = definitions;
        this.handler = handler;
        this.validator = validator;
        this.vars = VarTable.newInstance();
        this.validateCallback = this::validate;
        createControls();
        validate();
    }

    /**
     * Sets the title.
     *
     * @param title the new title.
     */
    @FXThread
    public void setTitle(@NotNull final String title) {
        getDialog().setTitle(title);
    }

    /**
     * Sets the text to the OK button.
     *
     * @param text the new text.
     */
    @FXThread
    public void setButtonOkText(@NotNull final String text) {
        getOkButton().setText(text);
    }

    /**
     * Sets the text to the Close button.
     *
     * @param text the new text.
     */
    @FXThread
    public void setButtonCloseText(@NotNull final String text) {
        getCloseButton().setText(text);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CREATE;
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);
        this.root = root;
    }

    /**
     * Gets the root to place controls.
     *
     * @return the root.
     */
    @FXThread
    private @NotNull VBox getRoot() {
        return notNull(root);
    }

    /**
     * Create controls.
     */
    @FXThread
    private void createControls() {

        final ObservableList<Node> children = getRoot().getChildren();

        final Array<PropertyDefinition> definitions = getDefinitions();
        definitions.forEach(definition -> {
            final PropertyEditorControl<?> control = build(vars, definition, validateCallback);
            control.prefWidthProperty().bind(widthProperty());
            children.add(control);
        });
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }

    /**
     * @return the list of all definitions.
     */
    @FXThread
    private @NotNull Array<PropertyDefinition> getDefinitions() {
        return definitions;
    }

    /**
     * Validate current values.
     */
    @FXThread
    protected void validate() {
        getOkButton().setDisable(!validator.test(vars));
    }

    @Override
    @FXThread
    protected void processOk() {
        super.processOk();
        handler.accept(vars);
    }
}
