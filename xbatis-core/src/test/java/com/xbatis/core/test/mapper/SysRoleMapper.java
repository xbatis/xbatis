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

package com.xbatis.core.test.mapper;

import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.mybatis.mapper.context.Pager;
import cn.xbatis.core.sql.executor.Query;
import cn.xbatis.db.annotations.Paging;
import com.xbatis.core.test.DO.ReqEntity;
import com.xbatis.core.test.DO.SysRole;
import com.xbatis.core.test.vo.JsonTypeTestVo;
import com.xbatis.core.test.vo.XmlNestedResultMap;
import db.sql.api.impl.cmd.struct.Where;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SysRoleMapper extends MybatisMapper<SysRole> {
    @Paging(optimize = false)
    Pager<SysRole> xmlPaging(Pager<SysRole> pager, @Param("id") Integer id, @Param("id2") Integer id2);

    @Paging
    Pager<SysRole> xmlPaging2(Pager<SysRole> pager);

    @Paging
    Pager<SysRole> xmlPaging3(Pager<SysRole> pager, @Param("id") Integer id);

    @Paging
    Pager<SysRole> xmlDynamicPaging(Pager<SysRole> pager, @Param("id") Integer id, @Param("id2") Integer id2, @Param("id3") Integer id3);

    @Paging
    @Select("select * from sys_role where id >=#{id} and id <=#{id2} order by id asc")
    Pager<SysRole> annotationPaging(Pager<SysRole> pager, @Param("id") Integer id, @Param("id2") Integer id2);


    JsonTypeTestVo jsonTypeTest1(@Param("sql") String sql);

    JsonTypeTestVo jsonTypeTest2(@Param("sql") String sql);

    List<SysRole> selectCustomSql(@Param("WHERE") Where where);

    List<SysRole> selectCustomSql2(Where where);

    List<SysRole> selectQueryCustomSql(Query<?> query);

    List<SysRole> selectQueryCustomSql2(@Param("query") Query<?> query, int xx);

    List<SysRole> selectQueryCustomSql3(Query<?> query);

    List<XmlNestedResultMap> selectXmlNestedResultMap();

    List<Map> testSuperMapParams(ReqEntity reqEntity);
}
