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

package com.xbatis.core.test.testCase.multiPk;

import com.xbatis.core.test.DO.MultiPk;
import com.xbatis.core.test.mapper.MultiPkMapper;
import com.xbatis.core.test.model.MultiPkModel;
import com.xbatis.core.test.testCase.BaseTest;
import com.xbatis.core.test.testCase.TestDataSource;
import db.sql.api.DbModel;
import db.sql.api.DbType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiPkTestCase extends BaseTest {

    private MultiPk getById(MultiPkMapper mapper, Integer id1, Integer id2) {
        return mapper.get(where -> {
            where.eq(MultiPk::getId1, id1).eq(MultiPk::getId2, id2);
        });
    }

    @Test
    public void saveBatchTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");
            mapper.saveBatch(Collections.singletonList(entity));
        }
    }

    @Test
    public void saveConflictTest() {
        if (!isSupportConflict()) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");

            mapper.save(entity);
            mapper.save(entity, strategy ->
                    strategy.onConflict(action -> action.doNothing())
            );
            mapper.save(Collections.singletonList(entity), strategy ->
                    strategy.onConflict(action -> action.doNothing())
            );
        }

        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE.getDbModel() == DbModel.ORACLE) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");


            mapper.save(entity);
            entity.setName("xxxx");
            mapper.save(entity, strategy -> {
                //strategy.conflictKeys(MultiPk::getId1, MultiPk::getId2);
                strategy.onConflict(action -> action.doUpdate(update -> update.overwrite(MultiPk::getName)));
            });
            mapper.save(Collections.singletonList(entity), strategy -> {
                //strategy.conflictKeys(MultiPk::getId1, MultiPk::getId2);
                strategy.onConflict(action -> action.doUpdate(update -> update.overwrite(MultiPk::getName)));
            });
            entity = mapper.get(where -> where.eq(MultiPk::getId1, 1).eq(MultiPk::getId2, 2));
            assertEquals("xxxx", entity.getName());
            System.out.println(entity);
        }


    }

    @Test
    public void saveConflictTest2() {
        if (!isSupportConflict()) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPkModel entity = new MultiPkModel();
            entity.setId1(1);
            entity.setId2x(2);
            entity.setName("12");
            mapper.save(entity);
            mapper.save(entity, strategy ->
                    strategy.onConflict(action -> action.doNothing())
            );
            mapper.saveModel(Collections.singletonList(entity), strategy ->
                    strategy.onConflict(action -> action.doNothing())
            );
        }

        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE.getDbModel() == DbModel.ORACLE) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPkModel entity = new MultiPkModel();
            entity.setId1(1);
            entity.setId2x(2);
            entity.setName("12");
            mapper.saveModel(Collections.singletonList(entity));
            entity.setName("xxxx");
            mapper.save(entity, strategy -> {
                strategy.conflictKeys(MultiPkModel::getId1, MultiPkModel::getId2x)
                        .onConflict(action -> action.doUpdate(update -> update.overwrite(MultiPkModel::getName)));
            });
            mapper.saveModel(Collections.singletonList(entity), strategy -> {
                strategy.conflictKeys(MultiPkModel::getId1, MultiPkModel::getId2x)
                        .onConflict(action -> action.doUpdate(update -> update.overwrite(MultiPkModel::getName)));
            });
            MultiPk entity2 = mapper.get(where -> where.eq(MultiPk::getId1, 1).eq(MultiPk::getId2, 2));
            assertEquals("xxxx", entity2.getName());
            System.out.println(entity2);
        }


    }

    @Test
    public void saveBatchConflictTest() {
        if (!isSupportConflict()) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");

            MultiPk entity2 = new MultiPk();
            entity2.setId1(2);
            entity2.setId2(2);
            entity2.setName("12");
            mapper.saveBatch(Arrays.asList(entity, entity2));
            mapper.saveBatch(Collections.singletonList(entity), strategy ->
                    strategy.onConflict(action -> action.doNothing())
            );
        }

        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE.getDbModel() == DbModel.ORACLE) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");

            MultiPk entity2 = new MultiPk();
            entity2.setId1(2);
            entity2.setId2(2);
            entity2.setName("12");

            mapper.saveBatch(Arrays.asList(entity, entity2));
            entity.setName("xxxx");
            mapper.saveBatch(Collections.singletonList(entity), strategy -> {
                //strategy.conflictKeys(MultiPk::getId1, MultiPk::getId2);
                strategy.onConflict(action -> action.doUpdate(update -> update.overwriteAll()));
            });
            entity = mapper.get(where -> where.eq(MultiPk::getId1, 1).eq(MultiPk::getId2, 2));
            assertEquals("xxxx", entity.getName());
            System.out.println(entity);
        }


    }

    private boolean isSupportConflict() {
        if (TestDataSource.DB_TYPE != DbType.H2
                && TestDataSource.DB_TYPE.getDbModel() != DbModel.MYSQL
                && TestDataSource.DB_TYPE != DbType.MYSQL
                && TestDataSource.DB_TYPE != DbType.MARIA_DB

                && TestDataSource.DB_TYPE.getDbModel() != DbModel.PGSQL
                && TestDataSource.DB_TYPE != DbType.PGSQL
                && TestDataSource.DB_TYPE != DbType.KING_BASE
                && TestDataSource.DB_TYPE != DbType.GAUSS
                && TestDataSource.DB_TYPE != DbType.SQLITE

                && TestDataSource.DB_TYPE.getDbModel() != DbModel.ORACLE
                && TestDataSource.DB_TYPE != DbType.ORACLE
        ) {
            return false;
        }
        return true;
    }

    @Test
    public void saveBatchConflictTest2() {
        if (!isSupportConflict()) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPkModel entity = new MultiPkModel();
            entity.setId1(1);
            entity.setId2x(2);
            entity.setName("12");

            MultiPkModel entity2 = new MultiPkModel();
            entity2.setId1(2);
            entity2.setId2x(2);
            entity2.setName("12");
            mapper.saveModelBatch(Arrays.asList(entity, entity2));
            mapper.saveModelBatch(Arrays.asList(entity, entity2), strategy ->
                    strategy.onConflict(action -> action.doNothing())
            );
        }

        if (TestDataSource.DB_TYPE == DbType.ORACLE || TestDataSource.DB_TYPE.getDbModel() == DbModel.ORACLE) {
            return;
        }

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPkModel entity = new MultiPkModel();
            entity.setId1(1);
            entity.setId2x(2);
            entity.setName("12");
            mapper.saveModelBatch(Collections.singletonList(entity));
            entity.setName("xxxx");
            mapper.saveModelBatch(Collections.singletonList(entity), strategy -> {
                strategy.conflictKeys(MultiPkModel::getId1, MultiPkModel::getId2x)
                        .onConflict(action -> action.doUpdate(update -> update.overwrite(MultiPkModel::getName)));
            });
            MultiPk entity2 = mapper.get(where -> where.eq(MultiPk::getId1, 1).eq(MultiPk::getId2, 2));
            assertEquals("xxxx", entity2.getName());
            System.out.println(entity2);
        }


    }

    @Test
    public void saveOrUpdateTest() {
        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);

            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");
            mapper.saveOrUpdate(Collections.singletonList(entity));
            mapper.saveOrUpdate(Collections.singletonList(entity));
        }
    }

    @Test
    public void oneTest() {

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);
            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");
            mapper.save(entity);

            assertEquals(entity, getById(mapper, 1, 2));
            entity.setName("12update");
            mapper.update(entity);
            mapper.saveOrUpdate(entity);

            assertEquals("12update", getById(mapper, 1, 2).getName());
            mapper.delete(entity);

            assertEquals(null, getById(mapper, 1, 2));
        }

    }

    @Test
    public void multiTest() {

        try (SqlSession session = this.sqlSessionFactory.openSession(false)) {
            MultiPkMapper mapper = session.getMapper(MultiPkMapper.class);
            MultiPk entity = new MultiPk();
            entity.setId1(1);
            entity.setId2(2);
            entity.setName("12");
            mapper.saveOrUpdate(entity);

            assertEquals(entity, getById(mapper, 1, 2));
            entity.setName("12update");

            List<MultiPk> list = Arrays.asList(entity);
            mapper.update(list);

            assertEquals("12update", getById(mapper, 1, 2).getName());

            mapper.delete(list);
            assertEquals(null, getById(mapper, 1, 2));
        }

    }
}
