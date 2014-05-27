package com.treecore.activity.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.FIELD,
		java.lang.annotation.ElementType.PARAMETER,
		java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TInjectResource {
	public abstract int id();
}