package com.xbatis.core.test.enums;

import cn.xbatis.core.mybatis.typeHandler.EnumSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BuiltinRole implements EnumSupport<Integer> {
    ADMIN(1), MERCHANT(2);

    private final Integer code;

    public static BuiltinRole of(Integer code) {
        for (BuiltinRole value : values()) if (value.code.equals(code)) return value;
        return null;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
