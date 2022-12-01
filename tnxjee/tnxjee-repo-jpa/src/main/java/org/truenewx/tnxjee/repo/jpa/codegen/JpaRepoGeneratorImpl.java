package org.truenewx.tnxjee.repo.jpa.codegen;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.codegen.ClassGeneratorSupport;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.entity.relation.Relation;
import org.truenewx.tnxjee.model.entity.relation.RelationKey;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;
import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * JPA Repo类生成器实现
 *
 * @author jianglei
 */
public class JpaRepoGeneratorImpl extends ClassGeneratorSupport implements JpaRepoGenerator {

    private String unityBaseTemplateLocation = "META-INF/template/unity-repo.ftl";
    private String unityExtTemplateLocation = "META-INF/template/unity-repox.ftl";
    private String unityImplTemplateLocation = "META-INF/template/unity-repo-impl.ftl";

    private String ownedUnityBaseTemplateLocation = "META-INF/template/owned-unity-repo.ftl";
    private String ownedUnityExtTemplateLocation = "META-INF/template/owned-unity-repox.ftl";
    private String ownedUnityImplTemplateLocation = "META-INF/template/owned-unity-repo-impl.ftl";

    private String relationBaseTemplateLocation = "META-INF/template/relation-repo.ftl";
    private String relationExtTemplateLocation = "META-INF/template/relation-repox.ftl";
    private String relationImplTemplateLocation = "META-INF/template/relation-repo-impl.ftl";

    private Class<?>[] ignoredEntityClasses;

    public JpaRepoGeneratorImpl(String modelBasePackage, String targetBasePackage, Class<?>[] ignoredEntityClasses) {
        super(modelBasePackage, targetBasePackage);
        this.ignoredEntityClasses = ignoredEntityClasses;
    }

    @Override
    public void generate(String... modules) throws Exception {
        generate(this.modelBasePackage, (module, entityClass) -> {
            if (this.ignoredEntityClasses == null || !ArrayUtils.contains(this.ignoredEntityClasses, entityClass)) {
                try {
                    generate(module, entityClass, true); // 默认均生成实现类，多余的可手工删除
                } catch (Exception e) {
                    throw ExceptionUtil.toRuntimeException(e);
                }
            }
        }, modules);
    }

    @Override
    public void generate(Class<? extends Entity> entityClass, boolean withImpl) throws Exception {
        String module = getModule(entityClass);
        generate(module, entityClass, withImpl);
    }

    private void generate(String module, Class<? extends Entity> entityClass, boolean withImpl) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (Unity.class.isAssignableFrom(entityClass)) {
            Class<?> keyClass = ClassUtil.getActualGenericType(entityClass, Unity.class, 0);
            putClassName(params, keyClass, "key");
            if (OwnedUnity.class.isAssignableFrom(entityClass)) { // 从属单体一定需要生成实现类
                Class<?> ownerClass = ClassUtil.getActualGenericType(entityClass, OwnedUnity.class, 1);
                putClassName(params, ownerClass, "owner");
                String repoxClassSimpleName = generate(module, entityClass, params, this.ownedUnityExtTemplateLocation,
                        "x");
                params.put("repoxClassSimpleName", repoxClassSimpleName);
                generate(module, entityClass, params, this.ownedUnityImplTemplateLocation, "Impl");
                generate(module, entityClass, params, this.ownedUnityBaseTemplateLocation, Strings.EMPTY);
            } else {
                if (withImpl) {
                    String repoxClassSimpleName = generate(module, entityClass, params, this.unityExtTemplateLocation,
                            "x");
                    params.put("repoxClassSimpleName", repoxClassSimpleName);
                    generate(module, entityClass, params, this.unityImplTemplateLocation, "Impl");
                }
                generate(module, entityClass, params, this.unityBaseTemplateLocation, Strings.EMPTY);
            }
        } else if (Relation.class.isAssignableFrom(entityClass)) { // 关系实体一定需要生成实现类
            Class<?> leftKeyClass = ClassUtil.getActualGenericType(entityClass, Relation.class, 0);
            putClassName(params, leftKeyClass, "leftKey");
            Class<?> rightKeyClass = ClassUtil.getActualGenericType(entityClass, Relation.class, 1);
            putClassName(params, rightKeyClass, "rightKey");
            String repoxClassSimpleName = generate(module, entityClass, params, this.relationExtTemplateLocation,
                    "x");
            params.put("repoxClassSimpleName", repoxClassSimpleName);
            Field keyField = ClassUtil.findField(entityClass, RelationKey.class);
            Class<?> keyFieldType = keyField.getType();
            if (keyFieldType.isMemberClass()) { // 关键字类型为成员类，则只需生成实体类下限定的简称类
                params.put("keyClassSimpleName",
                        entityClass.getSimpleName() + Strings.DOT + keyFieldType.getSimpleName());
            } else { // 否则需生成完成的关键字类名称
                params.put("keyClassSimpleName", keyFieldType.getSimpleName());
                params.put("keyClassName", keyFieldType.getName());
            }
            generate(module, entityClass, params, this.relationBaseTemplateLocation, Strings.EMPTY);
            Binate<Field, Field> keyFieldBinate = getRelationKeyField(keyFieldType);
            String keyPropertyName = keyField.getName();
            String leftKeyPropertyName = keyPropertyName + Strings.DOT + keyFieldBinate.getLeft().getName();
            params.put("leftKeyPropertyName", leftKeyPropertyName);
            String rightKeyPropertyName = keyPropertyName + Strings.DOT + keyFieldBinate.getRight().getName();
            params.put("rightKeyPropertyName", rightKeyPropertyName);
            generate(module, entityClass, params, this.relationImplTemplateLocation, "Impl");
        }
        // 既不是单体也不是关系的普通实体，由于JPA要求指定Key类型，但框架的Entity没有限定Key类型，故无法自动生成
        // Entity不能限定Key类型的原因在于，子接口Relation的Key拆分成了左右两个Key，无法单一指定Key类型，这一点与JPA不相同
        // 好在普通Entity实体数量很少，Repo也很容易手写
    }

    private String generate(String module, Class<? extends Entity> entityClass, Map<String, Object> params,
            String location, String repoClassNameSuffix) throws IOException {
        String packageName = getTargetModulePackageName(module);
        String entityClassSimpleName = entityClass.getSimpleName();
        String repoClassSimpleName = entityClassSimpleName + "Repo" + repoClassNameSuffix;
        String repoClassName = packageName + Strings.DOT + repoClassSimpleName;
        params.put("packageName", packageName);
        params.put("entityClassName", entityClass.getName());
        params.put("repoClassSimpleName", repoClassSimpleName);
        params.put("entityClassSimpleName", entityClassSimpleName);
        generate(repoClassName, location, params);
        return repoClassSimpleName;
    }

}
