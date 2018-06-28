package com.ss.editor.ui.component.asset.tree.context.menu.action;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.Array;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The base implementation of a file action.
 *
 * @author JavaSaBr
 */
public class FileAction extends MenuItem {

    protected static final Logger LOGGER = LoggerManager.getLogger(FileAction.class);

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

    public FileAction(@NotNull ResourceElement element) {
        this(element, null);
    }

    public FileAction(@NotNull Array<ResourceElement> elements) {
        this(null, elements);
    }

    public FileAction(@Nullable ResourceElement element, @Nullable Array<ResourceElement> elements) {
        this.element = element;
        this.elements = elements;

        setText(getName());
        setOnAction(this::execute);

        Image icon = getIcon();
        if (icon != null) {
            setGraphic(new ImageView(icon));
        }
    }

    /**
     * Get the file element of this action.
     *
     * @return the file element.
     */
    @FxThread
    protected @NotNull ResourceElement getElement() {
        return notNull(element);
    }

    /**
     * Get a file of the current resource element.
     *
     * @return the file of the current resource element.
     */
    @FxThread
    protected @NotNull Path getFile() {
        return getElement().getFile();
    }

    /**
     * Get the file elements of this action.
     *
     * @return the file elements.
     */
    @FxThread
    protected @NotNull Array<ResourceElement> getElements() {
        return notNull(elements);
    }

    /**
     * Get an icon of this action.
     *
     * @return the icon or null.
     */
    @FxThread
    protected @Nullable Image getIcon() {
        return null;
    }

    /**
     * Handle executing of this action.
     *
     * @param event the event.
     */
    @FxThread
    protected void execute(@Nullable ActionEvent event) {
    }

    /**
     * Get the name of this action.
     *
     * @return the name.
     */
    @FxThread
    protected @NotNull String getName() {
        return "Unknown";
    }
}
