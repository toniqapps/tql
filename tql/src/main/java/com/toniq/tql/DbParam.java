package com.toniq.tql;

public class DbParam
{
    private String key;
    private Object value;
    private String sign;
    private String operator;
    private Class dataType;

    public DbParam (String strKey, Object oValue)
    {
        key = strKey;
        value = oValue;
    }

    public DbParam (String strKey, Object oValue, String strSign)
    {
        key = strKey;
        value = oValue;
        sign = strSign;
    }

    public DbParam (String strKey, Object oValue, String strSign, String strOperator)
    {
        key = strKey;
        value = oValue;
        sign = strSign;
        operator = strOperator;
    }

    public DbParam (String strKey, Class oDataType)
    {
        key = strKey;
        dataType = oDataType;
    }

    public void setKey (String key)
    {
        this.key = key;
    }

    public String getKey ()
    {
        return key;
    }

    public void setValue (Object value)
    {
        this.value = value;
    }

    public Object getValue ()
    {
        return value;
    }

    public void setDataType (Class dataType)
    {
        this.dataType = dataType;
    }

    public Class getDataType ()
    {
        return dataType;
    }

    public String getSign ()
    {
        return sign;
    }

    public void setSign (String sign)
    {
        this.sign = sign;
    }

    public String getOperator ()
    {
        return operator;
    }

    public void setOperator (String operator)
    {
        this.operator = operator;
    }
}
