package cn.mybatis.mp.core.mybatis.mapper.context;

import cn.mybatis.mp.core.db.reflect.ModelFieldInfo;
import cn.mybatis.mp.core.db.reflect.TableFieldInfo;
import cn.mybatis.mp.core.util.IdValueConverter;
import cn.mybatis.mp.core.util.ModelInfoUtil;
import cn.mybatis.mp.core.util.StringPool;
import cn.mybatis.mp.core.util.TableInfoUtil;
import org.apache.ibatis.reflection.invoker.GetFieldInvoker;

import java.util.Objects;

public final class SetIdUtil {

    private static boolean isIdExists(Object obj, GetFieldInvoker getFieldInvoker) {
        //如果设置了id 则不在设置
        Object sourceId;
        try {
            sourceId = getFieldInvoker.invoke(obj, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(sourceId)) {
            if (sourceId instanceof String) {
                if (!StringPool.EMPTY.equals(((String) sourceId).trim())) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean setId(Object obj, TableFieldInfo idFieldInfo, Object id) {
        if (isIdExists(obj, idFieldInfo.getReadFieldInvoker())) {
            return false;
        }

        if (idFieldInfo.getField().getType() == String.class) {
            id = id instanceof String ? id : String.valueOf(id);
        }
        id = IdValueConverter.convert(id, idFieldInfo.getField().getType());
        TableInfoUtil.setValue(idFieldInfo, obj, id);
        return true;
    }

    public static boolean setId(Object obj, ModelFieldInfo idFieldInfo, Object id) {
        if (isIdExists(obj, idFieldInfo.getReadFieldInvoker())) {
            return false;
        }

        if (idFieldInfo.getField().getType() == String.class) {
            id = id instanceof String ? id : String.valueOf(id);
        }
        id = IdValueConverter.convert(id, idFieldInfo.getField().getType());
        //默认值回写
        ModelInfoUtil.setValue(idFieldInfo, obj, id);
        return true;
    }
}
