package com.treecore.crash.collector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class ReflectionCollector {
	public static String collectConstants(Class<?> someClass) {
		StringBuilder result = new StringBuilder();

		Field[] fields = someClass.getFields();
		for (Field field : fields) {
			result.append(field.getName()).append("=");
			try {
				result.append(field.get(null).toString());
			} catch (IllegalArgumentException e) {
				result.append("N/A");
			} catch (IllegalAccessException e) {
				result.append("N/A");
			}
			result.append("\n");
		}

		return result.toString();
	}

	public static String collectStaticGettersResults(Class<?> someClass) {
		StringBuilder result = new StringBuilder();
		Method[] methods = someClass.getMethods();
		for (Method method : methods)
			if ((method.getParameterTypes().length == 0)
					&& ((method.getName().startsWith("get")) || (method
							.getName().startsWith("is")))
					&& (!method.getName().equals("getClass")))
				try {
					result.append(method.getName());
					result.append('=');
					result.append(method.invoke(null, null));
					result.append("\n");
				} catch (IllegalArgumentException localIllegalArgumentException) {
				} catch (IllegalAccessException localIllegalAccessException) {
				} catch (InvocationTargetException localInvocationTargetException) {
				}
		return result.toString();
	}
}