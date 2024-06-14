package db.sql.api.impl.cmd.executor;

import db.sql.api.cmd.executor.IExecutor;
import db.sql.api.impl.cmd.CmdFactory;
import db.sql.api.impl.cmd.basic.Table;
import db.sql.api.impl.cmd.basic.TableField;

import java.util.function.BiConsumer;

public interface Executor<SELF extends Executor,
        CMD_FACTORY extends CmdFactory
        >
        extends IExecutor<SELF, Table, TableField> {

    CMD_FACTORY $();

    SELF dbExecutor(BiConsumer<SELF, DbSelector> consumer);

}
