package com.toniq.tql;

import android.content.Context;

final class DbConstant
{
    static final String DATABASE_NAME = "BioKnox";
    static final int DATABASE_VERSION = 1;
    static final int DB_READABLE = 0;
    static final int DB_WRITABLE = 1;
    static final String SETTER = "set";
    static final String GETTER = "get";

    static int INDEX_NOT_DEFINED = -1;

    static String getDataBasePath(Context oContext)
    {
        return oContext.getDatabasePath(DbConstant.DATABASE_NAME).getPath();
    }

    static String GetDataBaseParentPath(Context oContext)
    {
        return oContext.getDatabasePath(DbConstant.DATABASE_NAME).getParent();
    }
}
