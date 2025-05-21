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

package db.sql.api.cmd.executor.method;

import db.sql.api.Getter;
import db.sql.api.cmd.JoinMode;
import db.sql.api.cmd.basic.IDataset;
import db.sql.api.cmd.struct.IOn;
import db.sql.api.tookit.LambdaUtil;

import java.util.function.Consumer;

public interface IJoinMethod<SELF extends IJoinMethod, JOIN, ON extends IOn> {

    JOIN $join(JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> onConsumer);

    // DATASET join
    default SELF join(IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF innerJoin(IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF leftJoin(IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.LEFT, mainTable, secondTable, consumer);
    }

    default SELF rightJoin(IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.RIGHT, mainTable, secondTable, consumer);
    }


    // DATASET class join
    default SELF join(IDataset<?, ?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF innerJoin(IDataset<?, ?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF leftJoin(IDataset<?, ?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.LEFT, mainTable, secondTable, consumer);
    }

    default SELF rightJoin(IDataset<?, ?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return this.join(JoinMode.RIGHT, mainTable, secondTable, consumer);
    }

    SELF join(JoinMode mode, IDataset<?, ?> mainTable, Class<?> secondTable, Consumer<ON> consumer);

    SELF join(JoinMode mode, IDataset<?, ?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer);

    //class JOIN
    default SELF join(JoinMode mode, Class<?> mainTable, Class<?> secondTable) {
        return join(mode, mainTable, secondTable, (Consumer<ON>) null);
    }

    default SELF join(Class<?> mainTable, Class<?> secondTable) {
        return join(JoinMode.INNER, mainTable, secondTable);
    }

    default SELF innerJoin(Class<?> mainTable, Class<?> secondTable) {
        return join(JoinMode.INNER, mainTable, secondTable);
    }

    default SELF leftJoin(Class<?> mainTable, Class<?> secondTable) {
        return join(JoinMode.LEFT, mainTable, secondTable);
    }

    default SELF rightJoin(Class<?> mainTable, Class<?> secondTable) {
        return join(JoinMode.RIGHT, mainTable, secondTable);
    }


    //class JOIN and ON consumer
    default SELF join(Class<?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF innerJoin(Class<?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF leftJoin(Class<?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.LEFT, mainTable, secondTable, consumer);
    }

    default SELF rightJoin(Class<?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.RIGHT, mainTable, secondTable, consumer);
    }

    default SELF join(JoinMode mode, Class<?> mainTable, Class<?> secondTable, Consumer<ON> consumer) {
        return join(mode, mainTable, 1, secondTable, 1, consumer);
    }

    default SELF join(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey) {
        return join(JoinMode.INNER, mainTable, mainTableStorey, secondTable, secondTableStorey);
    }

    default SELF innerJoin(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey) {
        return join(JoinMode.INNER, mainTable, mainTableStorey, secondTable, secondTableStorey);
    }

    default SELF leftJoin(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey) {
        return join(JoinMode.LEFT, mainTable, mainTableStorey, secondTable, secondTableStorey);
    }

    default SELF rightJoin(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey) {
        return join(JoinMode.RIGHT, mainTable, mainTableStorey, secondTable, secondTableStorey);
    }

    default SELF join(JoinMode mode, Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey) {
        return join(mode, mainTable, mainTableStorey, secondTable, secondTableStorey, (Consumer<ON>) null);
    }

    default SELF join(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, mainTableStorey, secondTable, secondTableStorey, consumer);
    }

    default SELF innerJoin(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, mainTableStorey, secondTable, secondTableStorey, consumer);
    }

    default SELF leftJoin(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey, Consumer<ON> consumer) {
        return join(JoinMode.LEFT, mainTable, mainTableStorey, secondTable, secondTableStorey, consumer);
    }

    default SELF rightJoin(Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey, Consumer<ON> consumer) {
        return join(JoinMode.RIGHT, mainTable, mainTableStorey, secondTable, secondTableStorey, consumer);
    }

    SELF join(JoinMode mode, Class<?> mainTable, int mainTableStorey, Class<?> secondTable, int secondTableStorey, Consumer<ON> consumer);

    default SELF join(Class<?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF innerJoin(Class<?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, secondTable, consumer);
    }

    default SELF leftJoin(Class<?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.LEFT, mainTable, secondTable, consumer);
    }

    default SELF rightJoin(Class<?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.RIGHT, mainTable, secondTable, consumer);
    }

    default SELF join(JoinMode mode, Class<?> mainTable, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(mode, mainTable, 1, secondTable, consumer);
    }

    default SELF join(Class<?> mainTable, int mainTableStorey, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, mainTableStorey, secondTable, consumer);
    }

    default SELF innerJoin(Class<?> mainTable, int mainTableStorey, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.INNER, mainTable, mainTableStorey, secondTable, consumer);
    }

    default SELF leftJoin(Class<?> mainTable, int mainTableStorey, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.LEFT, mainTable, mainTableStorey, secondTable, consumer);
    }

    default SELF rightJoin(Class<?> mainTable, int mainTableStorey, IDataset<?, ?> secondTable, Consumer<ON> consumer) {
        return join(JoinMode.RIGHT, mainTable, mainTableStorey, secondTable, consumer);
    }

    SELF join(JoinMode mode, Class<?> mainTable, int mainTableStorey, IDataset<?, ?> secondTable, Consumer<ON> consumer);

    //JOIN getter getter
    default <T1, T2> SELF join(Getter<T1> mainJoinField, Getter<T2> secondJoinField) {
        return this.join(JoinMode.INNER, mainJoinField, 1, secondJoinField, 1);
    }

    default <T1, T2> SELF innerJoin(Getter<T1> mainJoinField, Getter<T2> secondJoinField) {
        return this.join(JoinMode.INNER, mainJoinField, 1, secondJoinField, 1);
    }

    default <T1, T2> SELF leftJoin(Getter<T1> mainJoinField, Getter<T2> secondJoinField) {
        return this.join(JoinMode.LEFT, mainJoinField, 1, secondJoinField, 1);
    }

    default <T1, T2> SELF rightJoin(Getter<T1> mainJoinField, Getter<T2> secondJoinField) {
        return this.join(JoinMode.RIGHT, mainJoinField, 1, secondJoinField, 1);
    }

    default <T1, T2> SELF join(JoinMode mode, Getter<T1> mainJoinField, Getter<T2> secondJoinField) {
        return this.join(mode, mainJoinField, 1, secondJoinField, 1);
    }

    //JOIN getter getter on
    default <T1, T2> SELF join(Getter<T1> mainJoinField, Getter<T2> secondJoinField, Consumer<ON> consumer) {
        return this.join(JoinMode.INNER, mainJoinField, 1, secondJoinField, 1, consumer);
    }

    default <T1, T2> SELF innerJoin(Getter<T1> mainJoinField, Getter<T2> secondJoinField, Consumer<ON> consumer) {
        return this.join(JoinMode.INNER, mainJoinField, 1, secondJoinField, 1, consumer);
    }

    default <T1, T2> SELF leftJoin(Getter<T1> mainJoinField, Getter<T2> secondJoinField, Consumer<ON> consumer) {
        return this.join(JoinMode.LEFT, mainJoinField, 1, secondJoinField, 1, consumer);
    }

    default <T1, T2> SELF rightJoin(Getter<T1> mainJoinField, Getter<T2> secondJoinField, Consumer<ON> consumer) {
        return this.join(JoinMode.RIGHT, mainJoinField, 1, secondJoinField, 1, consumer);
    }

    default <T1, T2> SELF join(JoinMode mode, Getter<T1> mainJoinField, Getter<T2> secondJoinField, Consumer<ON> consumer) {
        return this.join(mode, mainJoinField, 1, secondJoinField, 1, consumer);
    }

    //JOIN getter storey  getter storey
    default <T1, T2> SELF join(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey) {
        return this.join(JoinMode.INNER, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey);
    }

    default <T1, T2> SELF innerJoin(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey) {
        return this.join(JoinMode.INNER, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey);
    }

    default <T1, T2> SELF leftJoin(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey) {
        return this.join(JoinMode.LEFT, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey);
    }

    default <T1, T2> SELF rightJoin(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey) {
        return this.join(JoinMode.RIGHT, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey);
    }

    default <T1, T2> SELF join(JoinMode mode, Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey) {
        return this.join(mode, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey, null);
    }

    //JOIN getter storey  getter storey on
    default <T1, T2> SELF join(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey, Consumer<ON> on) {
        return this.join(JoinMode.INNER, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey, on);
    }

    default <T1, T2> SELF innerJoin(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey, Consumer<ON> on) {
        return this.join(JoinMode.INNER, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey, on);
    }

    default <T1, T2> SELF leftJoin(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey, Consumer<ON> on) {
        return this.join(JoinMode.LEFT, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey, on);
    }

    default <T1, T2> SELF rightJoin(Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey, Consumer<ON> on) {
        return this.join(JoinMode.RIGHT, mainJoinField, mainTableStorey, secondJoinField, secondTableStorey, on);
    }

    default <T1, T2> SELF join(JoinMode mode, Getter<T1> mainJoinField, int mainTableStorey, Getter<T2> secondJoinField, int secondTableStorey, Consumer<ON> on) {
        return this.join(mode, LambdaUtil.getFieldInfo(mainJoinField).getType(), mainTableStorey, LambdaUtil.getFieldInfo(secondJoinField).getType(), secondTableStorey, o -> {
            o.eq(mainJoinField, mainTableStorey, secondJoinField, secondTableStorey);
            if (on != null) {
                on.accept(o);
            }
        });
    }
}
