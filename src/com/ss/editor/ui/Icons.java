package com.ss.editor.ui;

import com.ss.editor.manager.IconManager;

import javafx.scene.image.Image;

/**
 * Интерфейс для перечисление иконок.
 *
 * @author Ronn
 */
public interface Icons {

    public static final IconManager ICON_MANAGER = IconManager.getInstance();

    public static final Image REFRESH_16 = ICON_MANAGER.getImage("/ui/icons/actions/18/refresh.png");

    public static final Image SAVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/save.png");
}
