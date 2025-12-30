package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.core.db.reflect.ModelFieldInfo;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.db.annotations.TableField;
import db.sql.api.Cmd;
import db.sql.api.cmd.CmdConvert;
import db.sql.api.impl.cmd.basic.BasicValue;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.util.function.Supplier;

public final class CmdParamUtil {

    public static Cmd build(TableFieldInfo tableFieldInfo, Object value) {
        if (value == null) {
            return new BasicValue(null);
        }
        TableField tableField = tableFieldInfo.getTableFieldAnnotation();
        return build(tableFieldInfo.getFieldInfo().getTypeClass(), tableFieldInfo.getTypeHandler(), tableField.jdbcType(), value);
    }

    public static Cmd build(ModelFieldInfo modelFieldInfo, Object value) {
        if (value == null) {
            return new BasicValue(null);
        }
        if (!modelFieldInfo.getTableFieldInfo().getFieldInfo().getTypeClass().isAssignableFrom(modelFieldInfo.getFieldInfo().getTypeClass())) {
            return new BasicValue(value);
        }
        TableField tableField = modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation();
        return build(modelFieldInfo.getFieldInfo().getTypeClass(), modelFieldInfo.getTableFieldInfo().getTypeHandler(), tableField.jdbcType(), value);
    }

    public static Cmd build(Class targetType, TypeHandler typeHandler, JdbcType jdbcType, Object value) {
        if (value == null) {
            return new BasicValue(null);
        }
        if (value instanceof Supplier) {
            return build(targetType, typeHandler, jdbcType, ((Supplier) value).get());
        }
        if (!targetType.isAssignableFrom(value.getClass())) {
            return new BasicValue(value);
        }
        if (typeHandler == null && (jdbcType == null || jdbcType == JdbcType.UNDEFINED)) {
            return new BasicValue(value);
        }
        if (value instanceof Cmd) {
            return (Cmd) value;
        } else if (value instanceof CmdConvert) {
            return ((CmdConvert) value).convert();
        }
        return new BasicValue(MybatisParameter.create(value, typeHandler, jdbcType));
    }
}
