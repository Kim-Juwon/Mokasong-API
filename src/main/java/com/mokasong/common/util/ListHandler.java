package com.mokasong.common.util;

import java.util.List;

public class ListHandler {
    public static boolean notEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
