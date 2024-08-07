package db.sql.api.impl.cmd.dbFun;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.tookit.Objects;
import db.sql.api.impl.tookit.SqlConst;

public class SubStr extends BasicFunction<SubStr> {

    private final int start;

    private final Integer length;

    public SubStr(Cmd key,int start) {
        this(key,start,null);
    }

    public SubStr(Cmd key, int start,Integer length) {
        super(SqlConst.SUBSTR, key);
        this.start = start;
        this.length = length;
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(operator).append(SqlConst.BRACKET_LEFT);
        this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.DELIMITER).append(this.start);
        if(Objects.nonNull(length)){
            sqlBuilder.append(SqlConst.DELIMITER).append(this.length);
        }
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

}