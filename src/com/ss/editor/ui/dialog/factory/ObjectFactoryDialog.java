package com.ss.editor.ui.dialog.factory;

import static com.ss.editor.ui.dialog.factory.control.PropertyEditorControlFactory.build;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.dialog.factory.control.PropertyEditorControl;
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
public class ObjectFactoryDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(400, 0);

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

    public ObjectFactoryDialog(@NotNull final Array<PropertyDefinition> definitions,
                               @NotNull final Consumer<VarTable> handler) {
        this(definitions, handler, varTable -> true);
    }

    public ObjectFactoryDialog(@NotNull final Array<PropertyDefinition> definitions,
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
    public void setTitle(@NotNull final String title) {
        getDialog().setTitle(title);
    }

    /**
     * Sets the label to the OK button.
     *
     * @param label the new label.
     */
    public void setButtonOkLabel(@NotNull final String label) {
        getOkButton().setText(label);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);
        this.root = root;
    }

    /**
     * Gets the root to place controls.
     *
     * @return the root.
     */
    @NotNull
    private VBox getRoot() {
        return notNull(root);
    }

    /**
     * Create controls.
     */
    private void createControls() {

        final ObservableList<Node> children = getRoot().getChildren();

        final Array<PropertyDefinition> definitions = getDefinitions();
        definitions.forEach(definition -> {
            final PropertyEditorControl<?> control = build(vars, definition, validateCallback);
            control.prefWidthProperty().bind(widthProperty());
            children.add(control);
        });
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }

    /**
     * @return the list of all definitions.
     */
    @NotNull
    private Array<PropertyDefinition> getDefinitions() {
        return definitions;
    }

    /**
     * Validate current values.
     */
    protected void validate() {
        getOkButton().setDisable(!validator.test(vars));
    }

    @Override
    protected void processOk() {
        super.processOk();
        handler.accept(vars);
    }
}
