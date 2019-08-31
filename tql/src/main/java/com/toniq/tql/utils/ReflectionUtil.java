package com.toniq.tql.utils;

import java.lang.reflect.Constructor;

public class ReflectionUtil
{
    public static String GetClassName(Class<?> oClass)
    {
        String strClassName = null;
        if (oClass != null)
        {
            strClassName = oClass.getName().trim();
            if (strClassName != null && strClassName.equals("") == false && strClassName.lastIndexOf('.') > 0)
            {
                strClassName = strClassName.substring(strClassName.lastIndexOf('.') + 1);
            }
        }
        return strClassName;
    }

    public static Object GetInstance(Class<?> oClass) throws Exception
    {
        Object oObject = null;
        if (oClass != null)
        {
            Constructor<?> oConstructor = oClass.getConstructor();
            if (oConstructor != null)
                oObject = oConstructor.newInstance();
        }

        return oObject;
    }
}
