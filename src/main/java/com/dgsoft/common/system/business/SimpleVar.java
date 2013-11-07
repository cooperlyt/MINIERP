package com.dgsoft.common.system.business;

import com.dgsoft.common.system.model.SimpleVarDefine;
import com.dgsoft.common.system.model.SimpleVarSubscribe;
import com.dgsoft.common.system.model.Word;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/27/13
 * Time: 12:16 PM
 */
public class SimpleVar implements SubscribeVar<SimpleVar> {

    private SimpleVarSubscribe simpleVarSubscribe;
    private String value;

    @Override
    public String getView() {
        return "/layout/system/business/SimpleVar.xhtml";
    }

    public SimpleVar(SimpleVarSubscribe simpleVarSubscribe) {
        super();
        this.simpleVarSubscribe = simpleVarSubscribe;
    }

    public SimpleVar(SimpleVarSubscribe simpleVarSubscribe, String value) {
        super();
        this.simpleVarSubscribe = simpleVarSubscribe;
        this.value = value;
    }

    public boolean isStrType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.STRING);
    }

    public String getStrValue() {
        return value;
    }

    public void setStrValue(String value) {
        this.value = value;
    }

    public boolean isIntType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.INTEGER);
    }

    public Integer getIntValue() {
        if (value == null) {
            return null;
        } else
            return Integer.valueOf(value);
    }

    public void setIntValue(Integer value) {
        if (value == null) {
            this.value = null;
        } else
            this.value = String.valueOf(value);
    }

    public boolean isFloatType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.FLOAT);
    }

    public Float getFloatValue() {
        if (value == null)
            return null;
        else
            return Float.valueOf(value);
    }

    public void setFloatValue(Float value) {
        if (value == null) {
            this.value = null;
        } else
            this.value = String.valueOf(value);
    }

    public boolean isDoubleType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.DOUBLE);
    }

    public Double getDoubleValue() {
        if (value == null)
            return null;
        else
            return Double.valueOf(value);
    }

    public void setDoubleValue(Double value) {
        if (value == null)
            this.value = null;
        else this.value = String.valueOf(value);
    }

    public boolean isBooleanType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.BOOLEAN);
    }

    public Boolean getBooleanValue() {
        if (value == null)
            return null;
        else return Boolean.valueOf(value);
    }

    public void setBooleanValue(Boolean value) {
        if (value == null)
            this.value = null;
        else this.value = String.valueOf(value);
    }

    public boolean isAreaType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.AREA);
    }


    public Double getAreaValue() {
        if (value == null) return null;
        else {
            return getScaleValue(3);
        }
    }

    public void setAreaValue(Double value) {
        if (value == null) this.value = null;
        else {
            setScaleValue(value, 3);
        }
    }

    public boolean isMoneyType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.MONEY);
    }

    public Double getMoneyValue() {
        if (value == null) return null;
        else {
            return getScaleValue(3);
        }
    }

    public void setMoneyValue(Double value) {
        if (value == null) this.value = null;
        else {
            setScaleValue(value, 3);
        }
    }

    public boolean isShortMoneyType() {
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.SHORT_MONEY);
    }

    public Double getShortMoneyValue() {
        if (value == null) return null;
        else {
            return getScaleValue(2);
        }
    }

    public void setShortMoneyValue(Double value) {
        if (value == null) this.value = null;
        else {
            setScaleValue(value, 2);
        }
    }


    private void setScaleValue(Double value, int scale) {
        BigDecimal bigDecimal = new BigDecimal(value);
        this.value = String.valueOf(bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    private Double getScaleValue(int scale) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public boolean isWordType(){
        return  simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.WORD);
    }

    public Word getWordValue() {
        if (value != null)
            for (Word word : simpleVarSubscribe.getSimpleVarDefine().getWordCategory().getWords()) {
                if (word.getId().equals(value)) {
                    return word;
                }
            }
        return null;
    }

    public void setWordValue(Word value) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = value.getId();
        }
    }

    public boolean isDateType(){
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.DATE);
    }

    public Date getDateValue(){
        if (value == null)
            return null;
        else
            return new Date(Long.parseLong(value));
    }

    public void setDateValue(Date value){
        if (value == null){
            this.value = null;
        }else
            this.value = String.valueOf(value.getTime());
    }

    public boolean isDatetimeType(){
        return simpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.DATETIME);
    }

    public Object getValue() {
        if (value != null)
            switch (simpleVarSubscribe.getSimpleVarDefine().getType()) {
                case STRING:
                    return getStrValue();
                case INTEGER:
                    return getIntValue();
                case FLOAT:
                    return getFloatValue();
                case DOUBLE:
                    return getDoubleValue();
                case BOOLEAN:
                    return getBooleanValue();
                case SHORT_MONEY:
                    return getShortMoneyValue();
                case MONEY:
                    return getMoneyValue();
                case AREA:
                    return getAreaValue();
                case WORD:
                    return getWordValue();

                case DATE:
                case DATETIME:
                    return getDateValue();
            }
        return null;
    }

    public SimpleVarSubscribe getSimpleVarSubscribe() {
        return simpleVarSubscribe;
    }

    @Override
    public int compareTo(SimpleVar o) {
        return new Integer(this.simpleVarSubscribe.getPriority()).compareTo(o.simpleVarSubscribe.getPriority());
    }
}
