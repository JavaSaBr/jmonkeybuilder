package com.ss.builder.ui.component.asset.tree.context.menu.action;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.component.asset.tree.resource.ResourceElement;
import com.ss.builder.ui.component.editor.EditorDescriptor;
import com.ss.builder.ui.event.FxEventManager;
import com.ss.builder.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.editor.EditorDescriptor;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to open a file by an editor.
 *
 * @author JavaSaBr
 */
class OpenFileByEditorAction extends FileAction {

    /**
     * The editor description.
     */
    @NotNull
    private final EditorDescriptor description;

    public OpenFileByEditorAction(@NotNull ResourceElement element, @NotNull EditorDescriptor description) {
        super(element);
        this.description = description;

        setText(description.getEditorName());

        var icon = description.getIcon();

        if (icon != null) {
            setGraphic(new ImageView(icon));
        }
    }

    @FxThread
    @Override
    protected @Nullable Image getIcon() {
        return Icons.EDIT_16;
    }

    @FxThread
    @Override
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        FxEventManager.getInstance()
                .notify(new RequestedOpenFileEvent(getFile(), description));
    }
}
