package org.truenewx.tnxjee.core.jackson;

import java.util.function.Predicate;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * 含断言判断的类型解决器构建器
 */
public class PredicateTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    private static final long serialVersionUID = -1000737704428050672L;

    private Predicate<Class<?>> predicate;

    public PredicateTypeResolverBuilder() {
        super(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, LaissezFaireSubTypeValidator.instance);
    }

    public PredicateTypeResolverBuilder(Predicate<Class<?>> predicate) {
        this();
        this.predicate = predicate;
    }

    @Override
    public boolean useForType(JavaType type) {
        while (type.isArrayType()) {
            type = type.getContentType();
        }
        while (type.isReferenceType()) {
            type = type.getReferencedType();
        }
        if (type.isPrimitive()) {
            return false;
        }
        // 注意：复合类型在此会被视为Object类型，从而使该判断为true
        if (type.isJavaLangObject()) {
            return true;
        }
        if (TreeNode.class.isAssignableFrom(type.getRawClass())) {
            return false;
        }
        if (this.predicate != null) {
            return this.predicate.test(type.getRawClass());
        }
        return true;
    }

}
