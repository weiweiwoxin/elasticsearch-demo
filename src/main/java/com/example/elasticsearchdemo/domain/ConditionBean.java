package com.example.elasticsearchdemo.domain;

/**
 * @Description: TODO 复合条件实体
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 下午5:19 19-7-5
 */
public class ConditionBean {

    private String fieldName;

    private String[] keywords;

    private BoolTypeEnum boolType;

    private QueryTypeEnum queryType;

    public enum QueryTypeEnum{

        MUST("must"),
        FILTER("filter"),
        MUST_NOT("must_not"),
        SHOULD("should");

        private String value;

        QueryTypeEnum(String value){
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static QueryTypeEnum fromValue(String text){
            for (QueryTypeEnum b : QueryTypeEnum.values()){
                if (String.valueOf(b.value).equals(text)){
                    return b;
                }
            }
            return null;
        }
    }


    public enum BoolTypeEnum{

        TERM("term"),
        TERMS("terms"),
        RANGE("range");

        private String value;

        BoolTypeEnum(String value){
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static BoolTypeEnum fromValue(String text){
            for (BoolTypeEnum b : BoolTypeEnum.values()){
                if (String.valueOf(b.value).equals(text)){
                    return b;
                }
            }
            return null;
        }
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public BoolTypeEnum getBoolType() {
        return boolType;
    }

    public void setBoolType(BoolTypeEnum boolType) {
        this.boolType = boolType;
    }

    public QueryTypeEnum getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryTypeEnum queryType) {
        this.queryType = queryType;
    }
}
