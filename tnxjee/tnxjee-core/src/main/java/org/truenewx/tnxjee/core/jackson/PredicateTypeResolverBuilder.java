package org.truenewx.tnxjee.core.jackson;

import java.util.function.Predicate;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * 由断言决定的类型解决器构建器
 */
public class PredicateTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    private static final long serialVersionUID = -1000737704428050672L;

    private Predicate<JavaType> predicate;

    public PredicateTypeResolverBuilder(Predicate<JavaType> predicate) {
        super(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, LaissezFaireSubTypeValidator.instance);
        this.predicate = predicate;
    }

    @Override
    public boolean useForType(JavaType type) {
        if (type.isPrimitive() || type.getContentType() != null || TreeNode.class
                .isAssignableFrom(type.getRawClass())) {
            return false;
        }
        if (this.predicate != null) {
            return this.predicate.test(type);
        }
        return type.isJavaLangObject();
    }

}
