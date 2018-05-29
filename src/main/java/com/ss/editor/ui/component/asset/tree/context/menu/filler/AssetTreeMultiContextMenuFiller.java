package com.ss.editor.ui.component.asset.tree.context.menu.filler;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.rlib.common.util.array.Array;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * The interface to implement a context menu filler for multiply elements.
 *
 * @author JavaSaBr
 */
@FunctionalInterface
public interface AssetTreeMultiContextMenuFiller {

    /**
     * Fill the context menu of the resource element.
     *
     * @param elements     the list of resource elements.
     * @param items        the container of items of a context menu.
     * @param actionTester the action tester.
     */
    @FxThread
    void fill(@NotNull final Array<ResourceElement> elements, @NotNull final List<MenuItem> items,
              @NotNull final Predicate<Class<?>> actionTester);
}