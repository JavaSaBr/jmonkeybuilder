package com.ss.editor.ui.component.editor.area;

import com.ss.editor.manager.IconManager;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;

import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;

import static com.ss.editor.manager.IconManager.DEFAULT_FILE_ICON_SIZE;

/**
 * Компонент для реализации области редакторов.
 *
 * @author Ronn
 */
public class EditorAreaComponent extends TabPane implements ScreenComponent {

    public static final String COMPONENT_ID = "EditorAreaComponent";

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();
    private static final IconManager ICON_MANAGER = IconManager.getInstance();

    public EditorAreaComponent() {
        FX_EVENT_MANAGER.addEventHandler(RequestedOpenFileEvent.EVENT_TYPE, event -> processOpenFile((RequestedOpenFileEvent) event));
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

        editor.dirtyProperty().addListener((observable, oldValue, newValue) -> {
            tab.setText(newValue == Boolean.TRUE? "*" + editor.getFileName() : editor.getFileName());
        });

        final ObservableList<Tab> tabs = getTabs();
        tabs.add(tab);
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
