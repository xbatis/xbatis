package cn.xbatis.core.sql;

import db.sql.api.impl.cmd.struct.ConditionChain;

public interface ObjectConditionLifeCycle {

    /**
     * 条件构建前执行
     */
    default void beforeBuildCondition() {
    }

    /**
     * 条件构建后执行
     *
     * @param conditionChain 条件链路器
     */
    default void afterBuildCondition(ConditionChain conditionChain) {
    }
}
