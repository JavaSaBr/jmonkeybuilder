package com.ss.editor.ui.dialog;

import static com.ss.editor.util.EditorUtil.tryToCreateUserObject;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.dictionary.DictionaryFactory.newObjectDictionary;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.extension.scene.filter.impl.*;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.AddSceneFilterOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

/**
 * The dialog to create a {@link SceneFilter}.
 *
 * @author JavaSaBr
 */
public class CreateSceneFilterDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(415, 0);

    @NotNull
    private static final ObjectDictionary<String, SceneFilter> BUILT_IN = newObjectDictionary();

    @NotNull
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    static {
        register(new EditableDirectionLightFromSceneShadowFilter());
        register(new EditableHqDirectionalLightFromSceneShadowFilter());
        register(new EditablePointLightFromSceneShadowFilter());
        register(new EditableCartoonEdgeFilter());
        register(new EditableColorOverlayFilter());
        register(new EditableDepthOfFieldFilter());
        register(new EditableFogFilter());
        register(new EditableFXAAFilter());
        register(new EditablePosterizationFilter());
        register(new EditableToneMapFilter());
        register(new EditableRadialBlurFilter());
        register(new EditableSceneBloomFilter());
        register(new EditableObjectsBloomFilter());
        register(new EditableSceneAndObjectsBloomFilter());
        register(new EditableWaterFilter());
        register(new EditableWaterWithDirectionLightFilter());
        register(new EditableLocalWaterFilter());
        register(new EditableLocalWaterWithDirectionLightFilter());
    }

    private static void register(@NotNull final SceneFilter sceneFilter) {
        BUILT_IN.put(sceneFilter.getName(), sceneFilter);
        BUILT_IN_NAMES.add(sceneFilter.getName());
    }

    /**
     * The list of built in filters.
     */
    @Nullable
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating filter.
     */
    @Nullable
    private CheckBox customCheckBox;

    /**
     * The full class name of creating filter.
     */
    @Nullable
    private TextField filterNameField;

    /**
     * The changes consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    public CreateSceneFilterDialog(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.CREATE_SCENE_FILTER_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label customBoxLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        customCheckBox = new CheckBox();
        customCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label builtInLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_BUILT_IN + ":");
        builtInLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        builtInBox = new ComboBox<>();
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());
        builtInBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label customNameLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        filterNameField = new TextField();
        filterNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        filterNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        root.add(builtInLabel, 0, 0);
        root.add(builtInBox, 1, 0);
        root.add(customBoxLabel, 0, 1);
        root.add(customCheckBox, 1, 1);
        root.add(customNameLabel, 0, 2);
        root.add(filterNameField, 1, 2);

        FXUtils.addClassTo(builtInLabel, customBoxLabel, customNameLabel, CssClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(builtInBox, customCheckBox, filterNameField, CssClasses.DIALOG_FIELD);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * @return the check box to chose an option of creating filter.
     */
    @FxThread
    private @NotNull CheckBox getCustomCheckBox() {
        return notNull(customCheckBox);
    }

    /**
     * @return the full class name of creating filter.
     */
    @FxThread
    private @NotNull TextField getFilterNameField() {
        return notNull(filterNameField);
    }

    /**
     * @return the list of built in filters.
     */
    @FxThread
    private @NotNull ComboBox<String> getBuiltInBox() {
        return notNull(builtInBox);
    }

    @Override
    @FxThread
    protected void processOk() {

        final SceneNode currentModel = changeConsumer.getCurrentModel();
        final List<SceneAppState> appStates = currentModel.getAppStates();
        final List<SceneFilter> filters = currentModel.getFilters();

        final CheckBox customCheckBox = getCustomCheckBox();
        final TextField filterNameField = getFilterNameField();
        final ComboBox<String> builtInBox = getBuiltInBox();

        if (customCheckBox.isSelected()) {

            final SceneFilter newExample = tryToCreateUserObject(this, filterNameField.getText(), SceneFilter.class);

            if (newExample == null) {
                throw new RuntimeException("Can't create a state of the class " + filterNameField.getText());
            }

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddSceneFilterOperation(newExample, currentModel));

        } else {

            final SingleSelectionModel<String> selectionModel = builtInBox.getSelectionModel();
            final String name = selectionModel.getSelectedItem();

            final SceneFilter example = notNull(BUILT_IN.get(name));
            final SceneFilter newExample = ClassUtils.newInstance(example.getClass());

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddSceneFilterOperation(newExample, currentModel));
        }

        super.processOk();
    }

    @FxThread
    private void check(@NotNull final List<SceneAppState> appStates, @NotNull final List<SceneFilter> filters,
                       @NotNull final SceneFilter newExample) {

        if (!(newExample instanceof EditableSceneFilter)) return;

        final EditableSceneFilter editableSceneFilter = (EditableSceneFilter) newExample;

        String message = editableSceneFilter.checkStates(appStates);

        if (message != null) {
            throw new RuntimeException(message);
        }

        message = editableSceneFilter.checkFilters(filters);

        if (message != null) {
            throw new RuntimeException(message);
        }
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
