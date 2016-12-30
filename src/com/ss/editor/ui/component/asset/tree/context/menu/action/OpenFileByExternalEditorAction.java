package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.util.EditorUtil;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * The action to open a file in an external editor.
 *
 * @author JavaSaBr
 */
public class OpenFileByExternalEditorAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The action element.
     */
    private final ResourceElement element;

    public OpenFileByExternalEditorAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_EXTERNAL_EDITOR);
        setOnAction(event -> processOpen());
        setGraphic(new ImageView(Icons.VIEW_16));
    }

    /**
     * Process of opening.
     */
    private void processOpen() {
        EditorUtil.openFileInExternalEditor(element.getFile());
    }
}
