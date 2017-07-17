package com.ss.editor.ui.component.tab;

import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * The component to contain editor tool components.
 *
 * @author JavaSaBr
 */
public class ScrollableEditorToolComponent extends EditorToolComponent {

    /**
     * Instantiates a new Scrollable editor tool component.
     *
     * @param pane  the pane
     * @param index the index
     */
    public ScrollableEditorToolComponent(@NotNull final SplitPane pane, final int index) {
        super(pane, index);
    }

    @Override
    public void addComponent(@NotNull final Region component, @NotNull final String name) {

        final ScrollPane scrollPane = new ScrollPane(new VBox(component));
        component.prefWidthProperty().bind(scrollPane.widthProperty());

        final Tab tab = new Tab(name);
        tab.setContent(scrollPane);
        tab.setClosable(false);

        getTabs().add(tab);

        FXUtils.bindFixedHeight(scrollPane, heightProperty());
    }
}
