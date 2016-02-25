package com.ss.editor.ui.util;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Duration;
import org.sample.client.ui.component.ScreenComponent;
import rlib.util.ClassUtils;
import rlib.util.array.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Набор утилитных методов для работы с UI.
 * 
 * @author Ronn
 */
public class UIUtils {


	public static final int TARGET_SCREEN_HEIGHT = 1080;
	public static final int TARGET_SCREEN_WIDTH = 1920;

	/**
	 * Поиск всех компонентов экрана.
	 */
	public static void fillComponents(final Array<ScreenComponent> container, final Node node) {

		if(node instanceof ScreenComponent) {
			container.add((ScreenComponent) node);
		}

		if(node instanceof Parent) {
			for(final Node children : ((Parent) node).getChildrenUnmodifiable()) {
				fillComponents(container, children);
			}
		}
	}

	public static final void addTo(TreeItem<? super Object> item, TreeItem<? super Object> parent) {
		final ObservableList<TreeItem<Object>> children = parent.getChildren();
		children.add(item);
	}

	private UIUtils() {
		throw new RuntimeException();
	}

	/**
	 * Override tooltip timeout.
	 */
	public static void overrideTooltipBehavior(int openDelayInMillis, int visibleDurationInMillis, int closeDelayInMillis) {

		try {

			Class<?> tooltipBehaviourClass = null;
			Class<?>[] declaredClasses = Tooltip.class.getDeclaredClasses();

			for(Class<?> declaredClass : declaredClasses) {
				if(declaredClass.getCanonicalName().equals("javafx.scene.control.Tooltip.TooltipBehavior")) {
					tooltipBehaviourClass = declaredClass;
					break;
				}
			}

			if(tooltipBehaviourClass == null) {
				return;
			}

			Constructor<?> constructor = tooltipBehaviourClass.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);

			if(constructor == null) {
				return;
			}

			constructor.setAccessible(true);

			Object tooltipBehaviour = ClassUtils.newInstance(constructor, new Duration(openDelayInMillis), new Duration(visibleDurationInMillis), new Duration(closeDelayInMillis), false);

			if(tooltipBehaviour == null) {
				return;
			}

			Field field = Tooltip.class.getDeclaredField("BEHAVIOR");

			if(field == null) {
				return;
			}

			field.setAccessible(true);

			// Cache the default behavior if needed.
			field.get(Tooltip.class);
			field.set(Tooltip.class, tooltipBehaviour);

		} catch(Exception e) {
			System.out.println("Aborted setup due to error:" + e.getMessage());
		}
	}

	public static TreeItem<Object> findItemForValue(TreeView<Object> treeView, Object object) {

		final TreeItem<Object> root = treeView.getRoot();
		final ObservableList<TreeItem<Object>> children = root.getChildren();

		if(!children.isEmpty()) {

			for(TreeItem<Object> treeItem : children) {

				final TreeItem<Object> result = findItemForValue(treeItem, object);

				if(result != null) {
					return result;
				}
			}
		}

		if(root.getValue() == object) {
			return root;
		}

		return null;
	}

	public static TreeItem<Object> findItemForValue(TreeItem<Object> root, Object object) {

		final ObservableList<TreeItem<Object>> children = root.getChildren();

		if(!children.isEmpty()) {

			for(TreeItem<Object> treeItem : children) {

				final TreeItem<Object> result = findItemForValue(treeItem, object);

				if(result != null) {
					return result;
				}
			}
		}

		if(root.getValue() == object) {
			return root;
		}

		return null;
	}
}
