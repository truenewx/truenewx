package org.truenewx.tnxjee.model.entity;

import java.lang.reflect.Field;

import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;

/**
 * 历史实体
 *
 * @param <P> 对应的当前实体类型
 */
public interface HistoryEntity<P extends Entity> extends Entity {

    @SuppressWarnings("unchecked")
    default P toPresent() {
        Class<? extends HistoryEntity<P>> historyClass = (Class<? extends HistoryEntity<P>>) getClass();
        Class<P> presentClass = ClassUtil.getActualGenericType(historyClass, HistoryEntity.class, 0);
        P present = ClassUtil.newInstance(presentClass);
        ClassUtil.loopDynamicFields(historyClass, historyField -> {
            String propertyName = historyField.getName();
            Field presentField = ClassUtil.findField(presentClass, propertyName);
            if (presentField != null) { // 当前实体与历史实体相同名称的字段才考虑
                Object fieldValue = BeanUtil.getFieldValue(this, historyField);
                // 如果字段类型也是历史实体类型，则需先转换为其对应的当前实体对象
                if (fieldValue instanceof HistoryEntity) {
                    fieldValue = ((HistoryEntity<?>) fieldValue).toPresent();
                }
                BeanUtil.setFieldValue(present, presentField, fieldValue);
            }
            return true;
        });
        return present;
    }

    @SuppressWarnings("unchecked")
    default void fromPresent(P present) {
        if (present != null) {
            Class<? extends HistoryEntity<P>> historyClass = (Class<? extends HistoryEntity<P>>) getClass();
            Class<P> presentClass = ClassUtil.getActualGenericType(historyClass, HistoryEntity.class, 0);
            ClassUtil.loopDynamicFields(historyClass, historyField -> {
                String propertyName = historyField.getName();
                Field presentField = ClassUtil.findField(presentClass, propertyName);
                if (presentField != null) { // 当前实体与历史实体相同名称的字段才考虑
                    Object fieldValue = BeanUtil.getFieldValue(present, presentField);
                    if (fieldValue != null) {
                        Class<?> historyFieldType = historyField.getType();
                        // 如果字段类型也是历史实体类型，则需先将其当前类型对象转换为历史对象
                        if (HistoryEntity.class.isAssignableFrom(historyFieldType)) {
                            HistoryEntity<Entity> historyFieldValue = (HistoryEntity<Entity>) BeanUtil
                                    .getFieldValue(this, historyField);
                            if (historyFieldValue == null) {
                                historyFieldValue = (HistoryEntity<Entity>) ClassUtil.newInstance(historyFieldType);
                            }
                            historyFieldValue.fromPresent((Entity) fieldValue);
                            fieldValue = historyFieldValue;
                        }
                    }
                    BeanUtil.setFieldValue(this, historyField, fieldValue);
                }
                return true;
            });
        }
    }

}
