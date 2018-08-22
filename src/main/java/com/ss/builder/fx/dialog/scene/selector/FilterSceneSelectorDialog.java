package com.ss.builder.ui.dialog.scene.selector;

import com.jme3.post.Filter;
import com.ss.editor.extension.scene.SceneNode;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The implementation of {@link SceneSelectorDialog} to select a filter.
 *
 * @author JavaSaBr
 */
public class FilterSceneSelectorDialog extends SceneSelectorDialog<Filter> {

    public FilterSceneSelectorDialog(@NotNull final SceneNode sceneNode, @NotNull final Consumer<Filter> handler) {
        super(sceneNode, Filter.class, handler);
    }
}
