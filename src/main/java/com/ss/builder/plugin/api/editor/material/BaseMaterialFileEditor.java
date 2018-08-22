package com.ss.builder.plugin.api.editor.material;

import static com.jme3.renderer.queue.RenderQueue.Bucket.Inherit;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.plugin.api.editor.Advanced3dFileEditorWithSplitRightTool;
import com.ss.builder.plugin.api.editor.material.BaseMaterialEditor3dPart.ModelType;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.editor.state.EditorState;
import com.ss.builder.fx.component.editor.state.impl.EditorMaterialEditorState;
import com.ss.builder.fx.component.tab.EditorToolComponent;
import com.ss.builder.fx.control.property.PropertyEditor;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The implementation of the Editor to edit materials.
 *
 * @author JavaSaBr
 */
public abstract class BaseMaterialFileEditor<T extends BaseMaterialEditor3dPart, S extends EditorMaterialEditorState, C extends ChangeConsumer> extends
        Advanced3dFileEditorWithSplitRightTool<T, S> {

    /**
     * The default state of editor light.
     */
    public static final boolean DEFAULT_LIGHT_ENABLED = true;

    /**
     * The list of available bucket types.
     */
    protected static final ObservableList<RenderQueue.Bucket> BUCKETS =
            FXCollections.observableArrayList(RenderQueue.Bucket.values());

    /**
     * The settings tree.
     */
    @NotNull
    protected final NodeTree<C> settingsTree;

    /**
     * The property editor.
     */
    @NotNull
    private final PropertyEditor<C> propertyEditor;

    /**
     * The button to use a cube.
     */
    @NotNull
    private final ToggleButton cubeButton;

    /**
     * The button to use a sphere.
     */
    @NotNull
    private final ToggleButton sphereButton;

    /**
     * The button to use a plane.
     */
    @NotNull
    private final ToggleButton planeButton;

    /**
     * The button to use a light.
     */
    @NotNull
    private final ToggleButton lightButton;

    /**
     * The list of RenderQueue.Bucket.
     */
    @NotNull
    private final ComboBox<RenderQueue.Bucket> bucketComboBox;

    protected BaseMaterialFileEditor() {
        super();
        this.bucketComboBox = new ComboBox<>(BUCKETS);
        this.cubeButton = new ToggleButton();
        this.sphereButton = new ToggleButton();
        this.planeButton = new ToggleButton();
        this.lightButton = new ToggleButton();
        this.settingsTree = new NodeTree<>(this::selectFromTree, getChangeConsumer(), SelectionMode.SINGLE);
        this.propertyEditor = new PropertyEditor<>(getChangeConsumer());
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
    protected boolean handleKeyActionInFx(
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    ) {

        if (isPressed && keyCode == KeyCode.C && !isControlDown && !isButtonMiddleDown) {
            cubeButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.S && !isControlDown && !isButtonMiddleDown) {
            sphereButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.P && !isControlDown && !isButtonMiddleDown) {
            planeButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.L && !isControlDown && !isButtonMiddleDown) {
            lightButton.setSelected(!lightButton.isSelected());
            return true;
        }

        return super.handleKeyActionInFx(keyCode, isPressed, isControlDown, isShiftDown, isButtonMiddleDown);
    }

    @Override
    @FxThread
    protected void createToolComponents(@NotNull EditorToolComponent container, @NotNull StackPane root) {
        super.createToolComponents(container, root);

        propertyEditor.prefHeightProperty()
                .bind(root.heightProperty());

        container.addComponent(buildSplitComponent(settingsTree, propertyEditor, root),
                getSettingsTreeToolName());

        FxUtils.addClass(settingsTree.getTreeView(), CssClasses.TRANSPARENT_TREE_VIEW);
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
     * Handle selected objects from tree.
     *
     * @param objects the selected objects.
     */
    @FxThread
    private void selectFromTree(@NotNull Array<Object> objects) {

        Object object = objects.first();

        Object parent = null;
        Object element;

        if (object instanceof TreeNode<?>) {
            var treeNode = (TreeNode) object;
            var  parentNode = treeNode.getParent();
            parent = parentNode == null ? null : parentNode.getElement();
            element = treeNode.getElement();
        } else {
            element = object;
        }

        propertyEditor.buildFor(element, parent);
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        switch (ModelType.valueOf(editorState.getModelType())) {
            case BOX:
                cubeButton.setSelected(true);
                break;
            case SPHERE:
                sphereButton.setSelected(true);
                break;
            case QUAD:
                planeButton.setSelected(true);
                break;
        }

        bucketComboBox.getSelectionModel().select(editorState.getBucketType());
        lightButton.setSelected(editorState.isLightEnable());
    }

    @Override
    @FxThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorMaterialEditorState::new;
    }

    @Override
    @FxThread
    protected void calcVSplitSize(@NotNull SplitPane splitPane) {
        splitPane.setDividerPosition(0, 0.2);
    }

    @Override
    @FxThread
    protected boolean needToolbar() {
        return true;
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull HBox container) {
        super.createToolbar(container);

        createActions(container);

        var bucketLabel = new Label(Messages.MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL + ":");

        bucketComboBox.getSelectionModel().select(Inherit);
        bucketComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeBucketType(newValue));

        FxUtils.addChild(container, bucketLabel, bucketComboBox);
    }

    /**
     * Create actions on toolbar.
     *
     * @param container the container.
     */
    @FxThread
    protected void createActions(@NotNull HBox container) {

        cubeButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_CUBE + " (C)"));
        cubeButton.setGraphic(new ImageView(Icons.CUBE_16));
        cubeButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.BOX, newValue));

        sphereButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_SPHERE + " (S)"));
        sphereButton.setGraphic(new ImageView(Icons.SPHERE_16));
        sphereButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.SPHERE, newValue));

        planeButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_PLANE + " (P)"));
        planeButton.setGraphic(new ImageView(Icons.PLANE_16));
        planeButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                changeModelType(ModelType.QUAD, newValue));

        lightButton.setTooltip(new Tooltip(Messages.MATERIAL_FILE_EDITOR_ACTION_LIGHT + " (L)"));
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(DEFAULT_LIGHT_ENABLED);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        FxUtils.addClass(cubeButton, sphereButton, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON)
                .addClass(planeButton, lightButton, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        FxUtils.addChild(container, createSaveAction(), cubeButton, sphereButton, planeButton, lightButton);

        DynamicIconSupport.addSupport(cubeButton, sphereButton, planeButton, lightButton);
    }

    /**
     * Handle changing the bucket type.
     */
    @FxThread
    private void changeBucketType(@NotNull RenderQueue.Bucket newValue) {

        editor3dPart.changeBucketType(newValue);

        var editorState = getEditorState();

        if (editorState != null) {
            editorState.setBucketType(newValue);
        }
    }

    /**
     * Handle changing the light enabling.
     */
    @FxThread
    private void changeLight(@NotNull Boolean newValue) {

        editor3dPart.updateLightEnabled(newValue);

        var editorState = getEditorState();

        if (editorState != null) {
            editorState.setLightEnable(newValue);
        }
    }

    /**
     * Handle the changed model type.
     */
    @FxThread
    private void changeModelType(@NotNull ModelType modelType, @NotNull Boolean newValue) {

        if (newValue == Boolean.FALSE) {
            return;
        }

        if (modelType == ModelType.BOX) {
            cubeButton.setMouseTransparent(true);
            sphereButton.setMouseTransparent(false);
            planeButton.setMouseTransparent(false);
            cubeButton.setSelected(true);
            sphereButton.setSelected(false);
            planeButton.setSelected(false);
            editor3dPart.changeModelType(modelType);
        } else if (modelType == ModelType.SPHERE) {
            cubeButton.setMouseTransparent(false);
            sphereButton.setMouseTransparent(true);
            planeButton.setMouseTransparent(false);
            cubeButton.setSelected(false);
            sphereButton.setSelected(true);
            planeButton.setSelected(false);
            editor3dPart.changeModelType(modelType);
        } else if (modelType == ModelType.QUAD) {
            cubeButton.setMouseTransparent(false);
            sphereButton.setMouseTransparent(false);
            planeButton.setMouseTransparent(true);
            sphereButton.setSelected(false);
            cubeButton.setSelected(false);
            planeButton.setSelected(true);
            editor3dPart.changeModelType(modelType);
        }

        var editorState = getEditorState();

        if (editorState != null) {
            editorState.setModelType(modelType);
        }
    }

    @Override
    @FxThread
    public void notifyFxChangeProperty(@NotNull Object object, @NotNull String propertyName) {
        if (object instanceof Material) {
            propertyEditor.refresh();
        } else {
            propertyEditor.syncFor(object);
        }
    }
}
