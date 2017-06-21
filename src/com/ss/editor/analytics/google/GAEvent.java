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
         * The constant LAUNCHED.
         */
        String LAUNCHED = "Launched";
        /**
         * The constant CLOSED.
         */
        String CLOSED = "Closed";
        /**
         * The constant EDITOR_OPENED.
         */
        String EDITOR_OPENED = "Opened";
        /**
         * The constant EDITOR_CLOSED.
         */
        String EDITOR_CLOSED = "Closed";
        /**
         * The constant DIALOG_OPENED.
         */
        String DIALOG_OPENED = "Opened";
        /**
         * The constant DIALOG_CLOSED.
         */
        String DIALOG_CLOSED = "Closed";
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
