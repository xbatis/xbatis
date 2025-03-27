package cn.xbatis.db.annotations;

import cn.xbatis.db.Logic;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ConditionGroups.class)
public @interface ConditionGroup {

     /**
      * 逻辑符号
      * @return Logic
      */
     Logic logic() default Logic.AND;

     /**
      * 条件组中的字段名
      * @return 字段名
      */
     String[] value();
}
