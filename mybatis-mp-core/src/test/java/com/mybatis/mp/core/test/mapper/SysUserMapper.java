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

package com.mybatis.mp.core.test.mapper;


import cn.mybatis.mp.core.mybatis.mapper.MybatisMapper;
import com.mybatis.mp.core.test.DO.SysUser;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;

import java.util.List;
import java.util.Map;

public interface SysUserMapper extends MybatisMapper<SysUser> {

    @Select("select * from big_data limit 1000000")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 1000)
    List<Map> selectAll();

    @Select("select * from big_data limit 1000000")
    List<Map> selectAll2();

    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 1000)
    @Select("select * from big_data limit 1000000")
    Cursor<Map> selectAll3();
}
