package com.ss.editor.ui.control.filter.dialog;

import static java.util.Objects.requireNonNull;
import static rlib.util.dictionary.DictionaryFactory.newObjectDictionary;

import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.filter.operation.AddSceneFilterOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.filter.SceneFilter;
import com.ss.extension.scene.filter.impl.EditableCartoonEdgeFilter;
import com.ss.extension.scene.filter.impl.EditableColorOverlayFilter;
import com.ss.extension.scene.filter.impl.EditableDepthOfFieldFilter;
import com.ss.extension.scene.filter.impl.EditableFXAAFilter;
import com.ss.extension.scene.filter.impl.EditableFogFilter;
import com.ss.extension.scene.filter.impl.EditableGammaCorrectionFilter;
import com.ss.extension.scene.filter.impl.EditablePosterizationFilter;
import com.ss.extension.scene.filter.impl.EditableToneMapFilter;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.net.URLClassLoader;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.ClassUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The dialog to create a {@link SceneFilter}.
 *
 * @author JavaSaBr
 */
public class CreateSceneFilterDialog extends AbstractSimpleEditorDialog {

    private static final Point DIALOG_SIZE = new Point(415, 184);

    private static final Insets THE_FIRST_OFFSET = new Insets(10, 0, 0, 0);
    private static final Insets THE_SECOND_OFFSET = new Insets(0, 0, 10, 0);

    private static final ObjectDictionary<String, SceneFilter<?>> BUILT_IN = newObjectDictionary();
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);
    public static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    static {
        register(new EditableCartoonEdgeFilter());
        register(new EditableColorOverlayFilter());
        register(new EditableDepthOfFieldFilter());
        register(new EditableFogFilter());
        register(new EditableFXAAFilter());
        register(new EditableGammaCorrectionFilter());
        register(new EditablePosterizationFilter());
        register(new EditableToneMapFilter());
    }

    private static void register(@NotNull final SceneFilter<?> sceneFilter) {
        BUILT_IN.put(sceneFilter.getName(), sceneFilter);
        BUILT_IN_NAMES.add(sceneFilter.getName());
    }

    /**
     * The list of built in filters.
     */
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating filter.
     */
    private CheckBox customCheckBox;

    /**
     * The full class name of creating filter.
     */
    private TextField filterNameField;

    /**
     * The changes consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    public CreateSceneFilterDialog(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_SCENE_FILTER_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        customCheckBox = new CheckBox();
        customCheckBox.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);

        final Label builtInLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_BUILT_IN + ":");
        builtInLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        builtInBox = new ComboBox<>();
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());

        final HBox buildInContainer = new HBox(builtInLabel, builtInBox);

        final Label customBoxLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        final HBox customBoxContainer = new HBox(customBoxLabel, customCheckBox);

        final Label customNameLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        filterNameField = new TextField();
        filterNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        filterNameField.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);

        final HBox customNameContainer = new HBox(customNameLabel, filterNameField);

        FXUtils.addToPane(buildInContainer, root);
        FXUtils.addToPane(customBoxContainer, root);
        FXUtils.addToPane(customNameContainer, root);

        FXUtils.addClassTo(builtInLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(builtInBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customBoxLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customNameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(filterNameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(customBoxContainer, THE_FIRST_OFFSET);
        VBox.setMargin(customNameContainer, THE_SECOND_OFFSET);
    }

    @Override
    protected void processOk() {

        final SceneNode currentModel = changeConsumer.getCurrentModel();

        if (customCheckBox.isSelected()) {

            SceneFilter<?> newExample = null;
            try {
                newExample = ClassUtils.newInstance(filterNameField.getText());
            } catch (final RuntimeException e) {

                final Array<URLClassLoader> classLoaders = RESOURCE_MANAGER.getClassLoaders();

                for (final URLClassLoader classLoader : classLoaders) {
                    try {
                        final Class<?> targetClass = classLoader.loadClass(filterNameField.getText());
                        newExample = ClassUtils.newInstance(targetClass);
                    } catch (final ClassNotFoundException ex) {
                        LOGGER.warning(this, e);
                    }
                }
            }

            if (newExample == null) {
                throw new RuntimeException("Can't create a state of the class " + filterNameField.getText());
            }

            changeConsumer.execute(new AddSceneFilterOperation(newExample, currentModel));

        } else {

            final SingleSelectionModel<String> selectionModel = builtInBox.getSelectionModel();
            final String name = selectionModel.getSelectedItem();

            final SceneFilter<?> example = requireNonNull(BUILT_IN.get(name));
            final SceneFilter<?> newExample = ClassUtils.newInstance(example.getClass());

            changeConsumer.execute(new AddSceneFilterOperation(newExample, currentModel));
        }

        super.processOk();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
