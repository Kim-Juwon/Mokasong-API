package com.mokasong.common.util;

import org.springframework.web.bind.annotation.RequestMapping;

public class ControllerLayerUtils {
    public static String getBaseURI(Class<?> clazz) throws Exception {
        RequestMapping requestMapping = clazz.getDeclaredAnnotation(RequestMapping.class);

        if (requestMapping == null) {
            return "";
        }

        String[] values = requestMapping.value();
        if (values.length == 0) {
            return "";
        }

        return values[0];
    }
}
