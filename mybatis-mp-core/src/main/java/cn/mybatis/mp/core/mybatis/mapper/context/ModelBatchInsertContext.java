package cn.mybatis.mp.core.mybatis.mapper.context;

import cn.mybatis.mp.core.MybatisMpConfig;
import cn.mybatis.mp.core.db.reflect.ModelFieldInfo;
import cn.mybatis.mp.core.db.reflect.ModelInfo;
import cn.mybatis.mp.core.db.reflect.TableIds;
import cn.mybatis.mp.core.incrementer.IdentifierGenerator;
import cn.mybatis.mp.core.incrementer.IdentifierGeneratorFactory;
import cn.mybatis.mp.core.sql.executor.BaseInsert;
import cn.mybatis.mp.core.sql.executor.Insert;
import cn.mybatis.mp.core.tenant.TenantUtil;
import cn.mybatis.mp.core.util.ModelInfoUtil;
import cn.mybatis.mp.core.util.StringPool;
import cn.mybatis.mp.core.util.TypeConvertUtil;
import cn.mybatis.mp.db.IdAutoType;
import cn.mybatis.mp.db.Model;
import cn.mybatis.mp.db.annotations.TableField;
import cn.mybatis.mp.db.annotations.TableId;
import db.sql.api.DbType;
import db.sql.api.impl.cmd.Methods;
import db.sql.api.impl.cmd.basic.NULL;
import db.sql.api.impl.cmd.basic.Table;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import java.util.*;
import java.util.stream.Collectors;

public class ModelBatchInsertContext<M extends Model> extends SQLCmdInsertContext<BaseInsert> implements SetIdMethod {

    private final Model<?>[] insertDatas;

    private final Set<String> saveFieldSet;

    private final ModelInfo modelInfo;

    private final boolean idHasValue;

    public ModelBatchInsertContext(ModelInfo modelInfo, Collection<M> list, Set<String> saveFieldSet) {
        this.modelInfo = modelInfo;
        this.insertDatas = list.toArray(new Model[0]);
        this.saveFieldSet = saveFieldSet;
        this.entityType = modelInfo.getEntityType();
        this.idHasValue = IdUtil.isIdExists(this.insertDatas[0], modelInfo.getIdFieldInfo());
    }

    private static Insert createCmd(ModelInfo modelInfo, Model<?>[] insertDatas, Set<String> saveFieldSet, DbType dbType) {
        Insert insert = new Insert();
        Class<?> entityType = modelInfo.getEntityType();
        insert.$().cacheTableInfo(modelInfo.getTableInfo());
        Table table = insert.$().table(modelInfo.getTableInfo().getSchemaAndTableName());
        insert.insert(table);


        List<ModelFieldInfo> saveFieldInfoSet = saveFieldSet.stream().map(modelInfo::getFieldInfo).collect(Collectors.toList());

        TableId tableId = null;

        //拼上主键
        if (!modelInfo.getIdFieldInfos().isEmpty()) {
            tableId = TableIds.get(entityType, dbType);
            if (tableId.value() == IdAutoType.GENERATOR) {
                modelInfo.getIdFieldInfos().forEach(item -> {
                    if (!saveFieldInfoSet.contains(item)) {
                        saveFieldInfoSet.add(item);
                    }
                });
            }
        }

        //拼上租户ID
        if (Objects.nonNull(modelInfo.getTenantIdFieldInfo())) {
            if (!saveFieldInfoSet.contains(modelInfo.getTenantIdFieldInfo())) {
                saveFieldInfoSet.add(modelInfo.getTenantIdFieldInfo());
            }
        }

        //拼上乐观锁
        if (Objects.nonNull(modelInfo.getVersionFieldInfo())) {
            if (!saveFieldInfoSet.contains(modelInfo.getVersionFieldInfo())) {
                saveFieldInfoSet.add(modelInfo.getVersionFieldInfo());
            }
        }

        //拼上逻辑删除
        if (Objects.nonNull(modelInfo.getLogicDeleteFieldInfo())) {
            if (!saveFieldInfoSet.contains(modelInfo.getLogicDeleteFieldInfo())) {
                saveFieldInfoSet.add(modelInfo.getLogicDeleteFieldInfo());
            }
        }

        //设置insert 列
        for (ModelFieldInfo modelFieldInfo : saveFieldInfoSet) {
            insert.fields(insert.$().field(table, modelFieldInfo.getTableFieldInfo().getColumnName()));
        }

        int fieldSize = saveFieldInfoSet.size();

        for (Object t : insertDatas) {
            List<Object> values = new ArrayList<>();
            for (int i = 0; i < fieldSize; i++) {
                ModelFieldInfo modelFieldInfo = saveFieldInfoSet.get(i);
                Object value = modelFieldInfo.getValue(t);
                boolean hasValue = (!modelFieldInfo.getTableFieldInfo().isTableId() && Objects.nonNull(value))
                        || (modelFieldInfo.getTableFieldInfo().isTableId() && IdUtil.isIdValueExists(value));
                if (!hasValue) {
                    if (modelFieldInfo.getTableFieldInfo().isTableId()) {
                        if (tableId.value() == IdAutoType.GENERATOR) {
                            IdentifierGenerator identifierGenerator = IdentifierGeneratorFactory.getIdentifierGenerator(tableId.generatorName());
                            Object id = identifierGenerator.nextId(modelInfo.getTableInfo().getType());
                            if (IdUtil.setId(t, modelFieldInfo, id)) {
                                value = id;
                            }
                        } else {
                            throw new RuntimeException(modelFieldInfo.getField().getName() + " has no value");
                        }
                    } else if (modelFieldInfo.getTableFieldInfo().isTenantId()) {
                        value = TenantUtil.setTenantId(t);
                    } else if (!StringPool.EMPTY.equals(modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue())) {
                        //设置默认值
                        value = MybatisMpConfig.getDefaultValue(modelFieldInfo.getFieldInfo().getTypeClass(), modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation().defaultValue());

                        //默认值回写
                        ModelInfoUtil.setValue(modelFieldInfo, t, value);
                    } else if (modelFieldInfo.getTableFieldInfo().isVersion()) {
                        //乐观锁设置 默认值1
                        value = TypeConvertUtil.convert(Integer.valueOf(1), modelFieldInfo.getField().getType());
                        //乐观锁回写
                        ModelInfoUtil.setValue(modelFieldInfo, t, value);
                    }
                }

                TableField tableField = modelFieldInfo.getTableFieldInfo().getTableFieldAnnotation();
                if (Objects.isNull(value)) {
                    values.add(NULL.NULL);
                } else {
                    MybatisParameter mybatisParameter = new MybatisParameter(value, tableField.typeHandler(), tableField.jdbcType());
                    values.add(Methods.value(mybatisParameter));
                }
            }
            insert.values(values);
        }
        return insert;
    }


    @Override
    public void init(DbType dbType) {
        super.init(dbType);
        if (Objects.isNull(this.execution)) {
            this.execution = createCmd(this.modelInfo, this.insertDatas, this.saveFieldSet, dbType);
        }
    }

    @Override
    public void setId(Object id, int index) {
        IdUtil.setId(this.insertDatas[index], this.modelInfo.getSingleIdFieldInfo(true), id);
    }

    @Override
    public boolean idHasValue() {
        return idHasValue;
    }

    @Override
    public int getInsertSize() {
        return this.insertDatas.length;
    }

    @Override
    public TypeHandler<?> getIdTypeHandler(Configuration configuration) {
        if (Objects.nonNull(this.modelInfo.getIdFieldInfo())) {
            TypeHandler typeHandler = this.modelInfo.getIdFieldInfo().getTableFieldInfo().getTypeHandler();
            if (Objects.isNull(typeHandler)) {
                return configuration.getTypeHandlerRegistry().getTypeHandler(this.modelInfo.getIdFieldInfo().getFieldInfo().getTypeClass());
            }
        }
        return null;
    }
}
