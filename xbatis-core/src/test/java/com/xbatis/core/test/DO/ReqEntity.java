package com.xbatis.core.test.DO;

import cn.xbatis.db.annotations.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_user")
public class ReqEntity extends BsEntity {

    private String name;
}
