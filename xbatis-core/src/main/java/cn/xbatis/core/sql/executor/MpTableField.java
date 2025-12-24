/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn) xbatis.
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

package cn.xbatis.core.sql.executor;

import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.mybatis.mapper.context.MybatisLikeQueryParameter;
import cn.xbatis.core.mybatis.mapper.context.MybatisParameter;
import cn.xbatis.core.mybatis.typeHandler.LikeQuerySupport;
import cn.xbatis.db.DatabaseCaseRule;
import db.sql.api.Cmd;
import db.sql.api.DbType;
import db.sql.api.cmd.CmdConvert;
import db.sql.api.cmd.LikeMode;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.impl.cmd.basic.TableField;

import java.util.Objects;

public class MpTableField extends TableField {

    private final TableFieldInfo tableFieldInfo;

    public MpTableField(MpTable table, TableFieldInfo tableFieldInfo) {
        super(table, tableFieldInfo.getColumnName(), tableFieldInfo.isTableId());
        this.tableFieldInfo = tableFieldInfo;
    }

    public TableFieldInfo getTableFieldInfo() {
        return tableFieldInfo;
    }

    @Override
    public String getName(DbType dbType) {
        if (tableFieldInfo.getTableAnnotation().databaseCaseRule() == DatabaseCaseRule.DEFAULT) {
            return super.getName(dbType);
        }
        return dbType.wrap(this.getName());
    }

    @Override
    public Object paramWrap(Class userType, Object param) {
        if (Objects.isNull(param)) {
            return null;
        } else if (param instanceof CmdConvert) {
            return ((CmdConvert) param).convert();
        }
        if (Objects.isNull(tableFieldInfo.getTypeHandler())) {
            return param;
        }
        if (!tableFieldInfo.getFieldInfo().getTypeClass().isAssignableFrom(param.getClass())) {
            return param;
        }
        if (tableFieldInfo.getTypeHandler() instanceof InvalidInConditionTypeHandler && ICondition.class.isAssignableFrom(userType)) {
            return param;
        }
        return new MybatisParameter(param, tableFieldInfo.getTableFieldAnnotation().typeHandler(), tableFieldInfo.getTableFieldAnnotation().jdbcType());
    }

    @Override
    public Object likeParamWrap(LikeMode likeMode, Object param, boolean isNotLike) {
        if (Objects.isNull(param) || param instanceof Cmd) {
            return param;
        } else if (param instanceof CmdConvert) {
            return ((CmdConvert) param).convert();
        }
        if (Objects.isNull(this.tableFieldInfo.getTypeHandler())) {
            return param;
        }
        if (!tableFieldInfo.getFieldInfo().getTypeClass().isAssignableFrom(param.getClass())) {
            return param;
        }
        Class typeHandler = tableFieldInfo.getTableFieldAnnotation().typeHandler();
        if (!LikeQuerySupport.class.isAssignableFrom(typeHandler)) {
            return param;
        }
//        if (tableFieldInfo.getTypeHandler() instanceof InvalidInConditionTypeHandler) {
//            return param;
//        }
        LikeQuerySupport likeQuerySupport = (LikeQuerySupport) tableFieldInfo.getTypeHandler();
        param = new MybatisLikeQueryParameter(param, isNotLike, likeMode, typeHandler, tableFieldInfo.getTableFieldAnnotation().jdbcType());
        likeMode = likeQuerySupport.convertLikeMode(likeMode, isNotLike);
        return new Object[]{likeMode, param};
    }
}
