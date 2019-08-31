package com.toniq.tql;

public class DbRawData
{
    private String m_strKey;
    private String m_strValue;

    public DbRawData(String strKey, String strValue)
    {
        setKey (strKey);
        setValue (strValue);
    }
    public String getKey ()
    {
        return m_strKey;
    }

    public void setKey (String key)
    {
        this.m_strKey = key;
    }

    public String getValue ()
    {
        return m_strValue;
    }

    public void setValue (String value)
    {
        this.m_strValue = value;
    }
}
