package db.sql.api.impl.cmd.dbFun;

import db.sql.api.Cmd;
import db.sql.api.SqlBuilderContext;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.CmdUtils;

import java.io.Serializable;

public class Field extends BasicFunction<Field> {

    private final Cmd[] values;

    public Field(Cmd key, Serializable... values) {
        super(SqlConst.FILED, key);
        Cmd[] vs = new Cmd[values.length];
        int i = 0;
        for (Serializable value : values) {
            if (value == null) {
                continue;
            }
            vs[i++] = Methods.convert(value);
        }
        this.values = vs;
    }

    public Field(Cmd key, Cmd... values) {
        super(SqlConst.FILED, key);
        this.values = values;
    }

    public Field(Cmd key, Object... values) {
        super(SqlConst.FILED, key);
        Cmd[] vs = new Cmd[values.length];
        int i = 0;
        for (Object value : values) {
            if (value == null) {
                continue;
            }
            if (value instanceof Cmd) {
                vs[i++] = (Cmd) value;
            } else {
                vs[i++] = Methods.convert(value);
            }
        }
        this.values = vs;
    }

    @Override
    public StringBuilder functionSql(Cmd module, Cmd parent, SqlBuilderContext context, StringBuilder sqlBuilder) {
        sqlBuilder.append(this.operator).append(SqlConst.BRACKET_LEFT);
        this.key.sql(module, this, context, sqlBuilder);
        sqlBuilder.append(SqlConst.DELIMITER);
        CmdUtils.join(module, this, context, sqlBuilder, this.values, SqlConst.DELIMITER);
        sqlBuilder.append(SqlConst.BRACKET_RIGHT);
        return sqlBuilder;
    }

    @Override
    public boolean contain(Cmd cmd) {
        return CmdUtils.contain(cmd, this.key, this.values);
    }
}
