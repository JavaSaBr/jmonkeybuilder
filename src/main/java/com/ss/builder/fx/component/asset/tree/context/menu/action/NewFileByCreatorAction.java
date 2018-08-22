package com.ss.builder.fx.component.asset.tree.context.menu.action;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.fx.component.creator.FileCreatorDescriptor;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.RequestedCreateFileEvent;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.fx.component.creator.FileCreatorDescriptor;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.RequestedCreateFileEvent;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action for creating a new file.
 *
 * @author JavaSaBr
 */
class NewFileByCreatorAction extends FileAction {

    /**
     * The creator descriptor.
     */
    @NotNull
    private final FileCreatorDescriptor descriptor;

    public NewFileByCreatorAction(@NotNull ResourceElement element, @NotNull FileCreatorDescriptor descriptor) {
        super(element);

        this.descriptor = descriptor;

        var icon = descriptor.getIcon();

        setText(descriptor.getDescription());
        setGraphic(new ImageView(icon == null ? Icons.NEW_FILE_16 : icon));
    }

    @FxThread
    @Override
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        FxEventManager.getInstance()
                .notify(new RequestedCreateFileEvent(getElement().getFile(), descriptor));
    }
}
