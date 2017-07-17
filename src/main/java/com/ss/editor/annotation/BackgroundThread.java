package com.ss.editor.annotation;

import java.lang.annotation.*;

/**
 * The annotation to mark a method that it can be executed in the background thread.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface BackgroundThread {
}
