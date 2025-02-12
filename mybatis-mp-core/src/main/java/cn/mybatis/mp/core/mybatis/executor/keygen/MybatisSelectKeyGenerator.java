/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
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
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MybatisSelectKeyGenerator extends SelectKeyGenerator {
    protected final boolean executeBefore;
    protected final MappedStatement keyStatement;

    public MybatisSelectKeyGenerator(MappedStatement keyStatement, boolean executeBefore) {
        super(keyStatement, executeBefore);
        this.executeBefore = executeBefore;
        this.keyStatement = keyStatement;
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (executeBefore) {
            if (parameter instanceof SetIdMethod) {
                this.processGeneratedKeys(executor, ms, (SetIdMethod) parameter);
            } else {
                super.processBefore(executor, ms, stmt, parameter);
            }
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (!executeBefore) {
            if (parameter instanceof SetIdMethod) {
                this.processGeneratedKeys(executor, ms, (SetIdMethod) parameter);
            } else {
                super.processAfter(executor, ms, stmt, parameter);
            }
        }
    }

    protected void processGeneratedKeys(Executor executor, MappedStatement ms, SetIdMethod parameter) {
        if (parameter == null || keyStatement == null || keyStatement.getKeyProperties() == null || parameter.idHasValue()) {
            return;
        }
        try {
            this.generatedKeys(executor, ms, parameter);
        } catch (ExecutorException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
        }
    }

    protected void generatedKeys(Executor executor, MappedStatement ms, SetIdMethod setIdMethod) throws SQLException {
        final Configuration configuration = ms.getConfiguration();
        Executor keyExecutor = configuration.newExecutor(executor.getTransaction(), ExecutorType.SIMPLE);
        for (int i = 0; i < setIdMethod.getInsertSize(); i++) {
            setIdMethod.setId(getId(keyExecutor, setIdMethod, i).get(0), i);
        }
    }

    protected List<Object> getId(Executor keyExecutor, SetIdMethod setIdMethod, int index) throws SQLException {
        List<Object> values = keyExecutor.query(keyStatement, setIdMethod.getInsertData(index), RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
        if (values.isEmpty()) {
            throw new ExecutorException("SelectKey returned no data.");
        }
        if (values.size() > 1) {
            throw new ExecutorException("SelectKey returned more than one value.");
        }
        return values;
    }
}
