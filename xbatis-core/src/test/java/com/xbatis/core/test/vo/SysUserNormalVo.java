package com.xbatis.core.test.vo;

import cn.xbatis.db.annotations.ResultField;
import lombok.Data;

@Data
public class SysUserNormalVo {

    private Integer id;

    @ResultField("user_name")
    private String name2;
}
