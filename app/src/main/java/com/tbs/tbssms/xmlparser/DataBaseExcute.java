package com.tbs.tbssms.xmlparser;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.tbs.tbssms.database.DataBaseUtil;

public class DataBaseExcute
{

    private static final String TAG = "DataBaseExcute";
    public Map<String, FieldInfo> columnsMap = new HashMap<String, FieldInfo>();
    private String m_tableName;
    Context con;

    public class FieldInfo
    {
        String fieldName;
        String aliaName;
        boolean isNumberType;
    }

    public DataBaseExcute(Context c)
    {
        this.con = c;
    }

    // 璁惧畾褰撳墠琛ㄥ苟鍒濆鍖栧瓧娈典俊鎭�
    boolean useTable(String tableName)
    {
        m_tableName = tableName;
        return initTableinfo();
    }

    // 鑾峰彇褰撳墠琛�
    String getTableName()
    {
        return m_tableName;
    }

    // 鍒ゅ畾瀛楁绫诲瀷鏄惁鏁板瓧绫诲瀷,SQL璇彞涓瓧绗︿覆绫诲瀷鍊奸渶瑕佸鍗曞紩鍙�
    public boolean isNumberType(int dataType)
    {
        switch (dataType)
        {
            case java.sql.Types.BIGINT:
            case java.sql.Types.DECIMAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.REAL:
            case java.sql.Types.ROWID:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.TINYINT:
                return true;
            default:
                return false;
        }
    }

    // 璁惧畾瀛楁鍒悕
    public boolean SetAlias(String fieldName, String aliasName)
    {
        FieldInfo info = columnsMap.get(fieldName);
        if (info == null)
        {
            return false;
        }
        else
        {
            info.aliaName = aliasName;
            columnsMap.put(fieldName,info);
            return true;
        }
    }

    // 鍒濆鍖栬〃淇℃伅
    @SuppressLint("NewApi")
    private boolean initTableinfo()
    {
        try
        {
            columnsMap.clear();
            Cursor c = new DataBaseUtil(con).getDataBase().query(m_tableName,null,null,null,null,null,null);
            String columnNmae[] = c.getColumnNames();
            for (int i = 1; i < columnNmae.length; i++)
            {
                FieldInfo field = new FieldInfo();
                field.fieldName = columnNmae[i];
                //	int dataType = c.getType(i);
                //	Log.d(TAG, "dataType = " + dataType);
                //	field.isNumberType = isNumberType(dataType);
                //	Log.d(TAG, "isNumberType = " + field.isNumberType);
                columnsMap.put(field.fieldName,field);
            }
            c.close();
            return true;

        }
        catch (Exception e)
        {
            Log.e(TAG,"Error Trace in initTableinfo() : " + e.getMessage());
        }
        return false;
    }

}
