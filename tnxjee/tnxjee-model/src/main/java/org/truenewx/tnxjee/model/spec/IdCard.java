package org.truenewx.tnxjee.model.spec;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;
import org.truenewx.tnxjee.model.ValueModel;
import org.truenewx.tnxjee.model.validation.constraint.IdCardNo;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsSpecialChars;

/**
 * 身份证
 */
public class IdCard implements ValueModel {

    @IdCardNo
    private String number;
    private LocalDate expiredDate;
    private String regionCode;
    @NotContainsSpecialChars
    private String address;

    protected IdCard() {
    }

    public IdCard(String number) {
        setNumber(number);
    }

    public String getNumber() {
        return this.number;
    }

    protected void setNumber(String number) {
        Assert.isTrue(StringUtil.isIdCardNo(number), "Id card number must be right format");
        this.number = number;
    }

    public LocalDate getExpiredDate() {
        return this.expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getRegionCode() {
        return this.regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //////


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdCard idCard = (IdCard) o;
        return this.number.equals(idCard.number) && Objects
                .equals(this.expiredDate, idCard.expiredDate) && Objects
                .equals(this.regionCode, idCard.regionCode) && Objects.equals(this.address, idCard.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number, this.expiredDate, this.regionCode, this.address);
    }

    public LocalDate getBirthday() {
        String s = this.number.substring(6, 14);
        return TemporalUtil.parse(LocalDate.class, s, "yyyyMMdd");
    }

    public boolean isMale() {
        String s = this.number.length() == 18 ? this.number.substring(16, 17) : this.number.substring(14);
        int value = MathUtil.parseInt(s);
        return value % 2 == 1;
    }

}
