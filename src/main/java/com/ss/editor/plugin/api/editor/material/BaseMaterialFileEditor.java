package com.ss.editor.plugin.api.editor.material;

import static com.jme3.renderer.queue.RenderQueue.Bucket.Inherit;
import static com.jme3.renderer.queue.RenderQueue.Bucket.values;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.plugin.api.editor.Advanced3DFileEditorWithSplitRightTool;
import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3DPart.ModelType;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorMaterialEditorState;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.property.PropertyEditor;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The implementation of the Editor to edit materials.
 *
 * @author JavaSaBr
 */
public abstract class BaseMaterialFileEditor<T extends BaseMaterialEditor3DPart, S extends EditorMaterialEditorState, C extends ChangeConsumer> extends
        Advanced3DFileEditorWithSplitRightTool<T, S> {

    /**
     * The default state of editor light.
     */
    public static final boolean DEFAULT_LIGHT_ENABLED = true;

    /**
     * The list of available bucket types.
     */
    @NotNull
    protected static final ObservableList<RenderQueue.Bucket> BUCKETS = observableArrayList(values());

    /**
     * The settings tree.
     */
    @Nullable
    private NodeTree<C> settingsTree;

    /**
     * The property editor.
     */
    @Nullable
    private PropertyEditor<C> propertyEditor;

    /**
     * The button to use a cube.
     */
    @Nullable
    private ToggleButton cubeButton;

    /**
     * The button to use a sphere.
     */
    @Nullable
    private ToggleButton sphereButton;

    /**
     * The button to use a plane.
     */
    @Nullable
    private ToggleButton planeButton;

    /**
     * The button to use a light.
     */
    @Nullable
    private ToggleButton lightButton;

    /**
     * The list of RenderQueue.Bucket.
     */
    @Nullable
    private ComboBox<RenderQueue.Bucket> bucketComboBox;

    protected BaseMaterialFileEditor() {
        super();
    }

    /**
     * Get the change consumer.
     *
     * @return the change consumer.
     */
    @FromAnyThread
    protected C getChangeConsumer() {
        return unsafeCast(this);
    }

    @Override
    @FxThread
    protected boolean handleKeyActionImpl(@NotNull final KeyCode keyCode, final boolean isPressed,
                                          final boolean isControlDown, final boolean isShiftDown,
                                          final boolean isButtonMiddleDown) {

        if (isPressed && keyCode == KeyCode.C && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton cubeButton = getCubeButton();
            cubeButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.S && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton sphereButton = getSphereButton();
            sphereButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.P && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton planeButton = getPlaneButton();
            planeButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.L && !isControlDown && !isButtonMiddleDown) {
            final ToggleButton lightButton = getLightButton();
            lightButton.setSelected(!lightButton.isSelected());
            return true;
        }

        return super.handleKeyActionImpl(keyCode, isPressed, isControlDown, isShiftDown, isButtonMiddleDown);
    }

    @Override
    @FxThread
    protected void createToolComponents(@NotNull final EditorToolComponent container, @NotNull final StackPane root) {
        super.createToolComponents(container, root);

        settingsTree = new NodeTree<>(this::selectedFromTree, getChangeConsumer(), SelectionMode.SINGLE);
        propertyEditor = new PropertyEditor<>(getChangeConsumer());
        propertyEditor.prefHeightProperty().bind(root.heightProperty());

        container.addComponent(buildSplitComponent(settingsTree, propertyEditor, root), getSettingsTreeToolName());

        FXUtils.addClassTo(settingsTree.getTreeView(), CssClasses.TRANSPARENT_TREE_VIEW);
    }

    /**
     * Get the settings tree tool name.
     *
     * @return the settings tree tool name.
     */
    @FromAnyThread
    protected @NotNull String getSettingsTreeToolName() {
        return Messages.MATERIAL_SETTINGS_MAIN;
    }

    /**
     * @return the settings tree.
     */
    @FromAnyThread
    protected @NotNull NodeTree<C> getSettingsTree() {
        return notNull(settingsTree);
    }

    /**
     * @return the property editor.
     */
    @FromAnyThread
    protected @NotNull PropertyEditor<C> getPropertyEditor() {
        return notNull(propertyEditor);
    }

    /**
     * Handle selected objects from tree.
     *
     * @param objects the selected objects.
     */
    @FxThread
    private void selectedFromTree(@NotNull final Array<Object> objects) {

        final Object object = objects.first();

        Object parent = null;
        Object element;

        if (object instanceof TreeNode<?>) {
            final TreeNode treeNode = (TreeNode) object;
            final TreeNode parentNode = treeNode.getParent();
            parent = parentNode == null ? null : parentNode.getElement();
            element = treeNode.getElement();
        } else {
            element = object;
        }

        getPropertyEditor().buildFor(element, parent);
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        switch (ModelType.valueOf(editorState.getModelType())) {
            case BOX:
                getCubeButton().setSelected(true);
                break;
            case SPHERE:
                getSphereButton().setSelected(true);
                break;
            case QUAD:
                getPlaneButton().setSelected(true);
                break;
        }

        getBucketComboBox().getSelectionModel().select(editorState.getBucketType());
        getLightButton().setSelected(editorState.isLightEnable());
    }

    @Override
    @FxThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorMaterialEditorState::new;
    }

    @Override
    @FxThread
    protected void calcVSplitSize(@NotNull final SplitPane splitPane) {
        splitPane.setDividerPosition(0, 0.2);
    }

    @Override
    @FxThread
    protected boolean needToolbar() {
        return true;
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);
        createActions(container);

        final Label bucketLabel = new Label(Messages.MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL + ":");

        bucketComboBox = new ComboBox<>(BUCKETS);
        bucketComboBox.getSelectionModel().select(Inherit);
        bucketComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeBucketType(newValue));

        FXUtils.addToPane(bucketLabel, container);
        FXUtils.addToPane(bucketComboBox, container);
    }

    /**
     * Create actions on toolbar.
     *
     * @param container the container.
     */
    @FxThread
    protected void createActions(@NotNull final HBox container) {

        cubeButton = new ToggleButton();
        cubeButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_CUBE + " (C)"));
        cubeButton.setGraphic(new ImageView(Icons.CUBE_16));
        cubeButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.BOX, newValue));

        sphereButton = new ToggleButton();
        sphereButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_SPHERE + " (S)"));
        sphereButton.setGraphic(new ImageView(Icons.SPHERE_16));
        sphereButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.SPHERE, newValue));

        planeButton = new ToggleButton();
        planeButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_PLANE + " (P)"));
        planeButton.setGraphic(new ImageView(Icons.PLANE_16));
        planeButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.QUAD, newValue));

        lightButton = new ToggleButton();
        lightButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_LIGHT + " (L)"));
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(DEFAULT_LIGHT_ENABLED);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        FXUtils.addToPane(createSaveAction(), container);
        FXUtils.addToPane(cubeButton, container);
        FXUtils.addToPane(sphereButton, container);
        FXUtils.addToPane(planeButton, container);
        FXUtils.addToPane(lightButton, container);

        DynamicIconSupport.addSupport(cubeButton, sphereButton, planeButton, lightButton);
        FXUtils.addClassTo(cubeButton, sphereButton, planeButton, lightButton, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);
    }

    /**
     * Handle changing the bucket type.
     */
    @FxThread
    private void changeBucketType(@NotNull final RenderQueue.Bucket newValue) {

        final T editor3DState = getEditor3DPart();
        editor3DState.changeBucketType(newValue);

        final EditorMaterialEditorState editorState = getEditorState();
        if (editorState != null) editorState.setBucketType(newValue);
    }

    /**
     * Handle changing the light enabling.
     */
    @FxThread
    private void changeLight(@NotNull final Boolean newValue) {

        final T editor3DState = getEditor3DPart();
        editor3DState.updateLightEnabled(newValue);

        final EditorMaterialEditorState editorState = getEditorState();
        if (editorState != null) editorState.setLightEnable(newValue);
    }

    /**
     * @return the button to use a cube.
     */
    @FromAnyThread
    private @NotNull ToggleButton getCubeButton() {
        return notNull(cubeButton);
    }

    /**
     * @return the button to use a plane.
     */
    @FromAnyThread
    private @NotNull ToggleButton getPlaneButton() {
        return notNull(planeButton);
    }

    /**
     * @return the button to use a sphere.
     */
    @FromAnyThread
    private @NotNull ToggleButton getSphereButton() {
        return notNull(sphereButton);
    }

    /**
     * @return the button to use a light.
     */
    @FromAnyThread
    private @NotNull ToggleButton getLightButton() {
        return notNull(lightButton);
    }

    /**
     * @return the list of RenderQueue.Bucket.
     */
    @FromAnyThread
    private @NotNull ComboBox<RenderQueue.Bucket> getBucketComboBox() {
        return notNull(bucketComboBox);
    }

    /**
     * Handle the changed model type.
     */
    @FxThread
    private void changeModelType(@NotNull final ModelType modelType, @NotNull final Boolean newValue) {
        if (newValue == Boolean.FALSE) return;

        final T editor3DState = getEditor3DPart();

        final ToggleButton cubeButton = getCubeButton();
        final ToggleButton sphereButton = getSphereButton();
        final ToggleButton planeButton = getPlaneButton();

        if (modelType == ModelType.BOX) {
            cubeButton.setMouseTransparent(true);
            sphereButton.setMouseTransparent(false);
            planeButton.setMouseTransparent(false);
            cubeButton.setSelected(true);
            sphereButton.setSelected(false);
            planeButton.setSelected(false);
            editor3DState.changeMode(modelType);
        } else if (modelType == ModelType.SPHERE) {
            cubeButton.setMouseTransparent(false);
            sphereButton.setMouseTransparent(true);
            planeButton.setMouseTransparent(false);
            cubeButton.setSelected(false);
            sphereButton.setSelected(true);
            planeButton.setSelected(false);
            editor3DState.changeMode(modelType);
        } else if (modelType == ModelType.QUAD) {
            cubeButton.setMouseTransparent(false);
            sphereButton.setMouseTransparent(false);
            planeButton.setMouseTransparent(true);
            sphereButton.setSelected(false);
            cubeButton.setSelected(false);
            planeButton.setSelected(true);
            editor3DState.changeMode(modelType);
        }

        final EditorMaterialEditorState editorState = getEditorState();
        if (editorState != null) editorState.setModelType(modelType);
    }

    @Override
    @FxThread
    public void notifyFxChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {
        if (object instanceof Material) {
            getPropertyEditor().refresh();
        } else {
            getPropertyEditor().syncFor(object);
        }
    }
}
