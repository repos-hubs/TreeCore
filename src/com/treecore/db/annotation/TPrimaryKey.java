package com.treecore.db.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD,
		java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TPrimaryKey {
	public abstract String name();

	public abstract String defaultValue();

	public abstract boolean autoIncrement();
}