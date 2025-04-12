package cn.xbatis.db.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionGroups {

    /**
     * 多个分组
     *
     * @return
     */
    ConditionGroup[] value();
}
