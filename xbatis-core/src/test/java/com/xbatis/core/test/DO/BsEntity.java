package com.xbatis.core.test.DO;

import cn.xbatis.db.annotations.Ignore;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BsEntity {

    @Ignore
    private Map<String,Object> params =new HashMap();


}
