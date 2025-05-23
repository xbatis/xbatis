package cn.xbatis.core.mybatis.mapper.mappers.utils;


import java.util.Iterator;
import java.util.Map;

public class DefaultValueContextUtil {

    /**
     * 移除非同级的数据
     */
    public static void removeNonSameLevelData(Map<String, Object> defaultValueContext) {
        if (defaultValueContext == null || defaultValueContext.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<String, Object>> it = defaultValueContext.entrySet().iterator();
        while (it.hasNext()) {
            //移除非同级的缓存 例如当前时间
            if (it.next().getKey().contains("{NOW}")) {
                it.remove();
            }
        }
    }

}
