/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License").
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 */

package cn.xbatis.core.db.reflect;

import cn.xbatis.core.util.TypeConvertUtil;
import lombok.Getter;
import org.apache.ibatis.type.TypeHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FetchWhenInfo {

    private final TableFieldInfo property;

    private final String column;

    private final List<Object> values;

    private volatile TypeHandler<?> propertyTypeHandler;

    public FetchWhenInfo(TableFieldInfo property, String column, String value) {
        this.property = property;
        this.column = column;
        String[] strs = value.split(",");
        this.values = Arrays.stream(strs).map(s -> TypeConvertUtil.convert(s, property.getFieldInfo().getFinalClass())).collect(Collectors.toList());
        this.propertyTypeHandler = property.getTypeHandler();
    }

    public void setPropertyTypeHandler(TypeHandler<?> propertyTypeHandler) {
        this.propertyTypeHandler = propertyTypeHandler;
    }
}
