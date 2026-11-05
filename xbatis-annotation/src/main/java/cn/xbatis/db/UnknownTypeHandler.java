package cn.xbatis.db;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.ObjectTypeHandler;

public class UnknownTypeHandler extends org.apache.ibatis.type.UnknownTypeHandler {

    public static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();

    public UnknownTypeHandler(Configuration configuration) {
        super(configuration);
    }

    public static final boolean isUnknownTypeHandler(Class clazz) {
        return org.apache.ibatis.type.UnknownTypeHandler.class.isAssignableFrom(clazz);
    }
}
