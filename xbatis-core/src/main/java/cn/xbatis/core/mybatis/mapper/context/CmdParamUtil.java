package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.db.annotations.TableField;
import db.sql.api.Cmd;
import db.sql.api.cmd.CmdConvert;
import db.sql.api.impl.cmd.basic.BasicValue;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

public final class CmdParamUtil {

    public static Cmd build(TableField tableField, Object value) {
        return build(tableField.typeHandler(), tableField.jdbcType(), value);
    }

    public static Cmd build(Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType, Object value) {
        if (value instanceof Cmd) {
            return (Cmd) value;
        } else if (value instanceof CmdConvert) {
            return ((CmdConvert) value).convert();
        }
        if (typeHandler == UnknownTypeHandler.class && jdbcType == JdbcType.UNDEFINED) {
            return new BasicValue(value);
        }
        return new BasicValue(new MybatisParameter(value, typeHandler, jdbcType));
    }
}
