package com.ss.editor.ui.component.editor.impl.model;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.state.editor.impl.model.ModelEditorState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.control.model.property.ModelPropertyEditor;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.ModelTreeChangeListener;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static javafx.geometry.Pos.TOP_RIGHT;

/**
 * Реализация редактора моделей.
 *
 * @author Ronn
 */
public class ModelFileEditor extends AbstractFileEditor<StackPane> {

    public static final String NO_FAST_SKY = Messages.MODEL_FILE_EDITOR_NO_SKY;

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName(Messages.MODEL_FILE_EDITOR_NAME);
        DESCRIPTION.setConstructor(ModelFileEditor::new);
        DESCRIPTION.addExtension(FileExtensions.JME_OBJECT);
    }

    private static final Array<String> FAST_SKY_LIST = ArrayFactory.newArray(String.class);


    static {
        FAST_SKY_LIST.add(NO_FAST_SKY);
        FAST_SKY_LIST.add("graphics/textures/sky/path.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/studio.hdr");
    }

    /**
     * Слушатель изменений в структуре модели.
     */
    private ModelTreeChangeListener changeTreeListener;

    /**
     * Обработчик изменений свойств.
     */
    private Runnable changeHandler;

    /**
     * 3D часть редактора.
     */
    private final ModelEditorState editorState;

    /**
     * Текущая модель.
     */
    private Spatial currentModel;

    /**
     * Обработчик выделения.
     */
    private Consumer<Object> selectionHandler;

    /**
     * Дерево узлов модели.
     */
    private ModelNodeTree modelNodeTree;

    /**
     * Редактор свойств модели.
     */
    private ModelPropertyEditor modelPropertyEditor;

    /**
     * Список доступных быстрых окружений.
     */
    private ComboBox<String> fastSkyComboBox;

    /**
     * Кнопка активации света камеры.
     */
    private ToggleButton lightButton;

    /**
     * Кнопка включения отображения выделения.
     */
    private ToggleButton selectionButton;

    /**
     * Кнопка включения отображения сетки.
     */
    private ToggleButton gridButton;

    /**
     * Игнорировать ли слушателей.
     */
    private boolean ignoreListeners;

    public ModelFileEditor() {
        this.editorState = new ModelEditorState(this);
        addEditorState(editorState);
    }

    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    /**
     * @return 3D часть редактора.
     */
    private ModelEditorState getEditorState() {
        return editorState;
    }

    /**
     * @return список доступных быстрых окружений.
     */
    private ComboBox<String> getFastSkyComboBox() {
        return fastSkyComboBox;
    }

    /**
     * @return дерево узлов модели.
     */
    private ModelNodeTree getModelNodeTree() {
        return modelNodeTree;
    }

    /**
     * @return редактор свойств модели.
     */
    private ModelPropertyEditor getModelPropertyEditor() {
        return modelPropertyEditor;
    }

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final Path assetFile = EditorUtil.getAssetFile(file);
        final ModelKey modelKey = new ModelKey(EditorUtil.toClasspath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Spatial model = assetManager.loadAsset(modelKey);

        final ModelEditorState editorState = getEditorState();
        editorState.openModel(model);

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {

            final ComboBox<String> fastSkyComboBox = getFastSkyComboBox();
            fastSkyComboBox.getSelectionModel().select(FAST_SKY_LIST.first());

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.fill(model);

        } finally {
            setIgnoreListeners(false);
        }
    }

    /**
     * @return игнорировать ли слушателей.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners игнорировать ли слушателей.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @param currentModel текущая модель.
     */
    private void setCurrentModel(final Spatial currentModel) {
        this.currentModel = currentModel;
    }

    /**
     * @return текущая модель.
     */
    private Spatial getCurrentModel() {
        return currentModel;
    }

    @Override
    public void doSave() {

        final Path editFile = getEditFile();
        final Spatial currentModel = getCurrentModel();

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(editFile)) {
            exporter.save(currentModel, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setDirty(false);
        notifyFileChanged();
    }

    @Override
    protected void createContent(final StackPane root) {
        this.selectionHandler = this::processSelect;
        this.changeTreeListener = createTreeChangeListener();
        this.changeHandler = () -> setDirty(true);

        root.setAlignment(TOP_RIGHT);

        final Accordion accordion = new Accordion();

        final VBox parameterContainer = new VBox();
        parameterContainer.setId(CSSIds.MODEL_FILE_EDITOR_PARAMETER_CONTAINER);

        modelNodeTree = new ModelNodeTree(selectionHandler, changeTreeListener);
        modelPropertyEditor = new ModelPropertyEditor(changeHandler);

        final ObservableList<TitledPane> panes = accordion.getPanes();
        panes.add(modelNodeTree);
        panes.add(modelPropertyEditor);

        FXUtils.addToPane(accordion, parameterContainer);
        FXUtils.addToPane(parameterContainer, root);

        accordion.setExpandedPane(modelNodeTree);

        FXUtils.bindFixedHeight(accordion, parameterContainer.heightProperty());
    }

    /**
     * Обработка выделения узла в дереве.
     */
    public void notifySelected(final Object object) {

        Spatial spatial = null;

        if (object instanceof Spatial) {
            spatial = (Spatial) object;
        }

        final ModelNodeTree modelNodeTree = getModelNodeTree();
        modelNodeTree.select(object);
    }

    /**
     * Обработка выделения узла в дереве.
     */
    public void processSelect(final Object object) {

        Spatial spatial = null;

        if (object instanceof Spatial) {
            spatial = (Spatial) object;
        }

        final ModelEditorState editorState = getEditorState();
        editorState.updateSelection(spatial);

        final ModelPropertyEditor modelPropertyEditor = getModelPropertyEditor();
        modelPropertyEditor.buildFor(object);
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(final HBox container) {

        FXUtils.addToPane(createSaveAction(), container);

        lightButton = new ToggleButton();
        lightButton.setGraphic(new ImageView(Icons.LIGHT_24));
        lightButton.setSelected(true);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        selectionButton = new ToggleButton();
        selectionButton.setGraphic(new ImageView(Icons.CUBE_24));
        selectionButton.setSelected(true);
        selectionButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeSelectionVisible(newValue));

        gridButton = new ToggleButton();
        gridButton.setGraphic(new ImageView(Icons.PLANE_24));
        gridButton.setSelected(true);
        gridButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeGridVisible(newValue));

        final Label fastSkyLabel = new Label(Messages.MODEL_FILE_EDITOR_FAST_SKY + ":");

        fastSkyComboBox = new ComboBox<>();
        fastSkyComboBox.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_BOX);
        fastSkyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeFastSky(newValue));

        final ObservableList<String> skyItems = fastSkyComboBox.getItems();

        FAST_SKY_LIST.forEach(skyItems::add);

        FXUtils.addClassTo(lightButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(selectionButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(selectionButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(gridButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(gridButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(fastSkyLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(fastSkyComboBox, CSSClasses.MAIN_FONT_13);

        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(selectionButton, container);
        FXUtils.addToPane(gridButton, container);
        FXUtils.addToPane(fastSkyLabel, container);
        FXUtils.addToPane(fastSkyComboBox, container);

        HBox.setMargin(lightButton, new Insets(0, 0, 0, 4));
        HBox.setMargin(selectionButton, new Insets(0, 0, 0, 2));
        HBox.setMargin(gridButton, new Insets(0, 0, 0, 2));
        HBox.setMargin(fastSkyLabel, new Insets(0, 0, 0, 8));
    }

    /**
     * Обработка изменения видимости выделения.
     */
    private void changeSelectionVisible(final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final ModelEditorState editorState = getEditorState();
        editorState.updateShowSelection(newValue);
    }

    /**
     * Обработка изменения видимости сетки.
     */
    private void changeGridVisible(final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final ModelEditorState editorState = getEditorState();
        editorState.updateShowGrid(newValue);
    }

    /**
     * Обработка смены быстрого окружения.
     */
    private void changeFastSky(final String newSky) {

        if (isIgnoreListeners()) {
            return;
        }

        final ModelEditorState editorState = getEditorState();

        if (NO_FAST_SKY.equals(newSky)) {
            editorState.changeFastSky(null);
            return;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial newFastSky = SkyFactory.createSky(assetManager, newSky, SkyFactory.EnvMapType.EquirectMap);

        editorState.changeFastSky(newFastSky);
    }

    /**
     * Обновление активности света камеры.
     */
    private void changeLight(final Boolean newValue) {
        final ModelEditorState editorState = getEditorState();
        editorState.updateLightEnabled(newValue);
    }

    /**
     * @return создание слушателя изменений в дереве.
     */
    private ModelTreeChangeListener createTreeChangeListener() {
        return new ModelTreeChangeListener() {

            @Override
            public void notifyMoved(final Object prevParent, final Object newParent, final Object node) {
                setDirty(true);
            }

            @Override
            public void notifyChanged(final Object node) {
                setDirty(true);
            }

            @Override
            public void notifyAdded(final Object parent, final Object node) {
                setDirty(true);
            }

            @Override
            public void notifyRemoved(final Object node) {
                setDirty(true);
            }
        };
    }
}
