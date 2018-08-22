package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that Image System was initialized.
 *
 * @author JavaSaBr
 */
public class ImageSystemInitializedEvent extends SceneEvent {

    public static final EventType<ImageSystemInitializedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, ImageSystemInitializedEvent.class.getSimpleName());
        }
    }

    public ImageSystemInitializedEvent() {
        super(EVENT_TYPE);
    }
}
