package org.truenewx.tnxjee.repo.jpa.converter;

import java.time.Instant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * Instant-字符串的属性转换器
 */
@Converter
public class InstantStringAttributeConverter implements AttributeConverter<Instant, String> {

    private static final int MIN_LENGTH = 19;
    private static final int MAX_LENGTH = 29;
    private static final int MAX_SCALE = MAX_LENGTH - MIN_LENGTH - 1; // 去掉小数点长度
    private static final char CHAR_END = 'Z';

    private int scale = MAX_SCALE;
    private boolean zoned;

    /**
     * @param scale 秒以下的精度，应在[0, 9]之间，小于0则视为0，大于9则视为9
     * @param zoned 是否时区化，即是否转换为当前时区的时间字符串，true-是，可读性最佳，略影响性能
     */
    public InstantStringAttributeConverter(int scale, boolean zoned) {
        this.scale = scale;
        this.zoned = zoned;
    }

    public InstantStringAttributeConverter(int scale) {
        this.scale = scale;
    }

    public InstantStringAttributeConverter() {
    }

    @Override
    public String convertToDatabaseColumn(Instant attribute) {
        if (attribute == null) {
            return null;
        }
        String s;
        if (this.zoned) {
            s = TemporalUtil.format(attribute) + Strings.DOT + attribute.getNano();
        } else {
            s = attribute.toString();
            s = s.substring(0, s.length() - 1); // 去掉末尾的Z
        }
        int cutLength = getCutLength();
        if (cutLength > 0) {
            s = s.substring(0, s.length() - cutLength);
        }
        return s;
    }

    private int getCutLength() {
        if (this.scale <= 0) {
            return MAX_SCALE + 1; // 连小数点都要去掉
        } else if (this.scale >= MAX_SCALE) {
            return 0;
        }
        return MAX_SCALE - this.scale;
    }

    @Override
    public Instant convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData) || dbData.length() < MIN_LENGTH) { // 精确到秒的最小长度
            return null;
        }
        StringBuilder s = new StringBuilder(dbData);
        if (s.length() > MAX_LENGTH) {
            s.delete(MAX_LENGTH, s.length());
        }
        char delimiter = s.charAt(10);
        if (delimiter == ' ') { // 转换了时区的
            Instant instant = TemporalUtil.formatter(DateUtil.LONG_DATE_PATTERN)
                    .parse(s.substring(0, MIN_LENGTH), Instant::from);
            if (s.length() > MIN_LENGTH + 1) { // 跳过小数点
                StringBuilder nanoString = new StringBuilder(s.substring(MIN_LENGTH + 1));
                while (nanoString.length() < MAX_SCALE) {
                    nanoString.append(0);
                }
                int nano = MathUtil.parseInt(nanoString.toString());
                instant = Instant.ofEpochSecond(instant.getEpochSecond(), nano);
            }
            return instant;
        } else if (delimiter == 'T') { // 未转换时区的
            if (!dbData.contains(Strings.DOT)) {
                s.append(Strings.DOT);
            }
            if (s.charAt(s.length() - 1) == CHAR_END) {
                s.deleteCharAt(s.length() - 1);
            }
            while (s.length() < MAX_LENGTH) {
                s.append(0);
            }
            s.append(CHAR_END);
            return Instant.parse(s);
        }
        return null;
    }

}
