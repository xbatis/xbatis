package com.xbatis.core.test.DO;

import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableId;
import com.xbatis.core.test.enums.BuiltinRole;

@Table("role")
public class Role {
    @TableId
    private Long id;
    private BuiltinRole code;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BuiltinRole getCode() { return code; }
    public void setCode(BuiltinRole code) { this.code = code; }
}
