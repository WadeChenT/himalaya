package com.otc.himalaya.utils;

import com.otc.himalaya.anno.NoLazyTrim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BeanLazyUtil {

    /**
     * 將source的值copy到target去，除了equal的值跟ignoreProperties
     *
     * @param source
     * @param target
     * @param ignoreProperties
     * @return
     */
    public static <S, T> T beanCopy(S source, T target, String... ignoreProperties) {

        log.trace("BeanLazyUtils.source:{}", JsonUtil.toJsonLog(source));
        BeanUtils.copyProperties(source, target, ignoreProperties);
        log.trace("BeanLazyUtils.target:{}", JsonUtil.toJsonLog(target));
        return target;
    }

    /**
     * 將source的值copy到target去，除了equal的值跟ignoreProperties
     * 之後再將target的每一個值trim掉空白
     *
     * @param source
     * @param target
     * @param ignoreProperties
     * @return
     */
    public static <S, T> T beanCopyAndTrim(S source, T target, String... ignoreProperties) {
        beanCopy(source, target, ignoreProperties);
        return trimStringValues(target);
    }

    public static <S, T> T beanCopyOnlyChangedAndNotIgnoredValue(S source,
                                                                 T target,
                                                                 String... ignoreProperties) {
        log.trace("BeanLazyUtils.source:{}", JsonUtil.toJsonLog(source));
        Set<String> toIgnoreSet = getIgnoredStrings(source, target, ignoreProperties);
        BeanUtils.copyProperties(source, target, toIgnoreSet.toArray(new String[0]));

        final BeanWrapper targetWrappedSource = new BeanWrapperImpl(target);
        toIgnoreSet.forEach(x -> {
            if (targetWrappedSource.isWritableProperty(x)) {
                targetWrappedSource.setPropertyValue(x, null);
            }
        });
        log.trace("BeanLazyUtils.target:{}", JsonUtil.toJsonLog(target));

        return target;
    }

    public static <T> T trimStringValues(T model) {
        for (Field field : model.getClass()
                                .getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(NoLazyTrim.class)) continue;

                field.setAccessible(true);
                Object value = field.get(model);
                if (Objects.nonNull(value) && value instanceof String) {
                    String trimmed = (String) value;
                    field.set(model, trimmed.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("BeanLazyUtils after trim:{}", JsonUtil.toJsonLog(model));

        return model;
    }

    public static Set<String> getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                     .map(FeatureDescriptor::getName)
                     .filter(propertyName -> Objects.isNull(wrappedSource.getPropertyValue(propertyName)))
                     .collect(Collectors.toSet());
    }

    public static Set<String> getEqualsPropertyNames(Object source, Object target) {
        final BeanWrapper sourceWrappedSource = new BeanWrapperImpl(source);
        final BeanWrapper targetWrappedSource = new BeanWrapperImpl(target);

        return Stream.of(targetWrappedSource.getPropertyDescriptors())
                     .map(FeatureDescriptor::getName)
                     .filter(propertyName ->
                                     sourceWrappedSource.isReadableProperty(propertyName)
                                     &&
                                     sourceWrappedSource.isWritableProperty(propertyName)
                                     &&
                                     targetWrappedSource.isReadableProperty(propertyName)
                                     &&
                                     targetWrappedSource.isWritableProperty(propertyName)
                                     &&
                                     Objects.equals(sourceWrappedSource.getPropertyValue(propertyName),
                                                    targetWrappedSource.getPropertyValue(propertyName))
                     )
                     .collect(Collectors.toSet());
    }

    private static <S, T> Set<String> getIgnoredStrings(S source, T target, String[] ignoreProperties) {
        Set<String> toIgnoreSet = getEqualsPropertyNames(source, target);
        Collections.addAll(toIgnoreSet, ignoreProperties);
        log.info("BeanLazyUtils to ignore properties:{}", toIgnoreSet);
        return toIgnoreSet;
    }
}
