/*
 *  Copyright (c) 2024-2024, Ai东 (abc-127@live.cn).
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

package cn.mybatis.mp.core.mybatis.executor.keygen;

import cn.mybatis.mp.core.mybatis.mapper.context.SetIdMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MybatisJdbc3KeyGenerator extends Jdbc3KeyGenerator {

    public static final MybatisJdbc3KeyGenerator INSTANCE = new MybatisJdbc3KeyGenerator();

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (parameter instanceof SetIdMethod) {
            final String[] keyProperties = ms.getKeyProperties();
            if (keyProperties == null || keyProperties.length == 0) {
                return;
            }

            SetIdMethod setIdMethod = (SetIdMethod) parameter;
            if (setIdMethod.idHasValue()) {
                return;
            }
            final Configuration configuration = ms.getConfiguration();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                final ResultSetMetaData rsmd = rs.getMetaData();

                if (rsmd.getColumnCount() < keyProperties.length) {
                    // Error?
                } else {
                    this.assignKeys(configuration, rs, setIdMethod);
                }
            } catch (Exception e) {
                throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
            }
            return;
        }
        super.processAfter(executor, ms, stmt, parameter);
    }

    private void assignSQLServerKeys(Configuration configuration, ResultSet rs, SetIdMethod setIdMethod) throws SQLException {
        int insertSize = setIdMethod.getInsertSize();
        for (int i = 0; i < insertSize; i++) {
            rs.next();
            setIdMethod.setId(setIdMethod.getIdTypeHandler(configuration).getResult(rs, setIdMethod.getIdColumnName()), i);
        }
    }

    private void assignKeys(Configuration configuration, ResultSet rs, SetIdMethod setIdMethod) throws SQLException {
        int insertSize = setIdMethod.getInsertSize();
        List<Object> genIds = new ArrayList<>(insertSize);
        for (int i = 0; i < insertSize; i++) {
            if (!rs.next()) {
                return;
            }
            genIds.add(setIdMethod.getIdTypeHandler(configuration).getResult(rs, 1));
        }

        if (genIds.size() == insertSize) {
            for (int i = 0; i < insertSize; i++) {
                setIdMethod.setId(genIds.get(i), i);
            }
        }
    }
}
