package com.ss.editor.config;

import com.ss.editor.Editor;
import com.ss.editor.document.DocumentConfig;
import com.ss.editor.util.EditorUtil;

import rlib.util.Util;
import rlib.util.VarTable;

/**
 * Класс для конфигурирование игры.
 *
 * @author Ronn
 */
public abstract class Config {

    public static final String CONFIG_RESOURCE_PATH = "/com/ss/editor/config/config.xml";

    public static final String TITLE = "jME3 SpaceShift Editor";
    public static final String VERSION = "v.0.2.0";

    /**
     * Путь к папке с программой.
     */
    public static final String PROJECT_PATH;

    /**
     * Отображать дебаг.
     */
    public static final boolean DEV_DEBUG;

    static {

        final VarTable vars = new DocumentConfig(EditorUtil.getInputStream(CONFIG_RESOURCE_PATH)).parse();

        DEV_DEBUG = vars.getBoolean("Dev.debug", false);

        PROJECT_PATH = Util.getRootFolderFromClass(Editor.class).toString();
    }
}
