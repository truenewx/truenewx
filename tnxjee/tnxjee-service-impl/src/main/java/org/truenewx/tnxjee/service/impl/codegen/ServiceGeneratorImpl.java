package org.truenewx.tnxjee.service.impl.codegen;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.codegen.ClassGeneratorSupport;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.entity.relation.Relation;
import org.truenewx.tnxjee.model.entity.relation.RelationKey;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;
import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 服务类生成器实现
 *
 * @author jianglei
 */
public class ServiceGeneratorImpl extends ClassGeneratorSupport implements ServiceGenerator {

    private String simpleUnityTemplateLocation = "META-INF/template/simple-unity-service.ftl";
    private String commandUnityTemplateLocation = "META-INF/template/command-unity-service.ftl";
    private String unityImplTemplateLocation = "META-INF/template/unity-service-impl.ftl";

    private String simpleOwnedUnityTemplateLocation = "META-INF/template/simple-owned-unity-service.ftl";
    private String commandOwnedUnityTemplateLocation = "META-INF/template/command-owned-unity-service.ftl";
    private String ownedUnityImplTemplateLocation = "META-INF/template/owned-unity-service-impl.ftl";

    private String simpleRelationTemplateLocation = "META-INF/template/simple-relation-service.ftl";
    private String commandRelationTemplateLocation = "META-INF/template/command-relation-service.ftl";
    private String relationImplTemplateLocation = "META-INF/template/relation-service-impl.ftl";

    private String entityTemplateLocation = "META-INF/template/entity-service.ftl";
    private String entityImplTemplateLocation = "META-INF/template/entity-service-impl.ftl";

    public ServiceGeneratorImpl(String modelBasePackage, String targetBasePackage) {
        super(modelBasePackage, targetBasePackage);
    }

    @Override
    public void generate(String... modules) throws Exception {
        generate(this.modelBasePackage, (module, entityClass) -> {
            try {
                generate(module, entityClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, modules);
    }

    @Override
    public void generate(Class<? extends Entity> entityClass) throws Exception {
        String module = getModule(entityClass);
        generate(module, entityClass);
    }

    private void generate(String module, Class<? extends Entity> entityClass) throws Exception {
        boolean simple = isSimple(entityClass);
        Map<String, Object> params = new HashMap<>();
        if (Unity.class.isAssignableFrom(entityClass)) {
            Class<?> keyClass = ClassUtil.getActualGenericType(entityClass, Unity.class, 0);
            putClassName(params, keyClass, "key");
            if (OwnedUnity.class.isAssignableFrom(entityClass)) { // 从属单体
                Class<?> ownerClass = ClassUtil.getActualGenericType(entityClass, OwnedUnity.class, 1);
                putClassName(params, ownerClass, "owner");
                String location = simple ? this.simpleOwnedUnityTemplateLocation : this.commandOwnedUnityTemplateLocation;
                String serviceInterfaceSimpleName = generate(module, entityClass, params, location, Strings.EMPTY);
                params.put("serviceInterfaceSimpleName", serviceInterfaceSimpleName);
                generate(module, entityClass, params, this.ownedUnityImplTemplateLocation, "Impl");
            } else {
                String location = simple ? this.simpleUnityTemplateLocation : this.commandUnityTemplateLocation;
                String serviceInterfaceSimpleName = generate(module, entityClass, params, location, Strings.EMPTY);
                params.put("serviceInterfaceSimpleName", serviceInterfaceSimpleName);
                generate(module, entityClass, params, this.unityImplTemplateLocation, "Impl");
            }
        } else if (Relation.class.isAssignableFrom(entityClass)) { // 关系实体
            Class<?> leftKeyClass = ClassUtil.getActualGenericType(entityClass, Relation.class, 0);
            putClassName(params, leftKeyClass, "leftKey");
            Class<?> rightKeyClass = ClassUtil.getActualGenericType(entityClass, Relation.class, 1);
            putClassName(params, rightKeyClass, "rightKey");
            String location = simple ? this.simpleRelationTemplateLocation : this.commandRelationTemplateLocation;
            String serviceInterfaceSimpleName = generate(module, entityClass, params, location, Strings.EMPTY);
            params.put("serviceInterfaceSimpleName", serviceInterfaceSimpleName);
            generate(module, entityClass, params, this.relationImplTemplateLocation, "Impl");
        } else { // 既不是单体也不是关系的普通实体
            String serviceInterfaceSimpleName = generate(module, entityClass, params, this.entityTemplateLocation,
                    Strings.EMPTY);
            params.put("serviceInterfaceSimpleName", serviceInterfaceSimpleName);
            generate(module, entityClass, params, this.entityImplTemplateLocation, "Impl");
        }
    }

    private boolean isSimple(Class<? extends Entity> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Class<?> fieldType = field.getType();
                // 有一个字段类型不是关系主键，但是复合类型或集合，则不能为简单模式
                if (!RelationKey.class.isAssignableFrom(fieldType)
                        && (ClassUtil.isComplex(fieldType) || ClassUtil.isAggregation(fieldType))) {
                    return false;
                }
            }
        }
        return true;
    }

    private String generate(String module, Class<? extends Entity> entityClass, Map<String, Object> params,
            String location, String serviceClassNameSuffix) throws IOException {
        String packageName = getTargetModulePackageName(module);
        String entityClassSimpleName = entityClass.getSimpleName();
        String serviceClassSimpleName = entityClassSimpleName + "Service" + serviceClassNameSuffix;
        String serviceClassName = packageName + Strings.DOT + serviceClassSimpleName;
        params.put("packageName", packageName);
        params.put("entityClassName", entityClass.getName());
        params.put("serviceClassSimpleName", serviceClassSimpleName);
        params.put("entityClassSimpleName", entityClassSimpleName);
        String repoClassSimpleName = entityClassSimpleName + "Repo";
        params.put("repoClassSimpleName", repoClassSimpleName);
        String repoPackageName;
        if (module == null) {
            repoPackageName = packageName.replaceFirst("\\.service", ".repo");
        } else {
            repoPackageName = packageName.replaceFirst("\\.service\\.", ".repo.");
        }
        String repoClassName = repoPackageName + Strings.DOT + repoClassSimpleName;
        params.put("repoClassName", repoClassName);
        generate(serviceClassName, location, params);
        return serviceClassSimpleName;
    }

}
