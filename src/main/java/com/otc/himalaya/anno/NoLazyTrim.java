package com.otc.himalaya.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, LOCAL_VARIABLE, TYPE_PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface NoLazyTrim {
}
