/*
 * Copyright (c) ACCA Corp.
 * All Rights Reserved.
 */
package com.pps.core.common.util;


import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;

/**
 * @author Pu PanSheng, 2021/5/11
 * @version OPRA v1.0
 */
public class MetaObjectUtil {
    public static Method method;

    public MetaObjectUtil() {
    }

    public static MetaObject forObject(Object object) {
        try {
            return (MetaObject)method.invoke((Object)null, object);
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    static {
        try {
            Class<?> metaClass = Class.forName("org.apache.ibatis.reflection.SystemMetaObject");
            method = metaClass.getDeclaredMethod("forObject", Object.class);
        } catch (Exception var3) {
            try {
                Class<?> metaClass = Class.forName("org.apache.ibatis.reflection.MetaObject");
                method = metaClass.getDeclaredMethod("forObject", Object.class);
            } catch (Exception var2) {
                throw new RuntimeException(var2);
            }
        }

    }
}
