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
import db.sql.api.cmd.LikeMode;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.impl.cmd.basic.DatasetField;
import org.apache.ibatis.type.TypeHandler;

import java.util.Objects;

public class MpDatasetField extends DatasetField {

    private final TableFieldInfo tableFieldInfo;

    public MpDatasetField(IDataset dataset, TableFieldInfo tableFieldInfo) {
        super(dataset, tableFieldInfo.getColumnName());
        this.tableFieldInfo = tableFieldInfo;
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
        if (Objects.isNull(param) || param instanceof Cmd) {
            return param;
        }
        if (!this.tableFieldInfo.getFieldInfo().getTypeClass().isAssignableFrom(param.getClass())) {
            return param;
        }
        if (Objects.isNull(this.tableFieldInfo.getTypeHandler())) {
            return param;
        }
//        if (tableFieldInfo.getTypeHandler() instanceof InvalidInConditionTypeHandler && ICondition.class.isAssignableFrom(userType)) {
//            return param;
//        }
        return new MybatisParameter(param, (Class<? extends TypeHandler<?>>) this.tableFieldInfo.getTypeHandler().getClass(), this.tableFieldInfo.getTableFieldAnnotation().jdbcType());
    }

    @Override
    public Object likeParamWrap(LikeMode likeMode, Object param, boolean isNotLike) {
        if (Objects.isNull(param) || param instanceof Cmd) {
            return param;
        }
        if (!this.tableFieldInfo.getFieldInfo().getTypeClass().isAssignableFrom(param.getClass())) {
            return param;
        }
        if (Objects.isNull(this.tableFieldInfo.getTypeHandler())) {
            return param;
        }
        if (!(this.tableFieldInfo.getTypeHandler() instanceof LikeQuerySupport)) {
            return param;
        }
//        if (tableFieldInfo.getTypeHandler() instanceof InvalidInConditionTypeHandler) {
//            return param;
//        }
        LikeQuerySupport likeQuerySupport = (LikeQuerySupport) this.tableFieldInfo.getTypeHandler();
        param = new MybatisLikeQueryParameter(param, isNotLike, likeMode, (Class<? extends TypeHandler<?>>) this.tableFieldInfo.getTypeHandler().getClass(), this.tableFieldInfo.getTableFieldAnnotation().jdbcType());
        likeMode = likeQuerySupport.convertLikeMode(likeMode, isNotLike);
        return new Object[]{likeMode, param};
    }

    public TableFieldInfo getTableFieldInfo() {
        return tableFieldInfo;
    }
}
