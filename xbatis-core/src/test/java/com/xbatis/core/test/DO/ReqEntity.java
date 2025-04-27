package com.xbatis.core.test.DO;

import cn.xbatis.db.annotations.Table;
import lombok.Data;

@Data
@Table("t_sys_user")
public class ReqEntity extends BsEntity{

    private String name;
}
