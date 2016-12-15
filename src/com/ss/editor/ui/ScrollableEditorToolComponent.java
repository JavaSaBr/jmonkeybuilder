package com.ss.editor.ui;

import com.ss.editor.ui.component.EditorToolComponent;
import com.ss.editor.ui.css.CSSClasses;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The component for containing editor tool components.
 *
 * @author JavaSaBr.
 */
public class ScrollableEditorToolComponent extends EditorToolComponent {

    public ScrollableEditorToolComponent(final SplitPane pane, final int index) {
        super(pane, index);
    }

    @Override
    public void addComponent(final Region component, final String name) {

        final ScrollPane scrollPane = new ScrollPane(new VBox(component));
        component.prefWidthProperty().bind(scrollPane.widthProperty());

        final Tab tab = new Tab(name);
        tab.setContent(scrollPane);
        tab.setClosable(false);

        FXUtils.addClassTo(tab, CSSClasses.SPECIAL_FONT_14);

        getTabs().add(tab);

        FXUtils.bindFixedHeight(scrollPane, heightProperty());
    }
}
