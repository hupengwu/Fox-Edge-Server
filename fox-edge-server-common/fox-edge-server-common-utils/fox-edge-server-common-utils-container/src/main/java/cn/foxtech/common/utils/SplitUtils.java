package cn.foxtech.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SplitUtils {
    public static <T> List<List<T>> split(Collection<T> dataList, int pageSize) {
        List<List<T>> resultList = new ArrayList<>();

        List<T> page = new ArrayList<>();
        for (T data : dataList) {
            if (page.size() < pageSize) {
                page.add(data);
            } else {
                resultList.add(page);
                page = new ArrayList<>();
            }
        }

        if (!page.isEmpty()) {
            resultList.add(page);
        }

        return resultList;
    }
}
