/***
 * Created by Rahul on 02-02-2016.
 */
package com.toniq.tql;

public class AppDataFormat
{
    public static int getInteger (Object objValue)
    {
        if (objValue instanceof String)
        {
            return getInteger ((String) objValue);
        }
        else if (objValue instanceof Integer)
        {
            return (Integer) objValue;
        }
        else
        {
            throw new IllegalArgumentException("This Object doesn't represent an int");
        }
    }

    public static int getInteger (String strValue)
    {
        return Integer.parseInt (strValue);
    }

    public static double GetDouble (Object objValue)
    {
        if (objValue instanceof String)
        {
            return GetDouble ((String) objValue);
        }
        else if (objValue instanceof Double)
        {
            return (Double) objValue;
        }
        else
        {
            throw new IllegalArgumentException("This Object doesn't represent an double");
        }
    }

    public static double GetDouble (String strValue)
    {
        return Double.parseDouble (strValue);
    }

    public static long GetLong (Object objValue)
    {
        if (objValue instanceof String)
        {
            return GetLong ((String) objValue);
        }
        else if (objValue instanceof Long)
        {
            return (Long) objValue;
        }
        else
        {
            throw new IllegalArgumentException("This Object doesn't represent an double");
        }
    }

    public static long GetLong (String strValue)
    {
        return Long.parseLong (strValue);
    }

    public static String getString (Object oData)
    {
        return String.valueOf (oData);
    }
}
