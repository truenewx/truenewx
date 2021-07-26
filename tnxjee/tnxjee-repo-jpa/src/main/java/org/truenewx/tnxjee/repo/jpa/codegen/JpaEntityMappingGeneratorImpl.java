package org.truenewx.tnxjee.repo.jpa.codegen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.message.MessageResolver;
import org.truenewx.tnxjee.core.spec.HttpRequestMethod;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.core.util.XmlUtil;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.ValueModel;
import org.truenewx.tnxjee.model.codegen.ModelBasedGeneratorSupport;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.entity.relation.RelationKey;
import org.truenewx.tnxjee.model.spec.enums.Ethnicity;
import org.truenewx.tnxjee.model.spec.user.DefaultUserIdentity;
import org.truenewx.tnxjee.repo.jpa.converter.IntArrayAttributeConverter;
import org.truenewx.tnxjee.repo.jpa.converter.LongArrayAttributeConverter;
import org.truenewx.tnxjee.repo.jpa.converter.MapToJsonAttributeConverter;
import org.truenewx.tnxjee.repo.jpa.converter.StringArrayAttributeConverter;
import org.truenewx.tnxjee.repo.jpa.converter.spec.DefaultUserIdentityAttributeConverter;
import org.truenewx.tnxjee.repo.jpa.converter.spec.EthnicityConverter;
import org.truenewx.tnxjee.repo.jpa.converter.spec.HttpRequestMethodAttributeConverter;

/**
 * JPA实体映射文件生成器实现
 *
 * @author jianglei
 */
public class JpaEntityMappingGeneratorImpl extends ModelBasedGeneratorSupport implements JpaEntityMappingGenerator {

    private static final String INFO_COLUMN_NOT_EXISTS = "info.tnxjee.repo.jpa.codegen.column_not_exists";
    private static final String INFO_CONFIRM_MAPS_ID = "info.tnxjee.repo.jpa.codegen.confirm_maps_id";
    private static final String INFO_UNSUPPORTED_PROPERTY = "info.tnxjee.repo.jpa.codegen.unsupported_property";

    @Autowired
    private MessageResolver messageResolver;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JpaEnumConverterGenerator enumConverterGenerator;

    private String templateLocation = "META-INF/template/entity-mapping.xml";
    private String targetLocation = "META-INF/jpa/";

    private final Logger logger = LogUtil.getLogger(getClass());

    public JpaEntityMappingGeneratorImpl(String modelBasePackage) {
        super(modelBasePackage);
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setTargetLocation(String targetLocation) {
        // 确保以/结尾
        if (!targetLocation.endsWith(Strings.SLASH)) {
            targetLocation += Strings.SLASH;
        }
        this.targetLocation = targetLocation;
    }

    @Override
    public void generate(String... modules) throws Exception {
        generate(this.modelBasePackage, (module, entityClass) -> {
            try {
                String tableName = "t_" + StringUtil.prependUnderLineToUpperChar(entityClass.getSimpleName(), true);
                generate(module, entityClass, tableName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, modules);
    }

    @Override
    public void generate(Class<? extends Entity> entityClass, String tableName) throws Exception {
        String module = getModule(entityClass);
        generate(module, entityClass, tableName);
    }

    private void generate(String module, Class<? extends Entity> entityClass, String tableName) throws Exception {
        ClassPathResource resource = getMappingResource(module, entityClass);
        if (resource != null) {
            Connection connection = DataSourceUtils.getConnection(this.dataSource);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, tableName, new String[]{ "TABLE" });
            if (!rs.next()) {
                this.logger.warn("====== Table {} does not exist, entity mapping file for {} can not been generated.",
                        tableName, entityClass.getName());
                return;
            }

            Document doc = readTemplate();
            Element rootElement = doc.getRootElement();
            Element entityElement = rootElement.addElement("entity")
                    .addAttribute("class", entityClass.getName());
            entityElement.addElement("table").addAttribute("name", tableName);
            Element attributesElement = entityElement.addElement("attributes");

            // JPA实体类仅声明字段有效，父类字段无效
            List<Field> refFields = new ArrayList<>();
            Field[] fields = entityClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (supports(field)) {
                    Class<?> fieldType = field.getType();
                    if (i == 0) { // 首个字段视为主键
                        if (RelationKey.class.isAssignableFrom(fieldType)) {
                            if (!fieldType.isMemberClass()) { // 主键类型不是内部类，则需要生成主键类型的映射文件
                                generateEmbeddableMapping(fieldType);
                            }
                            Element idElement = attributesElement.addElement("embedded-id")
                                    .addAttribute("name", field.getName());
                            addEmbeddedAttributeElements(metaData, tableName, idElement, field, false);
                        } else {
                            Element idElement = attributesElement.addElement("id")
                                    .addAttribute("name", field.getName());
                            JpaEntityColumn column = getColumn(metaData, tableName, field, null);
                            addColumnElement(idElement, column, false);
                            if (Boolean.TRUE.equals(column.getAutoIncrement())) {
                                idElement.addElement("generated-value").addAttribute("strategy", "IDENTITY");
                            }
                        }
                    } else {
                        Binate<Boolean, String> basicConverter = getBasicConverter(fieldType);
                        if (basicConverter.getLeft()) {
                            Element basicElement = attributesElement.addElement("basic").addAttribute("name",
                                    field.getName());
                            JpaEntityColumn column = getColumn(metaData, tableName, field, null);
                            addColumnElement(basicElement, column, true);
                            addConvertElement(basicElement, basicConverter.getRight());
                        } else { // 引用字段暂存，在所有简单字段类型生成后，再最后生成关联关系配置，以符合映射文件xml约束
                            refFields.add(field);
                        }
                    }
                }
            }
            for (Field field : refFields) {
                addRefElement(metaData, tableName, attributesElement, field);
            }

            File file = getSourceFile(resource);
            XmlUtil.write(doc, file);
            connection.close();
        }
    }

    private void addConvertElement(Element parentElement, String converter) {
        if (converter != null) {
            parentElement.addElement("convert").addAttribute("converter", converter);
        }
    }

    private ClassPathResource getMappingResource(String module, Class<?> clazz) {
        ClassPathResource dir = new ClassPathResource(this.targetLocation);
        if (module != null) {
            dir = (ClassPathResource) dir.createRelative(module + Strings.SLASH);
        }
        String filename = clazz.getSimpleName() + ".xml";
        ClassPathResource resource = (ClassPathResource) dir.createRelative(filename);
        if (resource.exists()) {
            this.logger.info("====== Entity mapping file for {} already exists, which is ignored.",
                    clazz.getName());
            return null;
        }
        return resource;
    }

    private Document readTemplate() throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        ClassPathResource template = new ClassPathResource(this.templateLocation);
        InputStream in = template.getInputStream();
        Document doc = reader.read(in);
        in.close();
        return doc;
    }

    private boolean supports(Field field) {
        return !Modifier.isStatic(field.getModifiers());
    }

    private void addEmbeddedAttributeElements(DatabaseMetaData metaData, String tableName, Element parentElement,
            Field field, boolean notId) throws Exception {
        String columnNamePrefix = null;
        if (notId) {
            columnNamePrefix = StringUtil.prependUnderLineToUpperChar(field.getName(), true) + Strings.UNDERLINE;
        }
        Field[] valueFields = field.getType().getDeclaredFields();
        for (Field valueField : valueFields) {
            if (supports(valueField)) {
                Element attributeElement = parentElement.addElement("attribute-override")
                        .addAttribute("name", valueField.getName());
                JpaEntityColumn column = getColumn(metaData, tableName, valueField, columnNamePrefix);
                addColumnElement(attributeElement, column, notId);
            }
        }
    }

    private JpaEntityColumn getColumn(DatabaseMetaData metaData, String tableName, Field field, String columnNamePrefix)
            throws SQLException {
        String columnName = StringUtil.prependUnderLineToUpperChar(field.getName(), true);
        if (columnNamePrefix != null) {
            columnName = columnNamePrefix + columnName;
        }
        JpaEntityColumn column = new JpaEntityColumn(columnName);
        ResultSet rs = metaData.getColumns(null, null, tableName, columnName);
        if (rs.next()) {
            Class<?> fieldType = field.getType();
            column.setAutoIncrement(getBoolean(rs.getString("IS_AUTOINCREMENT")));
            if (!fieldType.isPrimitive()) {
                column.setNullable(getBoolean(rs.getString("IS_NULLABLE")));
            }
            if (fieldType == String.class) { // 字段类型为字符串，则需获取其长度
                column.setLength(rs.getInt("COLUMN_SIZE"));
            }
            column.setDefinition(getColumnDefinition(fieldType, rs.getInt("DATA_TYPE")));
            if (fieldType == BigDecimal.class || fieldType == double.class || fieldType == float.class) { // 小数需获取精度
                column.setPrecision(rs.getInt("COLUMN_SIZE"));
                column.setScale(rs.getInt("DECIMAL_DIGITS"));
            }
        } else {
            column.setExists(false);
        }
        return column;
    }

    private Boolean getBoolean(String value) {
        if ("yes".equalsIgnoreCase(value)) {
            return true;
        } else if ("no".equalsIgnoreCase(value)) {
            return false;
        } else {
            return null;
        }
    }

    private String getColumnDefinition(Class<?> fieldType, int dataType) {
        switch (dataType) {
            case Types.CHAR:
                return "char";
            case Types.DATE:
                return "date";
            case Types.TIME:
                if (fieldType == Instant.class || fieldType == LocalDateTime.class) {
                    return null;
                }
                return "time";
            default:
                return null;
        }
    }

    private void addColumnElement(Element parentElement, JpaEntityColumn column, boolean withNullable) {
        if (!column.isExists()) {
            addComment(parentElement, INFO_COLUMN_NOT_EXISTS, column.getName());
        }
        Element columnElement = parentElement.addElement("column").addAttribute("name", column.getName());
        if (withNullable && column.getNullable() != null) {
            columnElement.addAttribute("nullable", column.getNullable().toString());
        }
        if (column.getLength() != null) {
            columnElement.addAttribute("length", column.getLength().toString());
        }
        if (column.getDefinition() != null) {
            columnElement.addAttribute("column-definition", column.getDefinition());
        }
        if (column.getPrecision() != null) {
            columnElement.addAttribute("precision", column.getPrecision().toString());
        }
        if (column.getScale() != null) {
            columnElement.addAttribute("scale", column.getScale().toString());
        }
    }

    private void addComment(Element element, String messageCode, Object... args) {
        element.addComment(this.messageResolver.resolveMessage(messageCode, args));
    }

    private Binate<Boolean, String> getBasicConverter(Class<?> fieldType) throws Exception {
        String converter = null;
        boolean basic = BeanUtils.isSimpleValueType(fieldType);
        // 不是基础类型或者是枚举类型，则尝试获取转换器，可取得转换器，则说明也是基础类型
        if (!basic || fieldType.isEnum()) {
            converter = getConverter(fieldType);
            if (converter != null) {
                basic = true;
            }
        }
        return new Binary<>(basic, converter);
    }

    private String getConverter(Class<?> fieldType) throws Exception {
        if (fieldType == Ethnicity.class) {
            return EthnicityConverter.class.getName();
        }
        if (fieldType == HttpRequestMethod.class) {
            return HttpRequestMethodAttributeConverter.class.getName();
        }
        if (fieldType == DefaultUserIdentity.class) {
            return DefaultUserIdentityAttributeConverter.class.getName();
        }
        if (fieldType == Map.class) {
            return MapToJsonAttributeConverter.class.getName();
        }
        if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            if (componentType == String.class) {
                return StringArrayAttributeConverter.class.getName();
            }
            if (componentType == int.class) {
                return IntArrayAttributeConverter.class.getName();
            }
            if (componentType == long.class) {
                return LongArrayAttributeConverter.class.getName();
            }
        }
        return this.enumConverterGenerator.generate(fieldType);
    }

    private void addRefElement(DatabaseMetaData metaData, String tableName, Element attributesElement, Field field)
            throws Exception {
        String propertyName = field.getName();
        Class<?> fieldType = field.getType();
        if (ClassUtil.isAggregation(fieldType)) { // 集合类型字段，为一对多或多对多引用
            // 暂时不支持，提醒手工配置
            addUnsupportedPropertyComment(attributesElement, propertyName);
        } else { // 单个引用字段，为一对一或多对一引用
            if (Entity.class.isAssignableFrom(fieldType)) { // 实体类型生成引用配置
                String columnName = StringUtil.prependUnderLineToUpperChar(propertyName, true) + "_id";
                ResultSet rs = metaData.getColumns(null, null, tableName, columnName);
                if (rs.next()) { // 存在外键字段，为多对一引用
                    Element refElement = attributesElement.addElement("many-to-one")
                            .addAttribute("name", propertyName);
                    Boolean optional = getBoolean(rs.getString("IS_NULLABLE"));
                    if (optional != null) {
                        refElement.addAttribute("optional", optional.toString());
                    }
                    String mapsId = getMapsId(attributesElement, field);
                    if (mapsId != null) {
                        refElement.addAttribute("maps-id", mapsId);
                        addComment(refElement, INFO_CONFIRM_MAPS_ID);
                    }
                    refElement.addElement("join-column").addAttribute("name", columnName);
                } else { // 不存在外键字段，为一对一引用
                    Element refElement = attributesElement.addElement("one-to-one")
                            .addAttribute("name", propertyName)
                            .addAttribute("optional", Boolean.FALSE.toString()); // 一对一映射默认不能为空
                    refElement.addElement("primary-key-join-column");
                }
            } else if (ValueModel.class.isAssignableFrom(fieldType)) { // 值模型生成内嵌配置
                generateEmbeddableMapping(fieldType);
                Element embeddedElement = attributesElement.addElement("embedded")
                        .addAttribute("name", propertyName);
                addEmbeddedAttributeElements(metaData, tableName, embeddedElement, field, true);
            } else { // 其它自定义类型提醒手工设置
                addUnsupportedPropertyComment(attributesElement, propertyName);
            }
        }
    }

    private void addUnsupportedPropertyComment(Element element, String propertyName) {
        addComment(element, INFO_UNSUPPORTED_PROPERTY, propertyName);
    }

    private String getMapsId(Element attributesElement, Field field) throws Exception {
        Element embeddedIdElement = attributesElement.element("embedded-id");
        if (embeddedIdElement != null) {
            String idPropertyName = embeddedIdElement.attributeValue("name");
            Class<?> entityClass = field.getDeclaringClass();
            Field idField = entityClass.getDeclaredField(idPropertyName);
            Class<?> idFieldType = idField.getType();
            Field mapsIdField = ClassUtil.findField(idFieldType, field.getName() + "Id");
            if (mapsIdField != null) {
                return mapsIdField.getName();
            }
        }
        return null;
    }

    private void generateEmbeddableMapping(Class<?> embeddableClass) throws Exception {
        String module = getModule(embeddableClass);
        ClassPathResource resource = getMappingResource(module, embeddableClass);
        if (resource != null) {
            Document doc = readTemplate();
            Element rootElement = doc.getRootElement();
            Element entityElement = rootElement.addElement("embeddable")
                    .addAttribute("class", embeddableClass.getName());
            Element attributesElement = entityElement.addElement("attributes");

            Field[] fields = embeddableClass.getDeclaredFields();
            for (Field field : fields) {
                if (supports(field)) {
                    Class<?> fieldType = field.getType();
                    Binate<Boolean, String> basicConverter = getBasicConverter(fieldType);
                    if (basicConverter.getLeft()) { // 值模型暂时只支持基础类型的属性映射
                        String converter = basicConverter.getRight();
                        if (converter != null) { // 内嵌类型映射只需生成属性转换器配置
                            Element basicElement = attributesElement.addElement("basic").addAttribute("name",
                                    field.getName());
                            addConvertElement(basicElement, converter);
                        }
                    }
                }
            }
            // 内嵌类型映射只在包含有效的属性配置时，才生成映射文件
            if (attributesElement.elements().size() > 0) {
                File file = getSourceFile(resource);
                XmlUtil.write(doc, file);
            }
        }
    }

    private File getSourceFile(ClassPathResource resource) throws IOException {
        File root = new ClassPathResource("/").getFile(); // classpath根
        root = root.getParentFile().getParentFile(); // 工程根
        return new File(root, "src/main/resources/" + resource.getPath());
    }

}
