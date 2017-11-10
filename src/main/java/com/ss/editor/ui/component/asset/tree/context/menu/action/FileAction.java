package com.ss.editor.ui.component.asset.tree.context.menu.action;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.array.Array;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of a file action.
 *
 * @author JavaSaBr
 */
public class FileAction extends MenuItem {

    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(FileAction.class);

    /**
     * The event manager.
     */
    @NotNull
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The action element.
     */
    @Nullable
    private final ResourceElement element;

    /**
     * The action elements.
     */
    @Nullable
    private final Array<ResourceElement> elements;

    public FileAction(@NotNull final ResourceElement element) {
        this(element, null);
    }

    public FileAction(@NotNull final Array<ResourceElement> elements) {
        this(null, elements);
    }

    public FileAction(@Nullable final ResourceElement element, @Nullable final Array<ResourceElement> elements) {
        this.element = element;
        this.elements = elements;
        setText(getName());
        setOnAction(this::execute);

        final Image icon = getIcon();
        if (icon != null) setGraphic(new ImageView(icon));
    }

    /**
     * Get the file element of this action.
     *
     * @return the file element.
     */
    @FXThread
    protected @NotNull ResourceElement getElement() {
        return notNull(element);
    }

    /**
     * Get the file elements of this action.
     *
     * @return the file elements.
     */
    @FXThread
    protected @NotNull Array<ResourceElement> getElements() {
        return notNull(elements);
    }

    /**
     * Get an icon of this action.
     *
     * @return the icon or null.
     */
    @FXThread
    protected @Nullable Image getIcon() {
        return null;
    }

    /**
     * Handle executing of this action.
     *
     * @param event the event.
     */
    @FXThread
    protected void execute(@Nullable final ActionEvent event) {
    }

    /**
     * Get the name of this action.
     *
     * @return the name.
     */
    @FXThread
    protected @NotNull String getName() {
        return "Unknown";
    }
}
