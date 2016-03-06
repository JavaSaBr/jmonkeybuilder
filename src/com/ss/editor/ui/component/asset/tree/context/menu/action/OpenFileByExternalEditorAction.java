package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.util.EditorUtil;

import javafx.scene.control.MenuItem;

/**
 * Реализация действия по открытию файла во внешнем редакторе.
 *
 * @author Ronn
 */
public class OpenFileByExternalEditorAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public OpenFileByExternalEditorAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_EXTERNAL_EDITOR);
        setOnAction(event -> processOpen());
    }

    /**
     * Процесс открытия файла.
     */
    private void processOpen() {
        EditorUtil.openFileInExternalEditor(element.getFile());
    }
}
