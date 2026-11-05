package cn.xbatis.db;

import org.apache.ibatis.session.Configuration;

public class UnknownTypeHandler extends org.apache.ibatis.type.UnknownTypeHandler {
    public UnknownTypeHandler(Configuration configuration) {
        super(configuration);
    }
}
