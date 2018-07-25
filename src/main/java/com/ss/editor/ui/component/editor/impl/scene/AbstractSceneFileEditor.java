package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3dPart.KEY_LOADED_MODEL;
import static com.ss.editor.util.MaterialUtils.saveIfNeedTextures;
import static com.ss.editor.util.MaterialUtils.updateMaterialIdNeed;
import static com.ss.editor.util.NodeUtils.findParent;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.ModelKey;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.transform.EditorTransformSupport.TransformType;
import com.ss.editor.control.transform.EditorTransformSupport.TransformationMode;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.ScenePresentable;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.editor.ModelEditingProvider;
import com.ss.editor.model.scene.EditorAudioNode;
import com.ss.editor.model.scene.EditorLightNode;
import com.ss.editor.model.scene.WrapperNode;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AddChildOperation;
import com.ss.editor.part3d.editor.impl.Stats3dPart;
import com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3dPart;
import com.ss.editor.plugin.api.RenderFilterRegistry;
import com.ss.editor.plugin.api.editor.Advanced3dFileEditorWithSplitRightTool;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.scripting.EditorScriptingComponent;
import com.ss.editor.ui.component.editor.state.impl.BaseEditorSceneEditorState;
import com.ss.editor.ui.component.painting.PaintingComponent;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.model.ModelPropertyEditor;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.control.tree.action.impl.multi.RemoveElementsAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.ControlUtils;
import com.ss.editor.util.*;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * The base implementation of a model file editor.
 *
 * @param <M>  the type of edited object.
 * @param <MA> the type of {@link AbstractSceneEditor3dPart}
 * @param <ES> the type of an editor state.
 * @author JavaSaBr
 */
public abstract class AbstractSceneFileEditor<M extends Spatial, MA extends AbstractSceneEditor3dPart, ES extends BaseEditorSceneEditorState> extends
        Advanced3dFileEditorWithSplitRightTool<MA, ES> implements ModelChangeConsumer, ModelEditingProvider {

    public interface SaveHandler {

        @BackgroundThread
        void handle(@NotNull Spatial spatial);
    }

    protected static final int OBJECTS_TOOL = 0;
    protected static final int PAINTING_TOOL = 1;
    protected static final int SCRIPTING_TOOL = 2;

    private static final Array<String> ACCEPTED_FILES = Array.of(
            FileExtensions.JME_MATERIAL,
            FileExtensions.JME_OBJECT
    );

    private static final ObservableList<TransformationMode> TRANSFORMATION_MODES =
            FXCollections.observableArrayList(TransformationMode.values());

    public static final String EP_PRE_SAVE_HANDLERS = "AbstractSceneFileEditor#preSaveHandlers";
    public static final String EP_POST_SAVE_HANDLERS = "AbstractSceneFileEditor#postSaveHandlers";

    private static final ExtensionPoint<SaveHandler> PRE_SAVE_HANDLERS =
            ExtensionPointManager.register(EP_PRE_SAVE_HANDLERS);

    private static final ExtensionPoint<SaveHandler> POST_SAVE_HANDLERS =
            ExtensionPointManager.register(EP_POST_SAVE_HANDLERS);

    /**
     * The stats 3D part.
     */
    @NotNull
    private final Stats3dPart stats3dPart;

    /**
     * The selection handler.
     */
    @NotNull
    private final Consumer<Array<Object>> selectionNodeHandler;

    /**
     * The list of transform modes.
     */
    @NotNull
    private final ComboBox<TransformationMode> transformModeComboBox;

    /**
     * The model tree.
     */
    @NotNull
    protected final ModelNodeTree modelNodeTree;

    /**
     * The model property editor.
     */
    @NotNull
    protected final ModelPropertyEditor modelPropertyEditor;

    /**
     * The container of painting components.
     */
    @NotNull
    private final PaintingComponentContainer paintingComponentContainer;

    /**
     * The scripting component.
     */
    @NotNull
    private final EditorScriptingComponent scriptingComponent;

    /**
     * The container of property editor in objects tool.
     */
    @NotNull
    private final VBox propertyEditorObjectsContainer;

    /**
     * The container of model node tree in objects tool.
     */
    @NotNull
    private final VBox modelNodeTreeObjectsContainer;

    /**
     * The container of model node tree in editing tool.
     */
    @NotNull
    private final VBox modelNodeTreeEditingContainer;

    /**
     * The stats container.
     */
    @NotNull
    private final VBox statsContainer;

    /**
     * The selection toggle.
     */
    @NotNull
    private final ToggleButton selectionButton;

    /**
     * The grid toggle.
     */
    @NotNull
    private final ToggleButton gridButton;

    /**
     * The statistics toggle.
     */
    @NotNull
    private final ToggleButton statisticsButton;

    /**
     * The move tool toggle.
     */
    @NotNull
    private final ToggleButton moveToolButton;

    /**
     * The rotation tool toggle.
     */
    @NotNull
    private final ToggleButton rotationToolButton;

    /**
     * The scaling tool toggle.
     */
    @NotNull
    private final ToggleButton scaleToolButton;

    /**
     * The opened model.
     */
    @Nullable
    private M currentModel;

    /**
     * The flag of ignoring camera moving.
     */
    private boolean ignoreCameraMove;

    public AbstractSceneFileEditor() {
        this.selectionNodeHandler = this::selectNodesFromTree;
        this.modelNodeTree = new ModelNodeTree(selectionNodeHandler, this);
        this.modelPropertyEditor = new ModelPropertyEditor(this);
        this.propertyEditorObjectsContainer = new VBox();
        this.modelNodeTreeEditingContainer = new VBox();
        this.modelNodeTreeObjectsContainer = new VBox();
        this.paintingComponentContainer = new PaintingComponentContainer(this, this);
        this.statsContainer = new VBox();
        this.stats3dPart = new Stats3dPart(statsContainer);
        this.scriptingComponent = new EditorScriptingComponent(this::refreshTree);
        this.selectionButton = new ToggleButton();
        this.gridButton = new ToggleButton();
        this.statisticsButton = new ToggleButton();
        this.moveToolButton = new ToggleButton();
        this.rotationToolButton = new ToggleButton();
        this.scaleToolButton = new ToggleButton();
        this.transformModeComboBox = new ComboBox<>(TRANSFORMATION_MODES);
        addEditor3dPart(stats3dPart);
        stats3dPart.setEnabled(true);
        processChangeTool(-1, OBJECTS_TOOL);
    }

    @Override
    @FxThread
    protected void processChangedFile(@NotNull FileChangedEvent event) {
        super.processChangedFile(event);

        var file = event.getFile();
        var extension = FileUtils.getExtension(file);

        if (extension.endsWith(FileExtensions.JME_MATERIAL)) {

            ExecutorManager.getInstance()
                    .addJmeTask(() -> updateMaterial(file));

        } else if (MaterialUtils.isShaderFile(file) || MaterialUtils.isTextureFile(file)) {

            ExecutorManager.getInstance()
                    .addJmeTask(() -> updateMaterials(file));
        }
    }

    /**
     * Updating a material from the file.
     */
    @FxThread
    private void updateMaterial(@NotNull Path file) {

        var assetFile = EditorUtil.requireAssetFile(file);
        var assetPath = EditorUtil.toAssetPath(assetFile);
        var currentModel = getCurrentModel();

        var geometries = NodeUtils.getGeometriesWithMaterial(currentModel, assetPath);

        if (geometries.isEmpty()) {
            return;
        }

        var material = EditorUtil.getAssetManager()
                .loadMaterial(assetPath);

        geometries.forEach(geometry -> geometry.setMaterial(material));

        RenderFilterRegistry.getInstance()
                .refreshFilters();
    }

    /**
     * Updating materials.
     */
    @FxThread
    private void updateMaterials(@NotNull Path file) {

        var currentModel = getCurrentModel();
        var needRefresh = new AtomicInteger();

        NodeUtils.visitGeometry(currentModel, geometry -> {

            var material = geometry.getMaterial();
            var newMaterial = updateMaterialIdNeed(file, material);

            if (newMaterial != null) {
                geometry.setMaterial(newMaterial);
                needRefresh.incrementAndGet();
            }
        });

        if (needRefresh.get() < 1) {
            return;
        }

        RenderFilterRegistry.getInstance()
                .refreshFilters();
    }

    /**
     * Handle a added model.
     *
     * @param model the model
     */
    @FxThread
    protected void handleAddedObject(@NotNull Spatial model) {

        NodeUtils.getAllLights(model).forEach(editor3dPart,
                (light, editor3dPart) -> editor3dPart.addLight(light));

        NodeUtils.getAllAudioNodes(model).forEach(editor3dPart,
                (audioNode, editor3dPart) -> editor3dPart.addAudioNode(audioNode));
    }

    /**
     * Handle a removed model.T
     *
     * @param model the model
     */
    @FxThread
    protected void handleRemovedObject(@NotNull Spatial model) {

        NodeUtils.getAllLights(model).forEach(editor3dPart,
                (light, editor3dPart) -> editor3dPart.removeLight(light));

        NodeUtils.getAllAudioNodes(model).forEach(editor3dPart,
                (audioNode, editor3dPart) -> editor3dPart.removeAudioNode(audioNode));
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        scriptingComponent.addVariable("root", getCurrentModel());
        scriptingComponent.addVariable("assetManager", EditorUtil.getAssetManager(), AssetManager.class);
        scriptingComponent.addImport(Spatial.class);
        scriptingComponent.addImport(Geometry.class);
        scriptingComponent.addImport(Control.class);
        scriptingComponent.addImport(Node.class);
        scriptingComponent.addImport(Light.class);
        scriptingComponent.addImport(DirectionalLight.class);
        scriptingComponent.addImport(PointLight.class);
        scriptingComponent.addImport(SpotLight.class);
        scriptingComponent.addImport(Material.class);
        scriptingComponent.addImport(Texture.class);
        scriptingComponent.addImport(AssetManager.class);
        scriptingComponent.setExampleCode("root.attachChild(\nnew Node(\"created from Groovy\"));");
        scriptingComponent.buildHeader();

        var editorState = notNull(getEditorState());

        gridButton.setSelected(editorState.isEnableGrid());
        statisticsButton.setSelected(editorState.isShowStatistics());
        selectionButton.setSelected(editorState.isEnableSelection());
        transformModeComboBox.getSelectionModel()
                .select(TransformationMode.valueOf(editorState.getTransformationMode()));

        var components = paintingComponentContainer.getComponents();
        components.forEach(editorState, PaintingComponent::loadState);

        var transformType = TransformType.valueOf(editorState.getTransformationType());

        switch (transformType) {
            case MOVE_TOOL: {
                moveToolButton.setSelected(true);
                break;
            }
            case ROTATE_TOOL: {
                rotationToolButton.setSelected(true);
                break;
            }
            case SCALE_TOOL: {
                scaleToolButton.setSelected(true);
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    @FxThread
    protected boolean handleKeyActionImpl(
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    ) {

        if (editor3dPart.isCameraMoving()) {
            return false;
        }

        if (isPressed && isControlDown && keyCode == KeyCode.Z) {
            undo();
            return true;
        } else if (isPressed && isControlDown && keyCode == KeyCode.Y) {
            redo();
            return true;
        } else if (isPressed && keyCode == KeyCode.G && !isControlDown && !isButtonMiddleDown) {
            moveToolButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.R && !isControlDown && !isButtonMiddleDown) {
            rotationToolButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.S && !isControlDown && !isButtonMiddleDown) {
            scaleToolButton.setSelected(true);
            return true;
        } else if (isPressed && keyCode == KeyCode.DELETE) {

            var removeAction = findTreeAction(RemoveElementsAction.class);

            if (removeAction == null) {
                return false;
            }

            removeAction.process();
            return true;

        } else if (isPressed && isControlDown && keyCode == KeyCode.C) {
            //TODO
        } else if (isPressed && isControlDown && keyCode == KeyCode.V) {
            //TODO
        }

        return super.handleKeyActionImpl(keyCode, isPressed, isControlDown, isShiftDown, isButtonMiddleDown);
    }

    /**
     * Find a tree action for the current selected items.
     *
     * @param type the action's type.
     * @param <T>  the action's type.
     * @return the found action or null.
     */
    @FxThread
    protected <T extends MenuItem> @Nullable T findTreeAction(@NotNull Class<T> type) {

        var selected = modelNodeTree.getSelected();

        if (selected == null || !selected.canRemove()) {
            return null;
        }

        var contextMenu = modelNodeTree.getContextMenu(null);

        return UiUtils.findMenuItem(contextMenu.getItems(), type);
    }

    /**
     * Return true if need to ignore moving camera.
     *
     * @return true if need to ignore moving camera.
     */
    @FxThread
    private boolean isIgnoreCameraMove() {
        return ignoreCameraMove;
    }

    /**
     * Set true if need to ignore moving camera.
     *
     * @param ignoreCameraMove true if need to ignore moving camera.
     */
    @FxThread
    private void setIgnoreCameraMove(boolean ignoreCameraMove) {
        this.ignoreCameraMove = ignoreCameraMove;
    }

    /**
     * Set the current opened model.
     *
     * @param currentModel the current opened model.
     */
    @FxThread
    protected void setCurrentModel(@NotNull M currentModel) {
        this.currentModel = currentModel;
    }

    @Override
    @FxThread
    public @NotNull M getCurrentModel() {
        return notNull(currentModel);
    }

    @Override
    @FxThread
    public void notifyFxChangeProperty(@Nullable Object parent, @NotNull Object object, @NotNull String propertyName) {

        if (object instanceof EditableProperty) {
            object = ((EditableProperty) object).getObject();
        }

        modelPropertyEditor.syncFor(object);
        modelNodeTree.notifyChanged(parent, object);

        if (object instanceof Geometry && Messages.MODEL_PROPERTY_MATERIAL.equals(propertyName)) {
            modelNodeTree.refresh(object);
        }

        paintingComponentContainer.notifyChangeProperty(object, propertyName);
    }

    @Override
    public void notifyJmePreChangeProperty(@NotNull Object object, @NotNull String propertyName) {
        editor3dPart.notifyPropertyPreChanged(object, propertyName);
    }

    @Override
    @FxThread
    public void notifyJmeChangedProperty(@NotNull Object object, @NotNull String propertyName) {
        editor3dPart.notifyPropertyChanged(object, propertyName);
    }

    @Override
    @FxThread
    public void notifyFxChangePropertyCount(@NotNull Object object) {
        modelPropertyEditor.rebuildFor(object, null);
    }

    @Override
    @FxThread
    public void notifyFxAddedChild(@NotNull Object parent, @NotNull Object added, int index, boolean needSelect) {

        modelNodeTree.notifyAdded(parent, added, index);

        if (added instanceof Light) {
            editor3dPart.addLight((Light) added);
        } else if (added instanceof AudioNode) {
            editor3dPart.addAudioNode((AudioNode) added);
        } else if (added instanceof Spatial) {
            handleAddedObject((Spatial) added);
        }

        if (needSelect) {
            var executorManager = ExecutorManager.getInstance();
            executorManager.addJmeTask(() -> executorManager.addFxTask(() -> modelNodeTree.selectSingle(added)));
        }
    }

    @Override
    @FxThread
    public void notifyFxRemovedChild(@NotNull Object parent, @NotNull Object removed) {

        modelNodeTree.notifyRemoved(parent, removed);

        if (removed instanceof Light) {
            editor3dPart.removeLight((Light) removed);
        } else if (removed instanceof AudioNode) {
            editor3dPart.removeAudioNode((AudioNode) removed);
        } else if (removed instanceof Spatial) {
            handleRemovedObject((Spatial) removed);
        }
    }

    @Override
    @FxThread
    public void notifyFxReplaced(
            @NotNull Object parent,
            @Nullable Object oldChild,
            @Nullable Object newChild,
            boolean needExpand,
            boolean needDeepExpand
    ) {

        var currentModel = getCurrentModel();

        if (currentModel == oldChild && newChild instanceof Spatial) {

            handleRemovedObject(currentModel);

            editor3dPart.openModel(ClassUtils.<M>unsafeCast(newChild));

            handleAddedObject((Spatial) newChild);

            setCurrentModel(ClassUtils.unsafeCast(newChild));

        } else {

            if (oldChild instanceof Spatial) {
                handleRemovedObject((Spatial) oldChild);
            }

            if (newChild instanceof Spatial) {
                handleAddedObject((Spatial) newChild);
            }
        }

        modelNodeTree.notifyReplace(parent, oldChild, newChild, needExpand, needDeepExpand);
    }

    @Override
    @FxThread
    public void notifyFxMoved(
            @NotNull Object prevParent,
            @NotNull Object newParent,
            @NotNull Object child,
            int index,
            boolean needSelect
    ) {

        modelNodeTree.notifyMoved(prevParent, newParent, child, index);

        if (needSelect) {
            var executorManager = ExecutorManager.getInstance();
            executorManager.addJmeTask(() -> executorManager.addFxTask(() -> modelNodeTree.selectSingle(child)));
        }
    }

    /**
     * Handle the selected object.
     *
     * @param object the object
     */
    @FxThread
    public void notifySelected(@Nullable Object object) {

        if (object instanceof EditorLightNode) {
            object = ((EditorLightNode) object).getLight();
        }

        if (object instanceof EditorAudioNode) {
            object = ((EditorAudioNode) object).getAudioNode();
        }

        setIgnoreCameraMove(true);
        try {

            modelNodeTree.selectSingle(object);

            var selectionModel = getEditorToolComponent().getSelectionModel();

            if (isNeedToOpenObjectsTool(selectionModel.getSelectedIndex())) {
                selectionModel.select(OBJECTS_TOOL);
            }

        } finally {
            setIgnoreCameraMove(false);
        }
    }

    /**
     * Return true if need to open objects tool.
     *
     * @param current the current opened tool.
     * @return true if need to open objects tool.
     */
    @FxThread
    protected boolean isNeedToOpenObjectsTool(int current) {
        return !(current == OBJECTS_TOOL || current == SCRIPTING_TOOL);
    }

    /**
     * Return true of the spatial can be selected.
     *
     * @param spatial the spatial.
     * @return true if the spatial can be selected.
     */
    @FxThread
    protected boolean canSelect(@NotNull Spatial spatial) {
        return true;
    }

    /**
     * Handle the selected object from the tree.
     *
     * @param object the selected object.
     */
    @FxThread
    public void selectNodeFromTree(@Nullable Object object) {
        selectNodesFromTree(object == null ? Array.empty() : Array.of(object));
    }

    /**
     * Handle the selected objects from the tree.
     *
     * @param objects the selected objects.
     */
    @FxThread
    public void selectNodesFromTree(@NotNull Array<?> objects) {

        editor3dPart.select(Array.empty());

        if (objects.size() > 1) {
            multiSelectNodesFromTree(objects, editor3dPart);
        } else if (objects.size() == 1) {
            singleSelectNodesFromTree(objects, editor3dPart);
            return;
        } else {
            editor3dPart.select(Array.empty());
        }

        modelPropertyEditor.buildFor(null, null);
        paintingComponentContainer.prepareFor(null);
    }

    /**
     * Handle multi select nodes from tree.
     *
     * @param objects the selected objects.
     * @param editor3dPart the 3D part of this editor.
     */
    @FxThread
    protected void multiSelectNodesFromTree(@NotNull Array<?> objects, @NotNull MA editor3dPart) {

        var toSelect = Array.ofType(Spatial.class);

        for (var object : objects) {
            getSpatialToShowSelection(editor3dPart, object)
                    .ifPresent(toSelect::add);
        }

        editor3dPart.select(toSelect);
    }

    /**
     * Try to get a spatial to show selection of the object.
     *
     * @param editor3dPart the editor 3D part.
     * @param object       the object.
     * @return the optional value of a spatial.
     */
    @JmeThread
    protected @NotNull Optional<Spatial> getSpatialToShowSelection(@NotNull MA editor3dPart, Object object) {

        Object element;

        if (object instanceof TreeNode<?>) {
            element = ((TreeNode) object).getElement();
        } else {
            element = object;
        }

        if (element instanceof SceneLayer) {
            element = null;
        }

        Spatial spatial = null;

        if (element instanceof AudioNode) {
            var audioNode = editor3dPart.getAudioNode((AudioNode) element);
            spatial = audioNode == null ? null : audioNode.getEditedNode();
        } else if (element instanceof Spatial) {
            spatial = (Spatial) element;
        } else if (element instanceof Light) {
            spatial = editor3dPart.getLightNode((Light) element);
        } else if (element instanceof ScenePresentable) {
            var presentableNode = editor3dPart.getPresentableNode((ScenePresentable) element);
            spatial = presentableNode == null ? null : presentableNode.getEditedNode();
        }

        if (spatial != null && !spatial.isVisible()) {
            spatial = null;
        }

        if (spatial != null && canSelect(spatial)) {
            return Optional.of(spatial);
        }

        return Optional.empty();
    }

    /**
     * Handle single select node from a tree.
     *
     * @param objects the selected objects.
     * @param editor3dPart the 3D part of this editor.
     */
    @FxThread
    protected void singleSelectNodesFromTree(@NotNull Array<?> objects, @NotNull MA editor3dPart) {

        Object parent = null;
        Object element;

        var first = objects.first();

        if (first instanceof TreeNode<?>) {
            var treeNode = (TreeNode) first;
            var parentNode = treeNode.getParent();
            element = treeNode.getElement();
            parent = parentNode == null ? null : parentNode.getElement();
        } else {
            element = first;
        }

        if (element instanceof SceneLayer) {
            element = null;
        }

        Spatial spatial = getSpatialToShowSelection(editor3dPart, first)
                .orElse(null);

        if (spatial != null && canSelect(spatial)) {

            editor3dPart.select(spatial);

            if (!isIgnoreCameraMove() && !isVisibleOnEditor(spatial)) {
                editor3dPart.cameraLookAt(spatial);
            }
        }

        modelPropertyEditor.buildFor(element, parent);
        paintingComponentContainer.prepareFor(element);
    }

    @FxThread
    private boolean isVisibleOnEditor(@NotNull Spatial spatial) {

        var camera = editor3dPart.getCamera();

        var position = spatial.getWorldTranslation();
        var coordinates = camera.getScreenCoordinates(position, new Vector3f());

        var invisible = coordinates.getZ() < 0F || coordinates.getZ() > 1F;
        invisible = invisible || !isInside(coordinates.getX(), camera.getHeight() - coordinates.getY(), Event.class);

        return !invisible;
    }

    @Override
    @BackgroundThread
    public void doSave(@NotNull Path toStore) throws Throwable {
        super.doSave(toStore);

        var currentModel = getCurrentModel();
        try {

            NodeUtils.visitGeometry(currentModel,
                    Geometry::getMaterial,
                    MaterialUtils::saveIfNeedTextures);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var preSaveHandlers = PRE_SAVE_HANDLERS.getExtensions();
        var postSaveHandlers = POST_SAVE_HANDLERS.getExtensions();

        preSaveHandlers.forEach(saveHandler -> saveHandler.handle(currentModel));
        try {

            var exporter = BinaryExporter.getInstance();

            try (var out = Files.newOutputStream(toStore)) {
                exporter.save(currentModel, out);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            postSaveHandlers.forEach(saveHandler -> saveHandler.handle(currentModel));
        }
    }

    @Override
    @FxThread
    protected boolean needToolbar() {
        return true;
    }

    /**
     * Switch transformation mode.
     */
    @FxThread
    private void changeTransformMode(@NotNull TransformationMode transformationMode) {

        editor3dPart.setTransformMode(transformationMode);

        var editorState = getEditorState();

        if (editorState != null) {
            editorState.setTransformationMode(transformationMode.ordinal());
        }
    }

    /**
     * Switch transformation type.
     */
    @FxThread
    private void updateTransformTool(@NotNull TransformType transformType, @NotNull Boolean newValue) {

        if (newValue != Boolean.TRUE) {
            if (editor3dPart.getTransformType() == transformType) {
                if (transformType == TransformType.MOVE_TOOL) {
                    moveToolButton.setSelected(true);
                } else if (transformType == TransformType.ROTATE_TOOL) {
                    rotationToolButton.setSelected(true);
                } else if (transformType == TransformType.SCALE_TOOL) {
                    scaleToolButton.setSelected(true);
                }
            }
            return;
        }

        var editorState = getEditorState();
        editor3dPart.setTransformType(transformType);

        if (transformType == TransformType.MOVE_TOOL) {
            rotationToolButton.setSelected(false);
            scaleToolButton.setSelected(false);
        } else if (transformType == TransformType.ROTATE_TOOL) {
            moveToolButton.setSelected(false);
            scaleToolButton.setSelected(false);
        } else if (transformType == TransformType.SCALE_TOOL) {
            rotationToolButton.setSelected(false);
            moveToolButton.setSelected(false);
        }

        if (editorState != null) {
            editorState.setTransformationType(transformType.ordinal());
        }
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull HBox container) {
        createActions(container);

        var transformModeLabel = new Label(Messages.MODEL_FILE_EDITOR_TRANSFORM_MODE + ":");

        transformModeComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeTransformMode(newValue));

        FxUtils.addChild(container, transformModeLabel, transformModeComboBox);
    }

    @FxThread
    protected void createActions(@NotNull HBox container) {

        FxUtils.addChild(container, createSaveAction());

        selectionButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SELECTION));
        selectionButton.setGraphic(new ImageView(Icons.CUBE_16));
        selectionButton.setSelected(true);

        gridButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_GRID));
        gridButton.setGraphic(new ImageView(Icons.PLANE_16));
        gridButton.setSelected(true);

        statisticsButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_STATISTICS));
        statisticsButton.setGraphic(new ImageView(Icons.STATISTICS_16));
        statisticsButton.setSelected(true);

        moveToolButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_MOVE_TOOL + " (G)"));
        moveToolButton.setGraphic(new ImageView(Icons.MOVE_16));
        moveToolButton.setSelected(true);

        rotationToolButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_ROTATION_TOOL + " (R)"));
        rotationToolButton.setGraphic(new ImageView(Icons.ROTATION_16));

        scaleToolButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_SCALE_TOOL + " (S)"));
        scaleToolButton.setGraphic(new ImageView(Icons.SCALE_16));

        FxControlUtils.onSelectedChange(scaleToolButton, this::changeSelectionVisible);
        FxControlUtils.onSelectedChange(gridButton, this::changeGridVisible);
        FxControlUtils.onSelectedChange(statisticsButton, this::changeStatisticsVisible);
        FxControlUtils.onSelectedChange(moveToolButton, selected -> updateTransformTool(TransformType.MOVE_TOOL, selected));
        FxControlUtils.onSelectedChange(rotationToolButton, selected -> updateTransformTool(TransformType.ROTATE_TOOL, selected));
        FxControlUtils.onSelectedChange(scaleToolButton, selected -> updateTransformTool(TransformType.SCALE_TOOL, selected));

        DynamicIconSupport.addSupport(selectionButton, gridButton, statisticsButton,
                moveToolButton, rotationToolButton, scaleToolButton);

        FxUtils.addClass(selectionButton, gridButton, statisticsButton, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON)
                .addClass(moveToolButton, rotationToolButton, scaleToolButton, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        FxUtils.addChild(container, selectionButton, gridButton, statisticsButton,
                moveToolButton, rotationToolButton, scaleToolButton);
    }

    @Override
    @FxThread
    protected void createContent(@NotNull StackPane root) {

        scriptingComponent.prefHeightProperty()
                .bind(root.heightProperty());

        super.createContent(root);

        statsContainer.setMouseTransparent(true);
        statsContainer.prefHeightProperty()
                .bind(editorAreaPane.heightProperty());

        modelNodeTree.prefHeightProperty()
                .bind(root.heightProperty());

        modelPropertyEditor.prefHeightProperty()
                .bind(root.heightProperty());

        FxUtils.addChild(editorAreaPane, statsContainer);

        FxUtils.addClass(statsContainer, CssClasses.SCENE_EDITOR_STATS_CONTAINER)
                .addClass(modelNodeTree.getTreeView(), CssClasses.TRANSPARENT_TREE_VIEW);
    }

    @Override
    @FxThread
    protected void createToolComponents(@NotNull EditorToolComponent container, @NotNull StackPane root) {
        super.createToolComponents(container, root);

        container.addComponent(buildSplitComponent(modelNodeTreeObjectsContainer, propertyEditorObjectsContainer, root),
                Messages.SCENE_FILE_EDITOR_TOOL_OBJECTS);
        container.addComponent(buildSplitComponent(modelNodeTreeEditingContainer, paintingComponentContainer, root),
                Messages.SCENE_FILE_EDITOR_TOOL_PAINTING);

        container.addComponent(scriptingComponent, Messages.SCENE_FILE_EDITOR_TOOL_SCRIPTING);
    }

    /**
     * Refresh tree.
     */
    @FxThread
    protected void refreshTree() {
       modelNodeTree.fill(notNull(getCurrentModel()));
    }

    /**
     * Process change tool.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    @FxThread
    protected void processChangeTool(@Nullable Number oldValue, @NotNull Number newValue) {

        var propertyEditorParent = (VBox) modelPropertyEditor.getParent();
        var modelNodeTreeParent = (VBox) modelNodeTree.getParent();

        if (propertyEditorParent != null) {
            FxUtils.removeChild(propertyEditorParent, modelPropertyEditor);
        }

        if (modelNodeTreeParent != null) {
            FxUtils.removeChild(modelNodeTreeParent, modelNodeTree);
        }

        var oldIndex = oldValue == null ? -1 : oldValue.intValue();
        var newIndex = newValue.intValue();

        if (newIndex == OBJECTS_TOOL) {
            FxUtils.addChild(propertyEditorObjectsContainer, modelPropertyEditor);
            FxUtils.addChild(modelNodeTreeObjectsContainer, modelNodeTree);
            selectNodesFromTree(modelNodeTree.getSelectedItems());
        } else if (newIndex == PAINTING_TOOL) {
            FXUtils.addToPane(modelNodeTree, modelNodeTreeEditingContainer);
            paintingComponentContainer.notifyShowed();
        }

        if (oldIndex == PAINTING_TOOL) {
            paintingComponentContainer.notifyHided();
        }

        editor3dPart.changePaintingMode(newIndex == PAINTING_TOOL);
    }

    @Override
    @FxThread
    protected void handleDragDroppedEvent(@NotNull DragEvent dragEvent) {

        UiUtils.handleDroppedFile(dragEvent, this, dragEvent, FileExtensions.JME_OBJECT,
                AbstractSceneFileEditor::addNewModel);

        UiUtils.handleDroppedFile(dragEvent, this, dragEvent, FileExtensions.JME_MATERIAL,
                AbstractSceneFileEditor::applyMaterial);
    }

    @Override
    @FxThread
    protected void handleDragOverEvent(@NotNull DragEvent dragEvent) {
        super.handleDragOverEvent(dragEvent);
        UiUtils.acceptIfHasFile(dragEvent, ACCEPTED_FILES);
    }

    /**
     * Apply a new material from an asset tree.
     *
     * @param dragEvent the drag event.
     * @param file      the file.
     */
    @FxThread
    private void applyMaterial(@NotNull DragEvent dragEvent, @NotNull Path file) {

        var assetFile = EditorUtil.requireAssetFile(file);
        var assetPath = EditorUtil.toAssetPath(assetFile);

        var materialKey = new MaterialKey(assetPath);
        var camera = editor3dPart.getCamera();

        var area = notNull(get3dArea());
        var areaPoint = area.sceneToLocal(dragEvent.getSceneX(), dragEvent.getSceneY());

        var executorManager = ExecutorManager.getInstance();
        executorManager.addJmeTask(() -> {

            var geometry = editor3dPart.getGeometryByScreenPos((float) areaPoint.getX(),
                    camera.getHeight() - (float) areaPoint.getY());

            if (geometry == null) {
                return;
            }

            var linkNode = findParent(geometry, AssetLinkNode.class::isInstance);

            if (linkNode != null) {
                return;
            }

            var assetManager = EditorUtil.getAssetManager();
            var material = assetManager.loadAsset(materialKey);

            var operation = new PropertyOperation<ChangeConsumer, Geometry, Material>(geometry,
                    Messages.MODEL_PROPERTY_MATERIAL, material, geometry.getMaterial());

            operation.setApplyHandler(Geometry::setMaterial);

            execute(operation);
        });
    }

    /**
     * Add a new model from an asset tree.
     *
     * @param dragEvent the drag event.
     * @param file      the file.
     */
    @FxThread
    private void addNewModel(@NotNull DragEvent dragEvent, @NotNull Path file) {

        var currentModel = getCurrentModel();
        if (!(currentModel instanceof Node)) {
            return;
        }

        var selected = modelNodeTree.getSelectedObject();

        final Node parent;

        if (selected instanceof Node &&
                modelNodeTree.getSelectedCount() == 1 &&
                findParent((Spatial) selected, AssetLinkNode.class::isInstance) == null) {
            parent = (Node) selected;
        } else {
            parent = (Node) currentModel;
        }

        var assetFile = EditorUtil.requireAssetFile(file);
        var assetPath = EditorUtil.toAssetPath(assetFile);

        var modelKey = new ModelKey(assetPath);
        var camera = editor3dPart.getCamera();

        var area = notNull(get3dArea());
        var areaPoint = area.sceneToLocal(dragEvent.getSceneX(), dragEvent.getSceneY());

        var executorManager = ExecutorManager.getInstance();
        executorManager.addJmeTask(() -> {

            var defaultLayer = EditorUtil.getDefaultLayer(this);
            var local = LocalObjects.get();

            var loadedModel = EditorUtil.getAssetManager()
                    .loadModel(modelKey);

            var assetLinkNode = new AssetLinkNode(modelKey);
            assetLinkNode.attachLinkedChild(loadedModel, modelKey);
            assetLinkNode.setUserData(KEY_LOADED_MODEL, true);

            if (defaultLayer != null) {
                SceneLayer.setLayer(defaultLayer, assetLinkNode);
            }

            var scenePoint = editor3dPart.getScenePosByScreenPos((float) areaPoint.getX(),
                    camera.getHeight() - (float) areaPoint.getY());
            var result = local.nextVector(scenePoint)
                    .subtractLocal(parent.getWorldTranslation());

            var isPhysics = NodeUtils.children(loadedModel)
                    .flatMap(ControlUtils::controls)
                    .anyMatch(PhysicsControl.class::isInstance);

            if (isPhysics) {

                NodeUtils.updateWorldBound(loadedModel);

                var worldBound = loadedModel.getWorldBound();

                float height = 0;

                if (worldBound instanceof BoundingBox) {
                    height = ((BoundingBox) worldBound).getYExtent();
                    height = Math.min(((BoundingBox) worldBound).getXExtent(), height);
                    height = Math.min(((BoundingBox) worldBound).getZExtent(), height);
                } else if (worldBound instanceof BoundingSphere) {
                    height = ((BoundingSphere) worldBound).getRadius();
                }

                height /= 2F;

                var localRotation = assetLinkNode.getLocalRotation();
                var up = GeomUtils.getUp(localRotation, local.nextVector());
                up.multLocal(height);

                result.addLocal(up);
            }


            assetLinkNode.setLocalTranslation(result);

            execute(new AddChildOperation(assetLinkNode, parent, false));
        });
    }


    /**
     * Handle changing select visibility.
     */
    @FxThread
    private void changeSelectionVisible(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        editor3dPart.updateShowSelection(newValue);

        var editorState = getEditorState();

        if (editorState != null) {
            editorState.setEnableSelection(newValue);
        }
    }

    /**
     * Handle changing grid visibility.
     */
    @FxThread
    private void changeGridVisible(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        editor3dPart.updateShowGrid(newValue);

        var editorState = getEditorState();

        if (editorState != null) {
            editorState.setEnableGrid(newValue);
        }
    }

    /**
     * Handle changing statistics visibility.
     */
    @FxThread
    private void changeStatisticsVisible(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        stats3dPart.setEnabled(newValue);

        var editorState = getEditorState();
        if (editorState != null) {
            editorState.setShowStatistics(newValue);
        }
    }

    /**
     * Notify about transformed the object.
     *
     * @param spatial the spatial
     */
    @FromAnyThread
    public void notifyTransformed(@NotNull Spatial spatial) {
        ExecutorManager.getInstance()
                .addFxTask(() -> notifyTransformedImpl(spatial));
    }

    /**
     * Notify about transformed the object.
     */
    @FxThread
    private void notifyTransformedImpl(@NotNull Spatial spatial) {

        Object toUpdate = spatial;

        if (spatial instanceof WrapperNode) {
            toUpdate = ((WrapperNode) spatial).getWrappedObject();
        }

        modelPropertyEditor.syncFor(toUpdate);
    }

    @Override
    @JmeThread
    public @NotNull Node getCursorNode() {
        return editor3dPart.getCursorNode();
    }

    @Override
    @JmeThread
    public  @NotNull Node getMarkersNode() {
        return editor3dPart.getMarkersNode();
    }
}

