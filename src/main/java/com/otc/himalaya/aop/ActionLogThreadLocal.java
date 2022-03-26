package com.otc.himalaya.aop;

import com.otc.himalaya.entity.ActionLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionLogThreadLocal {

    private static final InheritableThreadLocal<ActionLog> pool = new InheritableThreadLocal<>();

    private ActionLogThreadLocal() {}

    public static void set(ActionLog log) {
        pool.set(log);
    }

    public static ActionLog get() {
        return pool.get();
    }

    public static void clear() {
        pool.remove();
    }

}
