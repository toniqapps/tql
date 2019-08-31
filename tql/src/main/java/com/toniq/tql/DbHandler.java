package com.toniq.tql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.toniq.tql.utils.DateUtil;
import com.toniq.tql.utils.FileUtil;
import com.toniq.tql.utils.ReflectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("all")
public class DbHandler extends SQLiteOpenHelper {
    private static DbHandler dbHelper;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static SQLiteDatabase mDatabase;
    private static final String TAG = "helper.dbAccess.DbHandler";

    public static synchronized boolean initDbInstance(Context context) throws Exception
    {
        boolean valid = false;
        if (dbHelper == null)
        {
            dbHelper = new DbHandler(context);
            JSONArray dataArray;
            try
            {
                dataArray = DbHandler.getDbInstance().selectRawQuery("Select * from menuInfo", null);
            } catch (Exception e)
            {
                dataArray = null;
            }
            if (dataArray == null || dataArray.length() <= 0)
            {
                valid = copyDb(context);
            }
        }
        return valid;
    }

    private static boolean copyDb(Context context) throws Exception
    {
        getDbInstance().copyDbFromAsset(context);
        return true;
    }

    public static synchronized DbHandler getDbInstance()
    {
        return dbHelper;
    }

    private DbHandler(Context oContext)
    {
        super(oContext, DbConstant.DATABASE_NAME, null, DbConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private synchronized SQLiteDatabase openDatabase(int type)
    {
        if (mOpenCounter.incrementAndGet() == 1)
        {
            if (type == DbConstant.DB_WRITABLE)
            {
                mDatabase = dbHelper.getWritableDatabase();
            }
            else
            {
                mDatabase = dbHelper.getReadableDatabase();
            }
        }
        return mDatabase;
    }

    private synchronized void closeDatabase()
    {
        if (mOpenCounter.decrementAndGet() == 0)
        {
            mDatabase.close();
        }
    }

    public JSONArray selectRawQuery(String strSqlQuery, List<Object> whereClauseValues) throws Exception
    {
        JSONArray jsonArray = null;
        Cursor cursor = null;
        String[] tagValue = null;
        try
        {
            SQLiteDatabase sqLiteDatabase = openDatabase(DbConstant.DB_READABLE);
            if (whereClauseValues != null && whereClauseValues.size() > 0)
            {
                int length = whereClauseValues.size();
                tagValue = new String[length];
                for (int index = 0; index < length; index++)
                {
                    tagValue[index] = String.valueOf(whereClauseValues.get(index));
                }
            }
            cursor = sqLiteDatabase.rawQuery(strSqlQuery, tagValue);
            if (cursor.moveToFirst())
            {
                jsonArray = new JSONArray();
                do
                {
                    String[] columnNameArray = cursor.getColumnNames();
                    int columnLength = columnNameArray.length;
                    JSONObject jsonObject = new JSONObject();
                    for (int index = 0; index < columnLength; index++)
                    {
                        jsonObject.put(columnNameArray[index], cursor.getString(index));
                    }
                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());
            }
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
            closeDatabase();
        }
        return jsonArray;
    }

    @SuppressWarnings("all")
    public <T> List<T> selectQuery(Class<? extends AppDataStructure> appDataStructure, DbParamCollection paramCollection) throws Exception
    {
        List<T> oList = new ArrayList<>();
        Cursor oCursor = null;
        try
        {
            SQLiteDatabase sqLiteDatabase = openDatabase(DbConstant.DB_READABLE);
            oCursor = paramCollection.select(appDataStructure, sqLiteDatabase);
            if (oCursor.moveToFirst())
            {
                do
                {
                    Object _oInstance = ReflectionUtil.GetInstance(appDataStructure);
                    String[] a_strColumnName = oCursor.getColumnNames();
                    int nColumlength = a_strColumnName.length;
                    for (int index = 0; index < nColumlength; index++)
                    {
                        Class oDataTypeClass = paramCollection.GetColumnList().get(index).getDataType();
                        Object[] a_oObjects = new Object[1];

                        if (oDataTypeClass.equals(Date.class))
                        {
                            a_oObjects[0] = DateUtil.ValueToDate(oCursor.getString(index));
                        }
                        else if (oDataTypeClass.equals(Integer.class) || oDataTypeClass.equals(int.class))
                        {
                            a_oObjects[0] = oCursor.getInt(index);
                        }
                        else if (oDataTypeClass.equals(Long.class) || oDataTypeClass.equals(long.class))
                        {
                            a_oObjects[0] = oCursor.getLong(index);
                        }
                        else if (oDataTypeClass.equals(Float.class) || oDataTypeClass.equals(float.class))
                        {
                            a_oObjects[0] = oCursor.getFloat(index);
                        }
                        else if (oDataTypeClass.equals(Double.class) || oDataTypeClass.equals(double.class))
                        {
                            a_oObjects[0] = oCursor.getDouble(index);
                        }
                        else
                        {
                            a_oObjects[0] = oCursor.getString(index);
                        }
                        Class<?> params[] = new Class[a_oObjects.length];
                        params[0] = oDataTypeClass;
                        String strColumnName = a_strColumnName[index];
                        Method oMethod = appDataStructure.getDeclaredMethod(DbConstant.SETTER + strColumnName.substring(0, 1).toUpperCase() + strColumnName.substring(1), params);
                        oMethod.invoke(_oInstance, a_oObjects);
                    }
                    if (_oInstance instanceof AppDataStructure)
                    {
                        oList.add((T) _oInstance);
                    }
                } while (oCursor.moveToNext());
            }
        } finally
        {
            if (oCursor != null)
            {
                oCursor.close();
            }
            closeDatabase();
        }
        return oList;
    }

    public long insert(AppDataStructure appDataStructure) throws Exception
    {
        long result = DbConstant.INDEX_NOT_DEFINED;
        if (appDataStructure != null)
        {
            try
            {
                result = insertQuery(appDataStructure, openDatabase(DbConstant.DB_WRITABLE));
            } finally
            {
                closeDatabase();
            }
        }
        return result;
    }

    public <T> long batchInsert(List<T> oList) throws Exception
    {
        long result = DbConstant.INDEX_NOT_DEFINED;
        if (oList != null)
        {
            try
            {
                SQLiteDatabase sqLiteDatabase = openDatabase(DbConstant.DB_WRITABLE);
                sqLiteDatabase.beginTransaction();
                int length = oList.size();
                for (int index = 0; index < length; index++)
                {
                    AppDataStructure appDataStructure = (AppDataStructure) oList.get(index);
                    result = insertQuery(appDataStructure, sqLiteDatabase);
                }
                sqLiteDatabase.setTransactionSuccessful();
                sqLiteDatabase.endTransaction();
            } finally
            {
                closeDatabase();
            }
        }
        return result;
    }

    private long insertQuery(AppDataStructure appDataStructure, SQLiteDatabase sqLiteDatabase) throws Exception
    {
        long result = DbConstant.INDEX_NOT_DEFINED;
        ContentValues oContentValues = new ContentValues();
        Class<? extends AppDataStructure> c = appDataStructure.getClass();
        Method[] oMethods = c.getDeclaredMethods();
        for (Method oMethod : oMethods)
        {
            if (oMethod.getName().substring(0, 3).equals(DbConstant.GETTER))
            {
                String strValue = String.valueOf(oMethod.invoke(appDataStructure));
                if (strValue != null)
                {
                    String strFieldName = oMethod.getName().substring(3, oMethod.getName().length());
                    if (strFieldName.equalsIgnoreCase("Id"))
                    {
                        continue;
                    }
                    if (oMethod.getReturnType().equals(Date.class))
                    {
                        oContentValues.put(strFieldName, DateUtil.DateToValue(strValue));
                    }
                    else if (oMethod.getReturnType().equals(int.class))
                    {
                        oContentValues.put(strFieldName, Integer.valueOf(strValue));
                    }
                    else if (oMethod.getReturnType().equals(long.class))
                    {
                        oContentValues.put(strFieldName, Long.valueOf(strValue));
                    }
                    else if (oMethod.getReturnType().equals(double.class))
                    {
                        oContentValues.put(strFieldName, Double.valueOf(strValue));
                    }
                    else if (oMethod.getReturnType().equals(float.class))
                    {
                        oContentValues.put(strFieldName, Float.valueOf(strValue));
                    }
                    else
                    {
                        oContentValues.put(strFieldName, strValue);
                    }
                }
            }
        }
        String strTableName = ReflectionUtil.GetClassName(appDataStructure.getClass());
        if (oContentValues.size() > 0)
        {
            result = sqLiteDatabase.insertOrThrow(strTableName, null, oContentValues);
        }

        return result;
    }

    public int updateRawQuery(String strSqlQuery, List<Object> selectionList) throws Exception
    {
        int nResponse = DbConstant.INDEX_NOT_DEFINED;
        Cursor oCursor = null;
        try
        {
            SQLiteDatabase sqLiteDatabase = openDatabase(DbConstant.DB_WRITABLE);
            String[] selectionArgArray = null;
            if (selectionList != null && selectionList.size() > 0)
            {
                int length = selectionList.size();
                selectionArgArray = new String[length];
                for (int index = 0; index < length; index++)
                {
                    selectionArgArray[index] = String.valueOf(selectionList.get(index));
                }
            }
            oCursor = sqLiteDatabase.rawQuery(strSqlQuery, selectionArgArray);
            oCursor.moveToFirst();
            nResponse = 0;
        } finally
        {
            if (oCursor != null)
            {
                oCursor.close();
            }
            closeDatabase();
        }

        return nResponse;
    }

    public int deleteRawQuery(String strSqlQuery, List<Object> selectionList) throws Exception
    {
        int nResponse = DbConstant.INDEX_NOT_DEFINED;
        Cursor oCursor = null;
        try
        {
            SQLiteDatabase sqLiteDatabase = openDatabase(DbConstant.DB_WRITABLE);
            String[] selectionArgArray = null;
            if (selectionList != null && selectionList.size() > 0)
            {
                int length = selectionList.size();
                selectionArgArray = new String[length];
                for (int index = 0; index < length; index++)
                {
                    selectionArgArray[index] = String.valueOf(selectionList.get(index));
                }
            }
            oCursor = sqLiteDatabase.rawQuery(strSqlQuery, selectionArgArray);
            oCursor.moveToFirst();
            nResponse = 0;
        } finally
        {
            if (oCursor != null)
            {
                oCursor.close();
            }
            closeDatabase();
        }

        return nResponse;
    }

    private void copyDbFromAsset(Context context) throws Exception
    {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try
        {
            inputStream = context.getAssets().open(DbConstant.DATABASE_NAME);
            String dbFileName = DbConstant.getDataBasePath(context);
            File dbFile = new File(DbConstant.GetDataBaseParentPath(context));
            if (dbFile.exists() || dbFile.mkdir())
            {
                outputStream = new FileOutputStream(dbFileName);
                byte[] bufferArray = new byte[1024];
                int length;
                while ((length = inputStream.read(bufferArray)) > 0)
                {
                    outputStream.write(bufferArray, 0, length);
                }
                outputStream.flush();
            }
        } finally
        {
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
                if (inputStream != null)
                {
                    inputStream.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean importDatabase(Context context) throws Exception
    {
        String strDbPath = Environment.getExternalStorageDirectory() + "/" + DbConstant.DATABASE_NAME;
        boolean valid = false;
        String dbFilePath = DbConstant.getDataBasePath(context);
        File newDb = new File(strDbPath);
        File oldDb = new File(dbFilePath);

        if (newDb.exists())
        {
            FileUtil.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            valid = true;
        }
        return valid;
    }

    public boolean checkDbExist()
    {
        String strDbPath = Environment.getExternalStorageDirectory() + "/" + DbConstant.DATABASE_NAME;
        boolean valid = false;
        File newDb = new File(strDbPath);
        if (newDb.exists())
        {
            valid = true;
        }
        return valid;
    }

    @SuppressWarnings("all")
    public boolean exportDatabase(Context context) throws Exception
    {
        boolean valid = false;
        OutputStream outputStream = null;
        try
        {
            File dbFile = new File(DbConstant.getDataBasePath(context));
            File dbExternalPath = new File(Environment.getExternalStorageDirectory() + "/" + DbConstant.DATABASE_NAME);
            if (dbExternalPath.exists())
            {
                dbExternalPath.delete();
            }
            FileInputStream fis = new FileInputStream(dbFile);
            String outFileName = Environment.getExternalStorageDirectory() + "/" + DbConstant.DATABASE_NAME;
            outputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int nLength;
            while ((nLength = fis.read(buffer)) > 0)
            {
                outputStream.write(buffer, 0, nLength);
            }
            valid = true;
        } finally
        {
            if (outputStream != null)
            {
                try
                {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return valid;
    }


    public void deleteDataBase(Context context) throws Exception
    {
        if (context != null)
        {
            context.deleteDatabase(DbConstant.DATABASE_NAME);
            DbHandler.copyDb(context);
        }

    }
}
