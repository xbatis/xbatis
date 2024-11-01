package cn.mybatis.mp.core.db.reflect;

import cn.mybatis.mp.core.util.TypeConvertUtil;
import cn.mybatis.mp.db.annotations.PutValue;
import lombok.Getter;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;

@Getter
public class PutValueInfo {

    private final Field field;

    private final FieldInfo fieldInfo;

    private final PutValue annotation;

    private final String[] valuesColumn;

    private final TypeHandler<?>[] valuesTypeHandler;

    private final SetFieldInvoker writeFieldInvoker;

    private final Class<?> factory;

    private final Object defaultValue;

    public PutValueInfo(Class clazz, Field field, PutValue annotation, String[] valuesColumn, TypeHandler<?>[] valuesTypeHandler, Class<?> factory) {
        this.field = field;
        this.fieldInfo = new FieldInfo(clazz, field);
        this.annotation = annotation;
        this.valuesColumn = valuesColumn;
        this.valuesTypeHandler = valuesTypeHandler;
        this.writeFieldInvoker = new SetFieldInvoker(field);
        ;
        this.factory = factory;
        if (!annotation.defaultValue().isEmpty()) {
            this.defaultValue = TypeConvertUtil.convert(annotation.defaultValue(), this.fieldInfo.getTypeClass());
        } else {
            this.defaultValue = null;
        }
    }
}
