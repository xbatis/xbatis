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

package cn.xbatis.core.mybatis.configuration;

import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.db.reflect.TableInfo;
import cn.xbatis.core.db.reflect.Tables;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;

public class EntityWrapper extends BeanWrapper {

    private final Object entity;

    private final TableInfo tableInfo;

    public EntityWrapper(MetaObject metaObject, Object entity) {
        super(metaObject, entity);
        this.tableInfo = Tables.get(entity.getClass());
        this.entity = entity;
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(prop.getName());
        if (tableFieldInfo == null) {
            super.set(prop, value);
        } else {
            tableFieldInfo.setValue(entity, value);
        }
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        TableFieldInfo tableFieldInfo = tableInfo.getFieldInfo(prop.getName());
        if (tableFieldInfo == null) {
            return super.get(prop);
        } else {
            return tableFieldInfo.getValue(this.entity);
        }
    }
}
