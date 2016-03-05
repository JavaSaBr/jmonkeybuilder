package com.ss.editor.ui.component.editor.area;

import com.jme3.app.state.AppStateManager;
import com.ss.editor.Editor;
import com.ss.editor.file.converter.FileConverter;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.state.editor.EditorState;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedConvertFileEvent;
import com.ss.editor.ui.event.impl.RequestedCreateFileEvent;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;

import java.nio.file.Path;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;

/**
 * Компонент для реализации области редакторов.
 *
 * @author Ronn
 */
public class EditorAreaComponent extends TabPane implements ScreenComponent {

    public static final String COMPONENT_ID = "EditorAreaComponent";
    public static final String KEY_EDITOR = "editor";

    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();
    private static final FileCreatorRegistry CREATOR_REGISTRY = FileCreatorRegistry.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();
    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * Таблица открытых редакторов.
     */
    private final ObjectDictionary<Path, Tab> openedEditors;

    public EditorAreaComponent() {
        setId(CSSIds.EDITOR_AREA_COMPONENT);

        this.openedEditors = DictionaryFactory.newConcurrentObjectDictionary();

        final ObservableList<Tab> tabs = getTabs();
        tabs.addListener(this::processChangeTabs);

        final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            EXECUTOR_MANAGER.addEditorThreadTask(() -> processShowEditor(oldValue, newValue));
        });

        FX_EVENT_MANAGER.addEventHandler(RequestedOpenFileEvent.EVENT_TYPE, event -> processOpenFile((RequestedOpenFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedCreateFileEvent.EVENT_TYPE, event -> processCreateFile((RequestedCreateFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedConvertFileEvent.EVENT_TYPE, event -> processConvertFile((RequestedConvertFileEvent) event));
    }

    /**
     * @return таблица открытых редакторов.
     */
    private ObjectDictionary<Path, Tab> getOpenedEditors() {
        return openedEditors;
    }

    /**
     * Обработка запроса на конвертирование файла.
     */
    private void processConvertFile(final RequestedConvertFileEvent event) {

        final Path file = event.getFile();
        final FileConverterDescription description = event.getDescription();

        final FileConverter converter = FILE_CONVERTER_REGISTRY.newCreator(description, file);

        if (converter == null) {
            return;
        }

        converter.convert(file);
    }

    /**
     * Обработка запроса на создание нового файла.
     */
    private void processCreateFile(final RequestedCreateFileEvent event) {

        final Path file = event.getFile();
        final FileCreatorDescription description = event.getDescription();

        final FileCreator fileCreator = CREATOR_REGISTRY.newCreator(description, file);

        if (fileCreator == null) {
            return;
        }

        fileCreator.start(file);
    }

    /**
     * Обработка закрытия редакторов.
     */
    private void processChangeTabs(final ListChangeListener.Change<? extends Tab> change) {

        if (!change.next()) {
            return;
        }

        final List<? extends Tab> removed = change.getRemoved();

        if (removed == null || removed.isEmpty()) {
            return;
        }

        removed.forEach(tab -> {

            final ObservableMap<Object, Object> properties = tab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            final Path editFile = fileEditor.getEditFile();

            final ObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
            openedEditors.remove(editFile);

            fileEditor.notifyClosed();
        });
    }

    /**
     * Обработка смены отображаемого редактора.
     *
     * @param prevTab предыдущий редактор.
     * @param newTab  новый редактор.
     */
    private void processShowEditor(final Tab prevTab, final Tab newTab) {

        final AppStateManager stateManager = EDITOR.getStateManager();

        if (prevTab != null) {

            final ObservableMap<Object, Object> properties = prevTab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);

            final Array<EditorState> states = fileEditor.getStates();
            states.forEach(stateManager::detach);
        }

        if (newTab == null) {
            return;
        }

        final ObservableMap<Object, Object> properties = newTab.getProperties();
        final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);

        final Array<EditorState> states = fileEditor.getStates();
        states.forEach(stateManager::attach);
    }

    /**
     * Процесс открытия файла.
     */
    private void processOpenFile(final RequestedOpenFileEvent event) {

        final Path file = event.getFile();

        final ObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
        final Tab tab = openedEditors.get(file);

        if (tab != null) {
            final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
            selectionModel.select(tab);
            return;
        }

        final EditorDescription description = event.getDescription();
        final FileEditor editor = description == null ? EDITOR_REGISTRY.createEditorFor(file) : EDITOR_REGISTRY.createEditorFor(description, file);

        if (editor == null) {
            return;
        }

        editor.openFile(file);

        addEditor(editor);
    }

    /**
     * Добавление нового открытого редактора в область.
     */
    public void addEditor(final FileEditor editor) {

        final Path editFile = editor.getEditFile();

        final Tab tab = new Tab(editor.getFileName());
        tab.setGraphic(new ImageView(ICON_MANAGER.getIcon(editFile, DEFAULT_FILE_ICON_SIZE)));
        tab.setContent(editor.getPage());

        final ObservableMap<Object, Object> properties = tab.getProperties();
        properties.put(KEY_EDITOR, editor);

        editor.dirtyProperty().addListener((observable, oldValue, newValue) -> {
            tab.setText(newValue == Boolean.TRUE ? "*" + editor.getFileName() : editor.getFileName());
        });

        final ObservableList<Tab> tabs = getTabs();
        tabs.add(tab);

        final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        selectionModel.select(tab);

        final ObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
        openedEditors.put(editFile, tab);
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
