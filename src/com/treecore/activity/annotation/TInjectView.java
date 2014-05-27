package com.treecore.activity.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TInjectView {
	public abstract int id();

	public abstract String click();

	public abstract String longClick();

	public abstract String focuschange();

	public abstract String key();

	public abstract String Touch();
}