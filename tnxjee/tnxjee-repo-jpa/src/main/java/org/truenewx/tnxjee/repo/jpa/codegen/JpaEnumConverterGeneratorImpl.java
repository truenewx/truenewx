package org.truenewx.tnxjee.repo.jpa.codegen;

import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.codegen.ClassGeneratorSupport;

/**
 * JPA枚举转换器生成器实现
 *
 * @author jianglei
 */
public class JpaEnumConverterGeneratorImpl extends ClassGeneratorSupport implements JpaEnumConverterGenerator {

    private String templateLocation = "META-INF/template/enum-converter.ftl";
    private String arrayTemplateLocation = "META-INF/template/enum-array-converter.ftl";

    public JpaEnumConverterGeneratorImpl(String modelBasePackage, String targetBasePackage) {
        super(modelBasePackage, targetBasePackage);
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setArrayTemplateLocation(String arrayTemplateLocation) {
        this.arrayTemplateLocation = arrayTemplateLocation;
    }

    @Override
    public String generate(Class<?> enumClass) throws Exception {
        boolean ifArray = enumClass.isArray();
        if (ifArray) {
            enumClass = enumClass.getComponentType();
        }
        if (enumClass.isEnum()) {
            String module = getModule(enumClass);
            String packageName = getTargetModulePackageName(module) + ".converter";
            String enumClassSimpleName = enumClass.getSimpleName();
            String converterName = enumClassSimpleName + (ifArray ? "ArrayConverter" : "Converter");
            String converterClassName = packageName + Strings.DOT + converterName;
            String location = ifArray ? this.arrayTemplateLocation : this.templateLocation;
            Map<String, Object> params = new HashMap<>();
            params.put("packageName", packageName);
            params.put("enumClassName", enumClass.getName());
            params.put("enumClassSimpleName", enumClassSimpleName);
            params.put("converterName", converterName);
            generate(converterClassName, location, params);
            return converterClassName;
        }
        return null;
    }

}
