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

package db.sql.api.impl.cmd;

import db.sql.api.Cmd;
import db.sql.api.Getter;
import db.sql.api.cmd.LikeMode;
import db.sql.api.cmd.basic.ICondition;
import db.sql.api.cmd.executor.IQuery;
import db.sql.api.cmd.executor.method.condition.IConditionMethods;
import db.sql.api.impl.cmd.executor.AbstractSubQuery;
import db.sql.api.impl.cmd.struct.ConditionChain;
import db.sql.api.impl.exception.ConditionArrayValueEmptyException;
import db.sql.api.impl.exception.ConditionValueNullException;
import db.sql.api.impl.tookit.SqlConst;
import db.sql.api.tookit.LambdaUtil;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

public class ConditionFactory implements IConditionMethods<ICondition, Cmd, Object> {

    protected final CmdFactory cmdFactory;
    private boolean isIgnoreEmpty = false;
    private boolean isIgnoreNull = false;
    private boolean isStringTrim = false;

    public ConditionFactory(CmdFactory cmdFactory) {
        this.cmdFactory = cmdFactory;
    }

    public ConditionChain newConditionChain(ConditionChain parent) {
        return new ConditionChain(this, parent);
    }

    public CmdFactory getCmdFactory() {
        return cmdFactory;
    }

    public boolean isIgnoreEmpty() {
        return isIgnoreEmpty;
    }

    public void setIgnoreEmpty(boolean isIgnoreEmpty) {
        this.isIgnoreEmpty = isIgnoreEmpty;
    }


    public boolean isIgnoreNull() {
        return isIgnoreNull;
    }

    public void setIgnoreNull(boolean isIgnoreNull) {
        this.isIgnoreNull = isIgnoreNull;
    }

    public boolean isStringTrim() {
        return isStringTrim;
    }

    public void setStringTrim(boolean isStringTrim) {
        this.isStringTrim = isStringTrim;
    }


    protected boolean isKeyValid(Cmd filed) {
        return filed != null;
    }

    private Object getSingleValue(Object value) {
        if (Objects.isNull(value)) {
            if (!isIgnoreNull()) {
                throw new ConditionValueNullException("条件参数里包含null值");
            }
            return null;
        }
        if (value instanceof String) {
            String str = (String) value;
            str = isStringTrim() ? str.trim() : str;
            if (isIgnoreEmpty() && SqlConst.S_EMPTY.equals(str)) {
                return null;
            }
            return str;
        }
        return value;
    }

    protected Object checkAndGetValidValue(Object value) {
        if (Objects.isNull(value)) {
            if (!isIgnoreNull()) {
                throw new ConditionValueNullException("条件参数里包含null值");
            }
            return null;
        }
        if (value instanceof Object[]) {
            Object[] values = (Object[]) value;
            List<Object> objectList = new ArrayList<>(values.length);
            for (Object v : values) {
                Object nv = getSingleValue(v);
                if (Objects.isNull(nv)) {
                    continue;
                }
                objectList.add(nv);
            }

            if (!isIgnoreNull() && objectList.isEmpty()) {
                throw new ConditionArrayValueEmptyException("array can't be empty");
            }
            int length = objectList.size();
            if (length == values.length) {
                for (int i = 0; i < length; i++) {
                    values[i] = objectList.get(i);
                }
                objectList.clear();
                return values;
            }

            Object[] newObject = (Object[]) Array.newInstance(value.getClass().getComponentType(), length);
            for (int i = 0; i < length; i++) {
                newObject[i] = objectList.get(i);
            }
            return newObject;
        } else if (value instanceof Collection) {
            Collection collection = (Collection) value;
            Collection<Object> objectList;
            if (value instanceof List) {
                objectList = new ArrayList<>();
            } else if (value instanceof Set) {
                objectList = new HashSet<>(collection.size());
            } else if (value instanceof Queue) {
                objectList = new ArrayDeque<>(collection.size());
            } else {
                //throw new RuntimeException("Not supported");
                objectList = new ArrayList<>(collection.size());
            }
            for (Object v : collection) {
                Object nv = getSingleValue(v);
                if (Objects.isNull(nv)) {
                    continue;
                }
                objectList.add(nv);
            }
            if (!isIgnoreNull() && objectList.isEmpty()) {
                throw new ConditionArrayValueEmptyException("collection can't be empty");
            }
            return collection;
        }
        return getSingleValue(value);
    }

    private Object paramWrap(Object value) {
        if (isStringTrim() && value instanceof String) {
            String str = (String) value;
            value = str.trim();
        }
        return value;
    }

    private <T> Cmd createTableField(Getter<T> column, int storey) {
        return cmdFactory.field(column, storey);
    }

    @Override
    public ICondition $conditionWhenFalseRV() {
        return null;
    }

    @Override
    public ICondition empty(Cmd column) {
        if (!isKeyValid(column)) {
            return null;
        }
        return Methods.eq(column, Methods.value(""));
    }

    @Override
    public <T> ICondition empty(boolean when, Getter<T> column, int storey) {
        if (!when) {
            return null;
        }
        return Methods.empty(createTableField(column, storey));
    }

    @Override
    public ICondition notEmpty(Cmd column) {
        if (!isKeyValid(column)) {
            return null;
        }
        return Methods.ne(column, Methods.value(""));
    }

    @Override
    public <T> ICondition notEmpty(boolean when, Getter<T> column, int storey) {
        if (!when) {
            return null;
        }
        return Methods.notEmpty(createTableField(column, storey));
    }

    @Override
    public ICondition eq(Cmd column, Object value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.eq(column, paramWrap(value));
    }

    @Override
    public ICondition ne(Cmd column, Object value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.ne(column, paramWrap(value));
    }

    @Override
    public ICondition gt(Cmd column, Object value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.gt(column, paramWrap(value));
    }

    @Override
    public ICondition gte(Cmd column, Object value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.gte(column, paramWrap(value));
    }

    @Override
    public ICondition lt(Cmd column, Object value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.lt(column, paramWrap(value));
    }

    @Override
    public ICondition lte(Cmd column, Object value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.lte(column, paramWrap(value));
    }

    @Override
    public ICondition like(LikeMode mode, Cmd column, String value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.like(mode, column, value);
    }

    @Override
    public <T> ICondition like(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        if (!when) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.like(mode, createTableField(column, storey), value);
    }

    @Override
    public ICondition notLike(LikeMode mode, Cmd column, String value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.notLike(mode, column, value);
    }

    @Override
    public <T> ICondition notLike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        if (!when) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.notLike(mode, createTableField(column, storey), value);
    }


    @Override
    public ICondition iLike(LikeMode mode, Cmd column, String value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.iLike(mode, column, value);
    }

    @Override
    public <T> ICondition iLike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        if (!when) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.iLike(mode, createTableField(column, storey), value);
    }

    @Override
    public ICondition notILike(LikeMode mode, Cmd column, String value) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.notILike(mode, column, value);
    }

    @Override
    public <T> ICondition notILike(boolean when, LikeMode mode, Getter<T> column, int storey, String value) {
        if (!when) {
            return null;
        }
        value = (String) checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.notILike(mode, createTableField(column, storey), value);
    }

    @Override
    public ICondition between(Cmd column, Object value, Object value2) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        value2 = checkAndGetValidValue(value2);
        if (Objects.isNull(value) || Objects.isNull(value2)) {
            if (Objects.isNull(value) && Objects.isNull(value2)) {
                return null;
            }
            throw new ConditionValueNullException("条件参数里包含null值");
        }
        return Methods.between(column, value, value2);
    }

    @Override
    public <T> ICondition between(boolean when, Getter<T> column, int storey, Object value, Object value2) {
        if (!when) {
            return null;
        }
        value = checkAndGetValidValue(value);
        value2 = checkAndGetValidValue(value2);
        if (Objects.isNull(value) || Objects.isNull(value2)) {
            if (Objects.isNull(value) && Objects.isNull(value2)) {
                return null;
            }
            throw new ConditionValueNullException("条件参数里包含null值");
        }
        return Methods.between(createTableField(column, storey), value, value2);
    }

    @Override
    public ICondition notBetween(Cmd column, Object value, Object value2) {
        if (!isKeyValid(column)) {
            return null;
        }
        value = checkAndGetValidValue(value);
        value2 = checkAndGetValidValue(value2);
        if (Objects.isNull(value) || Objects.isNull(value2)) {
            if (Objects.isNull(value) && Objects.isNull(value2)) {
                return null;
            }
            throw new ConditionValueNullException("条件参数里包含null值");
        }
        return Methods.notBetween(column, value, value2);
    }

    @Override
    public <T> ICondition notBetween(boolean when, Getter<T> column, int storey, Object value, Object value2) {
        if (!when) {
            return null;
        }

        value = checkAndGetValidValue(value);
        value2 = checkAndGetValidValue(value2);
        if (Objects.isNull(value) || Objects.isNull(value2)) {
            if (Objects.isNull(value) && Objects.isNull(value2)) {
                return null;
            }
            throw new ConditionValueNullException("条件参数里包含null值");
        }
        return Methods.notBetween(createTableField(column, storey), value, value2);
    }

    @Override
    public ICondition isNull(Cmd column) {
        if (!isKeyValid(column)) {
            return null;
        }
        return Methods.isNull(column);
    }

    @Override
    public ICondition isNotNull(Cmd column) {
        if (!isKeyValid(column)) {
            return null;
        }
        return Methods.isNotNull(column);
    }

    @Override
    public <T> ICondition eq(boolean when, Getter<T> column, int storey, Object value) {
        if (!when) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.eq(createTableField(column, storey), paramWrap(value));
    }

    @Override
    public <T, T2> ICondition eq(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        if (!when) {
            return null;
        }
        Object v = this.checkAndGetValidValue(value);
        if (Objects.isNull(v)) {
            return null;
        }
        return Methods.eq(createTableField(column, columnStorey), createTableField(value, valueStorey));
    }

    @Override
    public <T> ICondition gt(boolean when, Getter<T> column, int storey, Object value) {
        if (!when) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.gt(createTableField(column, storey), paramWrap(value));
    }

    @Override
    public <T, T2> ICondition gt(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        if (!when) {
            return null;
        }
        Object v = this.checkAndGetValidValue(value);
        if (Objects.isNull(v)) {
            return null;
        }
        return Methods.gt(createTableField(column, columnStorey), createTableField(value, valueStorey));
    }

    @Override
    public <T> ICondition gte(boolean when, Getter<T> column, int storey, Object value) {
        if (!when) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.gte(createTableField(column, storey), paramWrap(value));
    }

    @Override
    public <T, T2> ICondition gte(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        if (!when) {
            return null;
        }
        Object v = this.checkAndGetValidValue(value);
        if (Objects.isNull(v)) {
            return null;
        }
        return Methods.gte(createTableField(column, columnStorey), createTableField(value, valueStorey));
    }


    @Override
    public <T> ICondition lt(boolean when, Getter<T> column, int storey, Object value) {
        if (!when) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }

        return Methods.lt(createTableField(column, storey), paramWrap(value));
    }

    @Override
    public <T, T2> ICondition lt(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        if (!when) {
            return null;
        }
        Object v = this.checkAndGetValidValue(value);
        if (Objects.isNull(v)) {
            return null;
        }
        return Methods.lt(createTableField(column, columnStorey), createTableField(value, valueStorey));
    }

    @Override
    public <T> ICondition lte(boolean when, Getter<T> column, int storey, Object value) {
        if (!when) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }
        return Methods.lte(createTableField(column, storey), paramWrap(value));
    }

    @Override
    public <T, T2> ICondition lte(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        if (!when) {
            return null;
        }
        Object v = this.checkAndGetValidValue(value);
        if (Objects.isNull(v)) {
            return null;
        }
        return Methods.lte(createTableField(column, columnStorey), createTableField(value, valueStorey));
    }

    @Override
    public <T> ICondition ne(boolean when, Getter<T> column, int storey, Object value) {
        if (!when) {
            return null;
        }
        value = checkAndGetValidValue(value);
        if (Objects.isNull(value)) {
            return null;
        }

        return Methods.ne(createTableField(column, storey), paramWrap(value));
    }

    @Override
    public <T, T2> ICondition ne(boolean when, Getter<T> column, int columnStorey, Getter<T2> value, int valueStorey) {
        if (!when) {
            return null;
        }
        Object v = this.checkAndGetValidValue(value);
        if (Objects.isNull(v)) {
            return null;
        }
        return Methods.ne(createTableField(column, columnStorey), createTableField(value, valueStorey));
    }


    @Override
    public <T> ICondition isNull(boolean when, Getter<T> column, int storey) {
        if (!when) {
            return null;
        }
        return Methods.isNull(createTableField(column, storey));
    }

    @Override
    public <T> ICondition isNotNull(boolean when, Getter<T> column, int storey) {
        if (!when) {
            return null;
        }
        return Methods.isNotNull(createTableField(column, storey));
    }

    @Override
    public ICondition in(Cmd column, IQuery query) {
        Objects.requireNonNull(query);
        return Methods.in(column, query);
    }

    @Override
    @SafeVarargs
    public final ICondition in(Cmd column, Serializable... values) {
        values = (Serializable[]) checkAndGetValidValue(values);
        if (Objects.isNull(values) || values.length == 0) {
            return null;
        }
        return Methods.in(column, (Object[]) values);
    }

    @Override
    public ICondition in(Cmd column, Collection<? extends Serializable> values) {
        values = (Collection<Serializable>) checkAndGetValidValue(values);
        if (Objects.isNull(values) || values.isEmpty()) {
            return null;
        }
        return Methods.in(column, values);
    }

    @Override
    public <T> ICondition in(boolean when, Getter<T> column, int storey, IQuery query) {
        if (!when) {
            return null;
        }
        Objects.requireNonNull(query);
        return Methods.in(createTableField(column, storey), query);
    }

    @Override
    @SafeVarargs
    public final <T> ICondition in(boolean when, Getter<T> column, int storey, Serializable... values) {
        if (!when) {
            return null;
        }
        values = (Serializable[]) checkAndGetValidValue(values);
        if (Objects.isNull(values) || values.length == 0) {
            return null;
        }
        return Methods.in(createTableField(column, storey), (Object[]) values);
    }

    @Override
    public <T> ICondition in(boolean when, Getter<T> column, int storey, Collection<? extends Serializable> values) {
        if (!when) {
            return null;
        }
        values = (Collection<Serializable>) checkAndGetValidValue(values);
        if (Objects.isNull(values) || values.isEmpty()) {
            return null;
        }
        return Methods.in(createTableField(column, storey), values);
    }

    @Override
    public ICondition exists(boolean when, IQuery query) {
        if (!when) {
            return null;
        }
        Objects.requireNonNull(query);
        return Methods.exists(query);
    }

    @Override
    public <T1, T2> ICondition exists(boolean when, Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter) {
        if (!when) {
            return null;
        }
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(targetGetter);

        return Methods.exists(cmdFactory.createSubQuery()
                .select1()
                .from(lambdaFieldInfo.getType())
                .eq(targetGetter, cmdFactory.field(sourceGetter, sourceStorey))
        );
    }


    @Override
    public ICondition notExists(boolean when, IQuery query) {
        if (!when) {
            return null;
        }
        Objects.requireNonNull(query);
        return Methods.notExists(query);
    }

    @Override
    public <T1, T2> ICondition notExists(boolean when, Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter) {
        if (!when) {
            return null;
        }
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(targetGetter);
        return Methods.notExists(cmdFactory.createSubQuery()
                .select1()
                .from(lambdaFieldInfo.getType())
                .eq(targetGetter, cmdFactory.field(sourceGetter, sourceStorey))
        );
    }

    @Override
    public ICondition notIn(Cmd column, IQuery query) {
        Objects.requireNonNull(query);
        return Methods.notIn(column, query);
    }

    @Override
    @SafeVarargs
    public final ICondition notIn(Cmd column, Serializable... values) {
        values = (Serializable[]) checkAndGetValidValue(values);
        if (Objects.isNull(values) || values.length == 0) {
            return null;
        }
        return Methods.notIn(column, (Object[]) values);
    }

    @Override
    public ICondition notIn(Cmd column, Collection<? extends Serializable> values) {
        values = (Collection<Serializable>) checkAndGetValidValue(values);
        if (Objects.isNull(values) || values.isEmpty()) {
            return null;
        }
        return Methods.notIn(column, values);
    }

    @Override
    public <T> ICondition notIn(boolean when, Getter<T> column, int storey, IQuery query) {
        if (!when) {
            return null;
        }
        Objects.requireNonNull(query);
        return Methods.notIn(createTableField(column, storey), query);
    }

    @Override
    @SafeVarargs
    public final <T> ICondition notIn(boolean when, Getter<T> column, int storey, Serializable... values) {
        if (!when) {
            return null;
        }
        Object[] newValues = (Serializable[]) checkAndGetValidValue(values);
        if (Objects.isNull(newValues) || newValues.length == 0) {
            return null;
        }
        return Methods.notIn(createTableField(column, storey), newValues);
    }

    @Override
    public <T> ICondition notIn(boolean when, Getter<T> column, int storey, Collection<? extends Serializable> values) {
        if (!when) {
            return null;
        }
        values = (Collection<? extends Serializable>) checkAndGetValidValue(values);
        if (Objects.isNull(values) || values.isEmpty()) {
            return null;
        }
        return Methods.notIn(createTableField(column, storey), values);
    }

    public <T1, T2> ICondition exists(boolean when, Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter, Consumer<AbstractSubQuery<?, ?>> consumer) {
        if (!when) {
            return null;
        }
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(targetGetter);

        return Methods.exists(cmdFactory.createSubQuery()
                .select1()
                .from(lambdaFieldInfo.getType())
                .eq(targetGetter, cmdFactory.field(sourceGetter, sourceStorey))
                .connect(consumer != null, q -> consumer.accept(q))
        );
    }

    public <T1, T2> ICondition notExists(boolean when, Getter<T1> sourceGetter, int sourceStorey, Getter<T2> targetGetter, Consumer<AbstractSubQuery<?, ?>> consumer) {
        if (!when) {
            return null;
        }
        LambdaUtil.LambdaFieldInfo lambdaFieldInfo = LambdaUtil.getFieldInfo(targetGetter);

        return Methods.notExists(cmdFactory.createSubQuery()
                .select1()
                .from(lambdaFieldInfo.getType())
                .eq(targetGetter, cmdFactory.field(sourceGetter, sourceStorey))
                .connect(consumer != null, q -> consumer.accept(q))
        );
    }
}
