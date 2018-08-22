package com.ss.builder.ui.component.asset.tree.context.menu.filler;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * The interface to implement a context menu filler.
 *
 * @author JavaSaBr
 */
@FunctionalInterface
public interface AssetTreeSingleContextMenuFiller {

    /**
     * Fill the context menu of the resource element.
     *
     * @param element      the resource element.
     * @param items        the container of items of a context menu.
     * @param actionTester the action tester.
     */
    @FxThread
    void fill(
            @NotNull ResourceElement element,
            @NotNull List<MenuItem> items,
            @NotNull Predicate<Class<?>> actionTester
    );
}