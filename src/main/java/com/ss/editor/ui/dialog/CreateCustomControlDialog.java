package com.ss.editor.ui.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.dictionary.DictionaryFactory.newObjectDictionary;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.control.EditableControl;
import com.ss.editor.extension.scene.control.impl.EditableBillboardControl;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ClasspathManager.Scope;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AddControlOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.ClassUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The dialog to create a custom control.
 *
 * @author JavaSaBr
 */
public class CreateCustomControlDialog extends AbstractSimpleEditorDialog {

    private static class ClassCell extends ListCell<Class<? extends Control>> {

        @Override
        protected void updateItem(@Nullable final Class<? extends Control> item, final boolean empty) {
            super.updateItem(item, empty);

            if (item == null) {
                setText(StringUtils.EMPTY);
            } else {
                setText(item.getSimpleName());
            }
        }
    }

    @NotNull
    private static final Point DIALOG_SIZE = new Point(415, -1);

    @NotNull
    private static final ObjectDictionary<String, EditableControl> BUILT_IN = newObjectDictionary();

    @NotNull
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    @NotNull
    private static final ClasspathManager CLASSPATH_MANAGER = ClasspathManager.getInstance();

    static {
        register(new EditableBillboardControl());
    }

    public static void register(@NotNull final EditableControl editableControl) {
        BUILT_IN.put(editableControl.getName(), editableControl);
        BUILT_IN_NAMES.add(editableControl.getName());
    }

    /**
     * The list of built in controls.
     */
    @Nullable
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating control.
     */
    @Nullable
    private CheckBox customCheckBox;

    /**
     * The list of available custom controls.
     */
    @Nullable
    private ComboBox<Class<? extends Control>> customComboBox;

    /**
     * The changes consumer.
     */
    @NotNull
    private final ModelChangeConsumer changeConsumer;

    /**
     * The spatial.
     */
    @NotNull
    private final Spatial spatial;

    public CreateCustomControlDialog(@NotNull final ModelChangeConsumer changeConsumer,
                                     @NotNull final Spatial spatial) {
        this.changeConsumer = changeConsumer;
        this.spatial = spatial;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.CREATE_CUSTOM_CONTROL_DIALOG_TITLE;
    }

    /**
     * Get the check box to chose an option of creating control.
     *
     * @return the check box to chose an option of creating control.
     */
    @FxThread
    private @NotNull CheckBox getCustomCheckBox() {
        return notNull(customCheckBox);
    }

    /**
     * Get the list of built in controls.
     *
     * @return the list of built in controls.
     */
    @FxThread
    private @NotNull ComboBox<String> getBuiltInBox() {
        return notNull(builtInBox);
    }

    /**
     * Get the list of available custom controls.
     *
     * @return the list of available custom controls.
     */
    @FxThread
    private @NotNull ComboBox<Class<? extends Control>> getCustomComboBox() {
        return notNull(customComboBox);
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label customBoxLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        customCheckBox = new CheckBox();
        customCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label builtInLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_BUILT_IN + ":");
        builtInLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        builtInBox = new ComboBox<>();
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());
        builtInBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label customLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_FIELD + ":");
        customLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        customComboBox = new ComboBox<>();
        customComboBox.setButtonCell(new ClassCell());
        customComboBox.setCellFactory(param -> new ClassCell());
        customComboBox.disableProperty().bind(customCheckBox.selectedProperty().not());
        customComboBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final ObservableList<Class<? extends Control>> items = customComboBox.getItems();

        CLASSPATH_MANAGER.findImplements(Control.class, Scope.ALL)
                .stream().filter(ClassUtils::hasConstructor).distinct()
                .sorted((f, s) -> StringUtils.compareIgnoreCase(f.getSimpleName(), s.getSimpleName()))
                .forEach(items::add);

        final GridPane settingsContainer = new GridPane();
        root.add(builtInLabel, 0, 0);
        root.add(builtInBox, 1, 0);
        root.add(customBoxLabel, 0, 1);
        root.add(customCheckBox, 1, 1);
        root.add(customLabel, 0, 2);
        root.add(customComboBox, 1, 2);

        FXUtils.addToPane(settingsContainer, root);
        FXUtils.addClassTo(builtInLabel, customBoxLabel, customLabel, CssClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(builtInBox, customCheckBox, customComboBox, CssClasses.DIALOG_FIELD);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    @Override
    @FxThread
    protected void processOk() {

        final CheckBox customCheckBox = getCustomCheckBox();

        if (customCheckBox.isSelected()) {

            final ComboBox<Class<? extends Control>> customComboBox = getCustomComboBox();
            final Class<? extends Control> selectedClass = customComboBox.getSelectionModel().getSelectedItem();
            final Control newExample = ClassUtils.newInstance(selectedClass);

            changeConsumer.execute(new AddControlOperation(newExample, spatial));

        } else {

            final ComboBox<String> builtInBox = getBuiltInBox();
            final SingleSelectionModel<String> selectionModel = builtInBox.getSelectionModel();
            final String name = selectionModel.getSelectedItem();

            final EditableControl example = notNull(BUILT_IN.get(name));
            final EditableControl newExample = ClassUtils.newInstance(example.getClass());

            changeConsumer.execute(new AddControlOperation(newExample, spatial));
        }

        super.processOk();
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
