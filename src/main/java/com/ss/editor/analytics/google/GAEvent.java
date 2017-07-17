package com.ss.editor.analytics.google;

/**
 * The interface Ga event.
 *
 * @author JavaSaBr
 */
public interface GAEvent {

    /**
     * The interface Category.
     */
    interface Category {
        /**
         * The constant APPLICATION.
         */
        String APPLICATION = "Application";
        /**
         * The constant DIALOG.
         */
        String DIALOG = "Dialog";
        /**
         * The constant EDITOR.
         */
        String EDITOR = "Editor";
    }

    /**
     * The interface Action.
     */
    interface Action {
        /**
         * The constant APPLICATION_LAUNCHED.
         */
        String APPLICATION_LAUNCHED = "Application launched";
        /**
         * The constant APPLICATION_CLOSED.
         */
        String APPLICATION_CLOSED = "Application closed";
        /**
         * The constant EDITOR_OPENED.
         */
        String EDITOR_OPENED = "Editor opened";
        /**
         * The constant EDITOR_CLOSED.
         */
        String EDITOR_CLOSED = "Editor closed";
        /**
         * The constant DIALOG_OPENED.
         */
        String DIALOG_OPENED = "Dialog opened";
        /**
         * The constant DIALOG_CLOSED.
         */
        String DIALOG_CLOSED = "Dialog closed";
        /**
         * The constant EXECUTE_NODE_ACTION.
         */
        String EXECUTE_NODE_ACTION = "Execute node action";
    }

    /**
     * The interface Label.
     */
    interface Label {
        /**
         * The constant THE_EDITOR_APP_WAS_CLOSED.
         */
        String THE_EDITOR_APP_WAS_CLOSED = "The editor application was closed";
        /**
         * The constant THE_EDITOR_APP_WAS_LAUNCHED.
         */
        String THE_EDITOR_APP_WAS_LAUNCHED = "The editor application was launched";
        /**
         * The constant WORKING_ON_AN_EDITOR.
         */
        String WORKING_ON_AN_EDITOR = "Working on an editor";
        /**
         * The constant SHOWING_A_DIALOG.
         */
        String SHOWING_A_DIALOG = "Showing a dialog";
    }
}
