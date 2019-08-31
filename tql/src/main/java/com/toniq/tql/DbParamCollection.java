package com.toniq.tql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.toniq.tql.utils.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class DbParamCollection
{
    private boolean m_bIsDistinct = false;
    private List<DbParam> m_oColumnsList = null;
    private List<DbParam> m_oWhereParamList = null;
    private String m_strGroupBy = null;
    private String m_strHaving = null;
    private String m_strOrderBy = null;
    private String limit = null;

    public DbParamCollection()
    {
    }

    public void SetIsDistinct(boolean bFlag)
    {
        m_bIsDistinct = bFlag;
    }

    public boolean getIsDistinct()
    {
        return m_bIsDistinct;
    }

    public void addColumnName(String strColumn, Class oDataType)
    {
        if (m_oColumnsList == null)
        {
            m_oColumnsList = new ArrayList<>();
        }
        m_oColumnsList.add(new DbParam(strColumn, oDataType));
    }

    public List<DbParam> GetColumnList()
    {
        return m_oColumnsList;
    }

    public void addWhereClause(DbParam oDbParam)
    {
        if (m_oWhereParamList == null)
        {
            m_oWhereParamList = new ArrayList<>();
        }
        m_oWhereParamList.add(oDbParam);
    }

    public List<DbParam> GetWhereClauseList()
    {
        return m_oWhereParamList;
    }

    public void SetGroupByClause(String strValue)
    {
        m_strGroupBy = strValue;
    }

    public String getGroupByClause()
    {
        return m_strGroupBy;
    }

    public void SetHavingClause(String strValue)
    {
        m_strGroupBy = strValue;
    }

    public String getHavingClause()
    {
        return m_strHaving;
    }

    public void setOrderByClause(String strValue)
    {
        m_strOrderBy = strValue;
    }

    public String getOrderByClause()
    {
        return m_strOrderBy;
    }

    public void setLimit(String strValue)
    {
        limit = strValue;
    }

    public String getLimit()
    {
        return limit;
    }

    public Cursor select(Class<? extends AppDataStructure> oAppDataStructure, SQLiteDatabase sqLiteDatabase)
    {
        String tableName = ReflectionUtil.GetClassName(oAppDataStructure);
        String whereClause = null;
        String[] whereArgs = null;
        String[] columns = null;

        if (GetColumnList() != null)
        {
            int nLength = GetColumnList().size();
            columns = new String[nLength];
            for (int nIndex = 0; nIndex < nLength; nIndex++)
            {
                DbParam oParameter = GetColumnList().get(nIndex);
                columns[nIndex] = oParameter.getKey();
            }

        }
        if (GetWhereClauseList() != null)
        {
            int nLength = GetWhereClauseList().size();
            StringBuilder oWhereClause = new StringBuilder();
            whereArgs = new String[nLength];

            for (int nIndex = 0; nIndex < nLength; nIndex++)
            {
                DbParam oParameter = GetWhereClauseList().get(nIndex);
                if (oParameter.getSign() != null)
                {
                    oWhereClause.append(oParameter.getKey() + oParameter.getSign() + "?");
                }
                else
                {
                    oWhereClause.append(oParameter.getKey() + " = ?");
                }
                if (nIndex + 1 < nLength)
                {
                    if (oParameter.getOperator() != null)
                    {
                        oWhereClause.append(" " + oParameter.getOperator() + " ");
                    }
                    else
                    {
                        oWhereClause.append(" And ");
                    }
                }
                whereArgs[nIndex] = String.valueOf(oParameter.getValue());
            }

            whereClause = oWhereClause.toString();
        }
        return sqLiteDatabase.query(getIsDistinct(), tableName, columns, whereClause, whereArgs, getGroupByClause(), getHavingClause(), getOrderByClause(), getLimit());
    }

    public int Update(Class<? extends AppDataStructure> oAppDataStructure, SQLiteDatabase oSqLiteDatabase)
    {
        int nResponse = -1;
        String strTableName = ReflectionUtil.GetClassName(oAppDataStructure);
        ContentValues oContentValues = new ContentValues();
        String strWhereClause = null;
        String[] a_strWhereArgs = null;

        if (GetColumnList() != null)
        {
            int nLength = GetColumnList().size();
            List<DbParam> oParameterList = GetColumnList();
            for (int nIndex = 0; nIndex < nLength; nIndex++)
            {
                DbParam oParameter = oParameterList.get(nIndex);
                if (oParameter.getDataType().equals(Integer.class) || oParameter.getDataType().equals(int.class))
                {
                    oContentValues.put(oParameter.getKey(), AppDataFormat.getInteger(oParameter.getValue()));
                }
                else if (oParameter.getDataType().equals(Long.class) || oParameter.getDataType().equals(long.class))
                {
                    oContentValues.put(oParameter.getKey(), AppDataFormat.GetLong(oParameter.getValue()));
                }
                else if (oParameter.getDataType().equals(Double.class) || oParameter.getDataType().equals(double.class))
                {
                    oContentValues.put(oParameter.getKey(), AppDataFormat.GetDouble(oParameter.getValue()));
                }
                else
                {
                    oContentValues.put(oParameter.getKey(), AppDataFormat.getString(oParameter.getValue()));
                }
            }

            if (GetWhereClauseList() != null)
            {
                nLength = GetWhereClauseList().size();
                StringBuilder oWhereClause = new StringBuilder();
                a_strWhereArgs = new String[nLength];

                for (int nIndex = 0; nIndex < nLength; nIndex++)
                {
                    DbParam oParameter = GetWhereClauseList().get(nIndex);
                    oWhereClause.append(oParameter.getKey() + " = ?");
                    a_strWhereArgs[nIndex] = String.valueOf(oParameter.getValue());
                }

                strWhereClause = oWhereClause.toString();
            }

            nResponse = oSqLiteDatabase.update(strTableName, oContentValues, strWhereClause, a_strWhereArgs);
        }

        return nResponse;
    }

    public int Delete(Class<? extends AppDataStructure> oAppDataStructure, SQLiteDatabase oSqLiteDatabase)
    {
        String strTableName = ReflectionUtil.GetClassName(oAppDataStructure);
        String strWhereClause = null;
        String[] a_strWhereArgs = null;

        if (GetWhereClauseList() != null)
        {
            int nLength = GetWhereClauseList().size();
            StringBuilder oWhereClause = new StringBuilder();
            a_strWhereArgs = new String[nLength];

            for (int nIndex = 0; nIndex < nLength; nIndex++)
            {
                DbParam oParameter = GetWhereClauseList().get(nIndex);
                oWhereClause.append(oParameter.getKey() + " = ?");
                a_strWhereArgs[nIndex] = String.valueOf(oParameter.getValue());
            }

            strWhereClause = oWhereClause.toString();
        }

        return oSqLiteDatabase.delete(strTableName, strWhereClause, a_strWhereArgs);
    }
}
