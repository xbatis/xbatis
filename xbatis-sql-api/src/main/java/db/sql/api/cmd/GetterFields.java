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

package db.sql.api.cmd;

import db.sql.api.Getter;

/**
 * GetterField 数组
 */
public final class GetterFields {

    @SafeVarargs
    public static <T> GetterField[] of(Getter<T>... getters) {
        return of(1, getters);
    }

    @SafeVarargs
    public static <T> GetterField[] of(int storey, Getter<T>... getters) {
        GetterField[] getterFields = new GetterField[getters.length];
        for (int i = 0; i < getters.length; i++) {
            getterFields[i] = GetterField.create(getters[i], storey);
        }
        return getterFields;
    }

    public static <T> GetterField[] merge(GetterField[] arr1, GetterField<T>[] arr2) {
        GetterField[] getterFields = new GetterField[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, getterFields, 0, arr1.length);
        System.arraycopy(arr2, 0, getterFields, arr1.length, arr2.length);
        return getterFields;
    }

    public static <T1> GetterField[] of(Getter<T1> getter1) {
        return new GetterField[]{GetterField.create(getter1)};
    }

    public static <T1> GetterField[] of(Getter<T1> getter1, int storey1) {
        return new GetterField[]{GetterField.create(getter1, storey1)};
    }

    public static <T1, T2> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2)};
    }

    public static <T1, T2> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2)};
    }

    public static <T1, T2, T3> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3)};
    }

    public static <T1, T2, T3> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3)};
    }

    public static <T1, T2, T3, T4> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3), GetterField.create(getter4)};
    }

    public static <T1, T2, T3, T4> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3), GetterField.create(getter4, storey4)};
    }

    public static <T1, T2, T3, T4, T5> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3), GetterField.create(getter4), GetterField.create(getter5)};
    }

    public static <T1, T2, T3, T4, T5> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3), GetterField.create(getter4, storey4), GetterField.create(getter5, storey5)};
    }

    public static <T1, T2, T3, T4, T5, T6> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3), GetterField.create(getter4), GetterField.create(getter5), GetterField.create(getter6)};
    }

    public static <T1, T2, T3, T4, T5, T6> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3), GetterField.create(getter4, storey4), GetterField.create(getter5, storey5), GetterField.create(getter6, storey6)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3), GetterField.create(getter4), GetterField.create(getter5), GetterField.create(getter6), GetterField.create(getter7)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3), GetterField.create(getter4, storey4), GetterField.create(getter5, storey5), GetterField.create(getter6, storey6), GetterField.create(getter7, storey7)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7, Getter<T8> getter8) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3), GetterField.create(getter4), GetterField.create(getter5), GetterField.create(getter6), GetterField.create(getter7), GetterField.create(getter8)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7, Getter<T8> getter8, int storey8) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3), GetterField.create(getter4, storey4), GetterField.create(getter5, storey5), GetterField.create(getter6, storey6), GetterField.create(getter7, storey7), GetterField.create(getter8, storey8)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7, Getter<T8> getter8, Getter<T9> getter9) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3), GetterField.create(getter4), GetterField.create(getter5), GetterField.create(getter6), GetterField.create(getter7), GetterField.create(getter8), GetterField.create(getter9)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7, Getter<T8> getter8, int storey8, Getter<T9> getter9, int storey9) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3), GetterField.create(getter4, storey4), GetterField.create(getter5, storey5), GetterField.create(getter6, storey6), GetterField.create(getter7, storey7), GetterField.create(getter8, storey8), GetterField.create(getter9, storey9)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> GetterField[] of(Getter<T1> getter1, Getter<T2> getter2, Getter<T3> getter3, Getter<T4> getter4, Getter<T5> getter5, Getter<T6> getter6, Getter<T7> getter7, Getter<T8> getter8, Getter<T9> getter9, Getter<T10> getter10) {
        return new GetterField[]{GetterField.create(getter1), GetterField.create(getter2), GetterField.create(getter3), GetterField.create(getter4), GetterField.create(getter5), GetterField.create(getter6), GetterField.create(getter7), GetterField.create(getter8), GetterField.create(getter9), GetterField.create(getter10)};
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> GetterField[] of(Getter<T1> getter1, int storey1, Getter<T2> getter2, int storey2, Getter<T3> getter3, int storey3, Getter<T4> getter4, int storey4, Getter<T5> getter5, int storey5, Getter<T6> getter6, int storey6, Getter<T7> getter7, int storey7, Getter<T8> getter8, int storey8, Getter<T9> getter9, int storey9, Getter<T10> getter10, int storey10) {
        return new GetterField[]{GetterField.create(getter1, storey1), GetterField.create(getter2, storey2), GetterField.create(getter3, storey3), GetterField.create(getter4, storey4), GetterField.create(getter5, storey5), GetterField.create(getter6, storey6), GetterField.create(getter7, storey7), GetterField.create(getter8, storey8), GetterField.create(getter9, storey9), GetterField.create(getter10, storey10)};
    }

    public static void main(String[] args) {

        GetterField[] arr = GetterFields.merge(GetterFields.of(ColumnField::getColumnName), GetterFields.of(ColumnField::getColumnName, ColumnField::getColumnName));
        System.out.println(arr.length);
        System.out.println(arr[arr.length - 1]);
    }
}
