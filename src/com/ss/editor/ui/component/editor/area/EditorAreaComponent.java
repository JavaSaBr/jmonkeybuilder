package com.ss.editor.ui.component.editor.area;

import com.jme3.app.state.AppStateManager;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.IconManager;
import com.ss.editor.state.editor.EditorState;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;

import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;

import static com.ss.editor.manager.IconManager.DEFAULT_FILE_ICON_SIZE;

/**
 * Компонент для реализации области редакторов.
 *
 * @author Ronn
 */
public class EditorAreaComponent extends TabPane implements ScreenComponent {

    public static final String COMPONENT_ID = "EditorAreaComponent";

    public static final String KEY_EDITOR = "editor";

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();
    private static final IconManager ICON_MANAGER = IconManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    public EditorAreaComponent() {

        final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            EXECUTOR_MANAGER.addEditorThreadTask(() -> processShowEditor(oldValue, newValue));
        });

        FX_EVENT_MANAGER.addEventHandler(RequestedOpenFileEvent.EVENT_TYPE, event -> processOpenFile((RequestedOpenFileEvent) event));
    }

    /**
     * Обработка смены отображаемого редактора.
     *
     * @param prevTab предыдущий редактор.
     * @param newTab новый редактор.
     */
    private void processShowEditor(final Tab prevTab, final Tab newTab) {

        final AppStateManager stateManager = EDITOR.getStateManager();

        if(prevTab != null) {

            final ObservableMap<Object, Object> properties = prevTab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);

            final Array<EditorState> states = fileEditor.getStates();
            states.forEach(stateManager::detach);
        }

        if(newTab == null) {
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
        final FileEditor editor = EDITOR_REGISTRY.createEditorFor(file);

        if(editor == null) {
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
            tab.setText(newValue == Boolean.TRUE? "*" + editor.getFileName() : editor.getFileName());
        });

        final ObservableList<Tab> tabs = getTabs();
        tabs.add(tab);

        final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        selectionModel.select(tab);
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
