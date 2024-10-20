package cn.mybatis.mp.generator.template;

import cn.mybatis.mp.generator.config.GeneratorConfig;
import cn.mybatis.mp.generator.database.meta.EntityInfo;
import cn.mybatis.mp.generator.util.PathUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MapperXmlTemplateBuilder extends AbstractTemplateBuilder {

    public MapperXmlTemplateBuilder(GeneratorConfig generatorConfig, EntityInfo entityInfo) {
        super(generatorConfig, entityInfo);
    }

    @Override
    public boolean enable() {
        return generatorConfig.getMapperXmlConfig().isEnable();
    }

    @Override
    public String targetFilePath() {
        return PathUtils.buildFilePath(
                generatorConfig.getBaseFilePath(),
                generatorConfig.getResourcePath(),
                generatorConfig.getMapperXmlConfig().getPackageName().replaceAll("\\.", File.separator),
                entityInfo.getName())
                + ".xml";
    }

    @Override
    public String templateFilePath() {
        return generatorConfig.getTemplateRootPath() + "/mapper.xml";
    }

    @Override
    public Map<String, Object> contextData() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", LocalDate.now().toString());
        data.put("author", generatorConfig.getAuthor());
        data.put("entityInfo", entityInfo);
        data.put("entityConfig", generatorConfig.getEntityConfig());
        data.put("mapperXmlConfig", generatorConfig.getMapperXmlConfig());
        data.put("mapperConfig", generatorConfig.getMapperConfig());
        data.put("daoConfig", generatorConfig.getDaoConfig());
        data.put("daoImplConfig", generatorConfig.getDaoImplConfig());
        data.put("serviceConfig", generatorConfig.getServiceConfig());
        data.put("serviceImplConfig", generatorConfig.getServiceImplConfig());
        data.put("generatorConfig", generatorConfig);
        data.put("containerType", generatorConfig.getContainerType());
        return data;
    }
}
