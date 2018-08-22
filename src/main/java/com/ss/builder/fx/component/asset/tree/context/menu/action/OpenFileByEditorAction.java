package com.ss.builder.fx.component.asset.tree.context.menu.action;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.editor.EditorDescriptor;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.RequestedOpenFileEvent;
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
