package com.ss.editor.analytics.google;

/**
 * @author JavaSaBr
 */
public interface GAEvent {

    interface Category {
        String EDITOR = "Editor";
    }

    interface Action {
        String EDITOR_LAUNCHED = "Launched";
        String EDITOR_CLOSED = "Closed";
        String OPEN = "Open";
        String CLOSE = "Close";
    }

    interface Label {
        String THE_EDITOR_WAS_CLOSED = "The editor was closed";
        String THE_EDITOR_WAS_LAUNCHED = "The editor was launched";
        String EXPORT_DESTINATION_CHOOSER = "Export destination chooser";
    }
}
