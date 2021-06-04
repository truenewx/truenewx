package org.truenewx.tnxjee.model.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.parser.FreeMarkerTemplateParser;
import org.truenewx.tnxjee.core.parser.TemplateParser;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;

/**
 * 类生成器支持
 *
 * @author jianglei
 */
public abstract class ClassGeneratorSupport extends ModelBasedGeneratorSupport {

    private String targetBasePackage;
    private TemplateParser templateParser = new FreeMarkerTemplateParser();

    public ClassGeneratorSupport(String modelBasePackage, String targetBasePackage) {
        super(modelBasePackage);
        this.targetBasePackage = targetBasePackage;
    }

    public void setTemplateParser(TemplateParser templateParser) {
        this.templateParser = templateParser;
    }

    protected String getTargetModulePackageName(String module) {
        String packageName = this.targetBasePackage;
        if (StringUtils.isNotBlank(module)) {
            packageName += Strings.DOT + module;
        }
        return packageName;
    }

    protected void generate(String className, String templateLocation, Map<String, Object> params)
            throws IOException {
        if (templateLocation != null) {
            try {
                ClassUtils.forName(className, null);
            } catch (ClassNotFoundException e) {
                File template = new ClassPathResource(templateLocation).getFile();
                String content = this.templateParser.parse(template, params, Locale.getDefault());
                File file = getJavaFile(className);
                FileWriter writer = new FileWriter(file);
                IOUtils.write(content, writer);
                writer.close();
            }
        }
    }

    private File getJavaFile(String className) throws IOException {
        File file = new ClassPathResource("/").getFile(); // classpath根
        file = file.getParentFile().getParentFile(); // 工程根
        String path = "src/main/java/" + className.replaceAll("[.]", Strings.SLASH) + ".java";
        file = new File(file, path);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    protected void putClassName(Map<String, Object> params, Class<?> clazz, String paramNamePrefix) {
        params.put(paramNamePrefix + "ClassSimpleName", clazz.getSimpleName());
        String className = getImportedClassName(clazz);
        if (className != null) {
            params.put(paramNamePrefix + "ClassName", className);
        }
    }

    private String getImportedClassName(Class<?> clazz) {
        if (clazz.isPrimitive() || "java.lang".equals(clazz.getPackageName())) {
            return null;
        }
        return clazz.getName();
    }

    protected Binate<Field, Field> getRelationKeyField(Class<?> relationKeyClass) {
        Field leftField = null;
        Field rightField = null;
        Field[] fields = relationKeyClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                if (leftField == null) {
                    leftField = field;
                } else if (rightField == null) {
                    rightField = field;
                } else {
                    break;
                }
            }
        }
        return new Binary<>(leftField, rightField);
    }
}
