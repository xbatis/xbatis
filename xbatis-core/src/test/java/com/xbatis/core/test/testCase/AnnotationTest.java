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

package com.xbatis.core.test.testCase;

import cn.xbatis.core.db.reflect.AnnotationUtil;
import cn.xbatis.core.db.reflect.TableFieldInfo;
import cn.xbatis.core.util.TableInfoUtil;
import cn.xbatis.db.annotations.ResultEntityField;
import cn.xbatis.db.annotations.Table;
import com.xbatis.core.test.DO.FetchAddr;
import com.xbatis.core.test.vo.FetchAddrVo;
import db.sql.api.DbType;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationTest extends BaseTest {

    @Test
    public void testSubClassMethodAnnotation() {
        Class clazz = FetchAddrVo.class;
        Field field;
        try {
            field = FetchAddr.class.getDeclaredField("addrs1");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        Class<ResultEntityField> annotationType = ResultEntityField.class;
        Map<Class<? extends Annotation>, Annotation> annotationMap = AnnotationUtil.getAnnotations(clazz, field, String.class, annotationType);

        ResultEntityField resultEntityField = (ResultEntityField) annotationMap.get(ResultEntityField.class);
        assertEquals("addrs1", resultEntityField.property());
    }

    @Test
    public void testTableIdAnnotation() {
        Class clazz = FetchAddrVo.class;
        Field field;
        try {
            field = FetchAddr.class.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        TableFieldInfo tableFieldInfo = new TableFieldInfo(clazz, FetchAddr.class.getAnnotation(Table.class), field);

        assertEquals(DbType.H2.getName(), TableInfoUtil.getTableIdAnnotation(tableFieldInfo, DbType.H2).dbType());
    }
}
