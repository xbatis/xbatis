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

package cn.xbatis.core.sql.executor.chain;

import cn.xbatis.core.mybatis.mapper.BaseMapper;
import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.sql.executor.BaseDelete;
import db.sql.api.impl.cmd.struct.Where;

import java.util.Objects;

/**
 * 删除链路
 */
public class DeleteChain extends BaseDelete<DeleteChain> {

    protected BaseMapper mapper;

    protected Class<?> entityType;

    protected DeleteChain() {

    }

    public DeleteChain(MybatisMapper<?> mapper) {
        this.mapper = mapper;
    }

    public DeleteChain(MybatisMapper<?> mapper, Where where) {
        super(where);
        this.mapper = mapper;
    }

    public DeleteChain(BaseMapper mapper, Class<?> entityType) {
        this.mapper = mapper;
        this.entityType = entityType;
    }

    public DeleteChain(BaseMapper mapper, Class<?> entityType, Where where) {
        super(where);
        this.mapper = mapper;
        this.entityType = entityType;
    }

    public static DeleteChain of(MybatisMapper<?> mapper) {
        return new DeleteChain(mapper);
    }

    public static DeleteChain of(MybatisMapper<?> mapper, Where where) {
        return new DeleteChain(mapper, where);
    }

    public static DeleteChain of(BaseMapper mapper, Class<?> entityType) {
        return new DeleteChain(mapper, entityType);
    }

    public static DeleteChain of(BaseMapper mapper, Class<?> entityType, Where where) {
        return new DeleteChain(mapper, entityType, where);
    }

    /**
     * 非特殊情况 请使用of静态方法
     * 使用此方法后 后续执行查询需调用一次withMapper(mybatisMapper)方法
     *
     * @return 自己
     */
    public static DeleteChain create() {
        return new DeleteChain();
    }

    protected Class<?> getEntityType() {
        if (entityType != null) {
            return entityType;
        }
        if (mapper instanceof MybatisMapper) {
            this.entityType = ((MybatisMapper) mapper).getEntityType();
        } else {
            throw new RuntimeException("you need specify entityType");
        }

        return entityType;
    }

    private void setDefault() {
        if (this.getDeleteTable() == null && this.getFrom() == null) {
            //自动设置实体类
            this.delete(getEntityType());
            this.from(getEntityType());
        }
    }

    private void checkAndSetMapper(BaseMapper mapper) {
        if (Objects.isNull(this.mapper)) {
            this.mapper = mapper;
            return;
        }
        if (this.mapper == mapper) {
            return;
        }
        throw new RuntimeException(" the mapper is already set, can't use another mapper");
    }

    /**
     * 用create静态方法的 Chain 需要调用一次此方法 用于设置 mapper
     *
     * @param mapper 操作目标实体类的mapper
     * @return 自己
     */
    public DeleteChain withMapper(MybatisMapper<?> mapper) {
        this.checkAndSetMapper(mapper);
        return this;
    }

    /**
     * 用create静态方法的 Chain 需要调用一次此方法 用于设置 mapper
     *
     * @param mapper 一般都是BasicMapper
     * @return 自己
     */
    public DeleteChain withMapper(BaseMapper mapper, Class<?> entityType) {
        this.checkAndSetMapper(mapper);
        this.entityType = entityType;
        return this;
    }

    /**
     * 执行
     *
     * @return
     */
    public int execute() {
        this.setDefault();
        return mapper.delete(this);
    }
}
