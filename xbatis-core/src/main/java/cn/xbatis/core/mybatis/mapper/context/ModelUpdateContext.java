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

package cn.xbatis.core.mybatis.mapper.context;


import cn.xbatis.core.db.reflect.ModelInfo;
import cn.xbatis.core.mybatis.mapper.context.strategy.UpdateStrategy;
import cn.xbatis.db.Model;

import java.util.Map;

public class ModelUpdateContext<M extends Model> extends SQLCmdUpdateContext {

    public ModelUpdateContext(ModelInfo modelInfo, M model, UpdateStrategy<M> updateStrategy, Map<String, Object> defaultValueContext) {
        super(ModelUpdateCreateUtil.create(modelInfo, model, updateStrategy, defaultValueContext));
    }
}
