package cn.xbatis.core.sql;

public interface ObjectConditionLifeCycle {

    /**
     * 条件构建前执行
     */
    void beforeBuildCondition();
}
