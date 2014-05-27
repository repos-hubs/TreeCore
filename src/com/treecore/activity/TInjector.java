package com.treecore.activity;

import android.app.Activity;
import android.content.res.Resources;
import com.treecore.activity.annotation.TInject;
import com.treecore.activity.annotation.TInjectResource;
import com.treecore.activity.annotation.TInjectView;
import java.lang.reflect.Field;

public class TInjector {
	private static TInjector instance;

	public static TInjector getInstance() {
		if (instance == null) {
			instance = new TInjector();
		}
		return instance;
	}

	public void inJectAll(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if ((fields != null) && (fields.length > 0))
			for (Field field : fields)
				if (field.isAnnotationPresent(TInjectView.class))
					injectView(activity, field);
				else if (field.isAnnotationPresent(TInjectResource.class))
					injectResource(activity, field);
				else if (field.isAnnotationPresent(TInject.class))
					inject(activity, field);
	}

	private void inject(Activity activity, Field field) {
		try {
			field.setAccessible(true);
			field.set(activity, field.getType().newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void injectView(Activity activity, Field field) {
		if (field.isAnnotationPresent(TInjectView.class)) {
			TInjectView viewInject = (TInjectView) field
					.getAnnotation(TInjectView.class);
			int viewId = viewInject.id();
			try {
				field.setAccessible(true);
				field.set(activity, activity.findViewById(viewId));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void injectResource(Activity activity, Field field) {
		if (field.isAnnotationPresent(TInjectResource.class)) {
			TInjectResource resourceJect = 
					(TInjectResource) field.getAnnotation(TInjectResource.class);
			int resourceID = resourceJect.id();
			try {
				field.setAccessible(true);
				Resources resources = activity.getResources();
				String type = resources.getResourceTypeName(resourceID);
				if (type.equalsIgnoreCase("string"))
					field.set(activity, activity.getResources().getString(resourceID));
				else if (type.equalsIgnoreCase("drawable"))
					field.set(activity, activity.getResources().getDrawable(resourceID));
				else if (type.equalsIgnoreCase("layout"))
					field.set(activity, activity.getResources().getLayout(resourceID));
				else if (type.equalsIgnoreCase("array")) {
					if (field.getType().equals(Integer.class))
						field.set(activity, activity.getResources().getIntArray(resourceID));
					else if (field.getType().equals(java.lang.String.class))
						field.set(activity, activity.getResources().getStringArray(resourceID));
					else {
						field.set(activity, activity.getResources().getStringArray(resourceID));
					}
				} else if (type.equalsIgnoreCase("color")) {
					if (field.getType().equals(Integer.TYPE))
						field.set(activity, Integer.valueOf(activity.getResources().getColor(resourceID)));
					else
						field.set(activity, activity.getResources().getColorStateList(resourceID));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void inject(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if ((fields != null) && (fields.length > 0))
			for (Field field : fields)
				if (field.isAnnotationPresent(TInject.class))
					inject(activity, field);
	}

	public void injectView(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if ((fields != null) && (fields.length > 0))
			for (Field field : fields)
				if (field.isAnnotationPresent(TInjectView.class))
					injectView(activity, field);
	}

	public void injectResource(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if ((fields != null) && (fields.length > 0))
			for (Field field : fields)
				if (field.isAnnotationPresent(TInjectResource.class))
					injectResource(activity, field);
	}
}