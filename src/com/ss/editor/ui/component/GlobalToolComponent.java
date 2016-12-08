package com.ss.editor.ui.component;

import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import rlib.ui.util.FXUtils;

/**
 * The component for containing global tool components.
 *
 * @author JavaSaBr.
 */
public class GlobalToolComponent extends TabPane implements ScreenComponent {

    public GlobalToolComponent() {
        setId(CSSIds.GLOBAL_TOOL_COMPONENT);
        setSide(Side.LEFT);
    }

    public void addComponent(final Region component, final String name) {

        final Tab tab = new Tab(name);
        tab.setContent(component);
        tab.setClosable(false);

        FXUtils.addClassTo(tab, CSSClasses.MAIN_FONT_12);

        getTabs().add(tab);

        FXUtils.bindFixedHeight(component, heightProperty());
    }
}
