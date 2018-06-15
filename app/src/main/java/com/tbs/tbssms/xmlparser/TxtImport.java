package com.tbs.tbssms.xmlparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.xmlparser.DataBaseExcute.FieldInfo;

public class TxtImport
{

    private static final String TAG = "TxtImport";
    Context con;
    DataBaseExcute db;
    String m_fieldNameSep = ""; // 字段与字段值间的分隔符
    String m_recordSep = ""; // 记录之间的分割符,与m_recordSepCount一起使用
    int m_recordSepCount = 3; // 记录分隔符需要重复的次数,如值为3, 分隔符是"\n",表示连续3次换行表示记录结束
    String m_lineSplitter = ""; // 文本的默认换行符,如Windows下是"\r\n", Linux下是"\n"

    public TxtImport(Context c)
    {
        this.con = c;
        this.db = new DataBaseExcute(c);
    }

    // 按行解析文本时的状态
    // 初始状态是read_idle
    // 当找到一条字段的时候,状态由read_idle转变为read_field
    // 当结束一条记录的扫描时,状态由read_field转变为read_idle
    public enum read_state
    {
        read_idle, // 空闲状态
        read_field; // 读字段内容的状态
    }

    /*
     * fieldNameSep 字段和字段只之间的分隔符,可以为空 recordSep 记录之间的分隔符 recordSepCount
     * 记录分隔符recordSep需要出现的次数,即遇到几次才算记录结束 lineSplitter
     * 默认换行符,如Windows下是"\r\n",Linux下是"\n"
     */
    public void setScanParams(String fieldNameSep, String recordSep, int recordSepCount, String lineSplitter)
    {
        m_fieldNameSep = fieldNameSep;
        m_recordSep = recordSep;
        m_recordSepCount = recordSepCount;
        m_lineSplitter = lineSplitter;

        if (m_recordSepCount < 1)
        {
            m_recordSepCount = 1;
        }
        if (m_recordSep.equals("\n") || m_recordSep.equals("\r\n"))
        {
            m_recordSep = ""; // 如果是换行符,直接使用realine后判断是否为空即可
        }
    }

    // 设定数据库表
    public boolean changeTable(String tableName)
    {
        return db.useTable(tableName);
    }

    // 设定字段别名
    public boolean setColumnAlias(String columnName, String aliasName)
    {
        return db.SetAlias(columnName,aliasName);
    }

    /**
     * 将文本导入到数据库表中
     * @param srcTxtName 文件的路径
     * @param bAppend 设置是否为追加模式
     * @return 返回导入的数量
     */
    @SuppressLint("NewApi")
    public int importFromTxt(BufferedReader reader)
    {

        int totalCount = 0;
        String desTableName = db.getTableName();

        try
        {
            String tempString = null;
            String nameList = new String();
            String valueList = new String();

            boolean prevIsNum = true;
            int recMarkRep = 0;
            read_state state = read_state.read_idle;

            // 开始逐行读取文本
            while ((tempString = reader.readLine()) != null)
            {
                // Log.d(TAG,"tempString = " + tempString);
                // 读取到空字符串
                if (tempString.isEmpty())
                {
                    if (state.equals(read_state.read_idle))
                    {
                        // Log.d(TAG,"state = " + state);
                        continue;
                    }
                    else if (m_recordSep.isEmpty())
                    { // 记录的分隔符是换行符(已被替换成空字符串)
                      // Log.d(TAG,"m_recordSep = " + m_recordSep);
                        recMarkRep++;
                        if (recMarkRep == m_recordSepCount)
                        { // 记录分隔符达到区分次数
                          // Log.d(TAG,"m_recordSepCount = " + m_recordSepCount);
                          // 执行sql语句
                            if (prevIsNum == false)
                            {
                                valueList += "'";
                            }
                            // 执行SQL插入记录操作
                            if (executeInsert(desTableName,nameList,valueList))
                            {
                                totalCount++;
                            }

                            // 一条记录扫描结束,重置状态
                            nameList = "";
                            valueList = "";
                            prevIsNum = true;
                            recMarkRep = 0;
                            state = read_state.read_idle;
                        }
                    }
                }
                else
                { // 如果读到非空字符串
                    // 如果读到记录分隔符,则需要累加找到记录分隔符的次数,以判断记录是否结束
                    if (!m_recordSep.isEmpty() && m_recordSep.equals(tempString))
                    {
                    	//Log.d(TAG,"m_recordSep = " + m_recordSep);
                        recMarkRep++;
                        if (recMarkRep == m_recordSepCount)
                        { // 记录分隔符达到区分次数,一条记录记录结束
                            // Log.d(TAG,"m_recordSepCount = " + m_recordSepCount);
                            // 执行sql语句
                            if (prevIsNum == false)
                            {
                                valueList += "'";
                            }
                            // 执行SQL插入记录操作
                            if (executeInsert(desTableName,nameList,valueList))
                            {
                                totalCount++;
                            }

                            // 一条记录扫描结束,重置状态
                            nameList = "";
                            valueList = "";
                            prevIsNum = true;
                            recMarkRep = 0;
                            state = read_state.read_idle;
                        }
                    }

                    // 如果是读取字段期间,且记录分隔符被读到过几次,则记录分隔符属于字段值的一部分
                    if (state == read_state.read_field && !prevIsNum && recMarkRep != 0)
                    {
                        for (int i = 0; i < recMarkRep; i++)
                        {
                            if (m_recordSep.isEmpty())
                            {
                                valueList += m_lineSplitter;
                            }
                            else
                            {
                                valueList += m_recordSep;
                            }
                        }
                        recMarkRep = 0;
                    }

                    // SQL语句中需要替换特殊符号,如SQL值分隔符单引号"'",转义符"\\"
                    tempString = tempString.replace("'","\\'");

                    boolean bFind = false;
                    String aliasName = new String(); // 扫描文本时需要使用字段别名(字段别名默认与字段名相同)
                    String fieldName = new String(); // 执行SQL语句时,需要用真正的字段名
                    String fieldVal = new String(); // 字段值

                    // 遍历字段数组,查找此行文本是否是新字段内容的起始
                    Collection<FieldInfo> c = db.columnsMap.values();
                    Iterator<FieldInfo> it = c.iterator();
                    while (it.hasNext())
                    {
                        DataBaseExcute.FieldInfo info = it.next();
                        aliasName = info.aliaName;
                        fieldName = info.fieldName;

                        // Log.d(TAG,"fieldName = " + fieldName);
                        // Log.d(TAG,"aliasName = " + aliasName);

                        if (tempString.startsWith(aliasName + m_fieldNameSep))
                        {
                            // Log.d(TAG,"m_fieldNameSep = " + m_fieldNameSep);

                            // 本行的格式是"字段名分隔符字段值",截取后半部分字段值
                            fieldVal = tempString.substring(aliasName.length() + m_fieldNameSep.length());
                            bFind = true;
                            break;
                        }
                    }

                    // 如果确定是新字段的起始
                    if (bFind)
                    {
                        DataBaseExcute.FieldInfo info = db.columnsMap.get(fieldName);

                        if (info != null)
                        {
                            state = read_state.read_field; // 设定开始读字段的状态
                            if (nameList.isEmpty() == false)
                            {
                                nameList += ", ";

                                if (prevIsNum == false)
                                {
                                    // 上一个的字段是字符串,需要补全右半边单引号
                                    valueList += "', ";
                                }
                                else
                                {
                                    valueList += ", ";
                                }
                            }

                            // 追加本次扫描到的字段名
                            nameList += fieldName;
                            if (info.isNumberType)
                            { // 数字类型
                                if (fieldVal.isEmpty())
                                { // 数字类型是空,直接置NULL
                                    valueList += "NULL";
                                }
                                else
                                {
                                    valueList += fieldVal;
                                }
                            }
                            else
                            { // 文本类型
                                if (fieldVal.isEmpty())
                                {
                                    // 本行为空,则追加坐半边的单引号(读到下一个字段或记录结束前,会补全右半边单引号)
                                    valueList = valueList + "'";
                                }
                                else
                                {
                                    // 加上左半边单引号,(读到下一个字段或记录结束前,会补全右半边单引号)
                                    valueList = valueList + "'" + fieldVal;
                                }
                            } // info.isNumberType

                            // 记录本行扫描的字段类型,用于判定是否需要补全字段值的右半边单引号
                            prevIsNum = info.isNumberType;

                        } // info.isNumberType
                    }
                    else
                    {
                        // 如果未找到字段名和分隔符前缀,那么这行文本就可能属于上一个字段的内容(换行了)
                        if (state == read_state.read_field && prevIsNum == false)
                        {
                            valueList = valueList + m_lineSplitter + tempString;
                        }
                    }
                }
            }

            // 读到文件末尾,仍然需要判断最后一条记录有没有被插入
            if (state != read_state.read_idle && !nameList.isEmpty())
            {
                if (prevIsNum == false)
                {
                    valueList += "'";
                }

                // 执行SQL插入操作
                if (executeInsert(desTableName,nameList,valueList))
                {
                    totalCount++;
                }

            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG,"IOException importFromTxt() : " + e.getMessage());
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e1)
                {
                    Log.e(TAG,"IOException importFromTxt() : " + e1.getMessage());
                }
            }
        }

        return totalCount; // 返回插入的记录数
    }

    /**
     * 将表记录导入到文本
     * @param desTxtName 传入的字符串
     * @param bAppend 判断是否为追加模式
     * @return 返回到处的数据数量
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("unused")
	public int export2Txt(String desTxtName, boolean bAppend)
    {

        //select到所有记录
        String srcTableName = db.getTableName();
        String selectSql = String.format("select * from %s;",srcTableName);
        String recordData = new String();

        int totalCount = 0;

        Cursor cursor = new DataBaseUtil(con).getDataBase().rawQuery(selectSql,null);//查询数据库返回cursor
        try
        {
            //打开待写入文本
            File file = new File(desTxtName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file,bAppend));

            //逐条读取表的记录
            while (cursor.moveToNext())
            {

                //将记录按列输出,每列输出需另启一行
                for (int i = 1; i <= cursor.getColumnCount(); i++)
                {

                    String field = cursor.getColumnName(i);//获得字段名称
                    DataBaseExcute.FieldInfo info = db.columnsMap.get(field);//将字段名称加入到map中去
                    recordData = info.aliaName + m_fieldNameSep;

                    String value = cursor.getString(cursor.getColumnIndex(field));
                    int type = cursor.getType(i);
                    if (value != null && type == java.sql.Types.CHAR)
                    {
                        int nLen = value.length();
                        char[] buffer = value.toCharArray();
                        while (nLen > 0 && buffer[nLen - 1] == ' ')
                        {
                            nLen--;
                        }
                        value = value.substring(0,nLen);
                    }

                    if (cursor == null)
                    {
                        if (db.isNumberType(type)) //数字类型字段的空值,直接赋NULL
                        {
                            recordData += "null";
                        }
                    }
                    else
                    {
                        recordData += value;
                    }
                    recordData += m_lineSplitter;
                    writer.write(recordData);
                }

                //写入一条记录结束的标记
                for (int i = 0; i < m_recordSepCount; i++)
                {
                    writer.write(m_recordSep + m_lineSplitter);
                }

                totalCount++;
            }

            //写入最后一条记录结束标记
            for (int i = 0; i < m_recordSepCount; i++)
            {
                writer.write(m_recordSep + m_lineSplitter);
            }

            writer.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //返回导出的记录数
        return totalCount;
    }

    /**
     * 导出单条数据,通过传入cursor直接处理，然后组合出数据库字符串返回
     * @param cursor 传入的数据库查询结果
     * @param bAppend 判断是否为追加模式
     * @return 返回拼接好的字符串
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("unused")
    public String export2String(Cursor cursor, boolean bAppend)
    {
        String tempStr = "";//临时字符串保存拼接的字符串
        String recordData = new String();
        int totalCount = 0;
        try
        {
            //逐条读取表的记录
            while (cursor.moveToNext())
            {

                //将记录按列输出,每列输出需另启一行
                for (int i = 1; i <= cursor.getColumnCount(); i++)
                {

                    String field = cursor.getColumnName(i);//获得字段名称

                    DataBaseExcute.FieldInfo info = db.columnsMap.get(field);//将字段名称加入到map中去
                    recordData = info.aliaName + m_fieldNameSep;

                    String value = cursor.getString(cursor.getColumnIndex(field));

                    //int type = cursor.getType(i);

                    //if (value != null && type == java.sql.Types.CHAR) {//sqlite数据类型都可以为text格式，所以无需区分数据类型
                    if (value != null)
                    {
                        int nLen = value.length();
                        char[] buffer = value.toCharArray();
                        while (nLen > 0 && buffer[nLen - 1] == ' ')
                        {
                            nLen--;
                        }
                        value = value.substring(0,nLen);
                    }

                    if (cursor == null)
                    {
                        //if (db.isNumberType(type)) //数字类型字段的空值,直接赋NULL,sqlite数据类型都可以为text格式，所以无需区分数据类型
                        //{
                        //recordData += "null";
                        //}
                    }
                    else
                    {
                        recordData += value;
                    }
                    recordData += m_lineSplitter;
                    tempStr += recordData;
                    //writer.write(recordData);
                }

                //写入一条记录结束的标记
                for (int i = 0; i < m_recordSepCount; i++)
                {
                    //writer.write(m_recordSep + m_lineSplitter);
                    tempStr += m_recordSep + m_lineSplitter;
                    //Log.d(TAG,"tempStr = " + tempStr);
                }
                totalCount++;
            }

            //写入最后一条记录结束标记
            for (int i = 0; i < m_recordSepCount; i++)
            {
                //writer.write(m_recordSep + m_lineSplitter);
                tempStr += m_recordSep + m_lineSplitter;
                //Log.d(TAG,"tempStr2 = " + tempStr);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempStr;
    }
    

    public static String repS(String s){
        
        s = s.replaceAll(",", "▲△#▽▼");
        String reg = "<[^>,^<]*>";
        while(true){
                String tmp = s;
                s = s.replaceAll(reg, "");
                if(tmp.equals(s))break;
        }
        s = s.replaceAll("▲△#▽▼", ",");
        return s;
    }
    
    
    // 执行SQL Insert语句
    private boolean executeInsert(String desTableName, String nameList, String valueList)
    {
        Cursor cursor = null;
        
        StringBuffer sb = new StringBuffer();
        

        //拼接查询语句where条件
        String[] nameListStr = repS(nameList).split(",");
        String[] valueListStr = repS(valueList).split("',");
//        String[] nameListStr = nameList.split(",");
//        String[] valueListStr = valueList.split("',");

        //查询条件变量
        String selection = new String();
        String[] selectionArgs = new String[valueListStr.length];

        ContentValues values = new ContentValues();

        for (int i = 0; i < nameListStr.length; i++)
        {
            if (i == (nameListStr.length - 1))
            {
                //拼接查询条件
                sb.append(nameListStr[i]);
                sb.append(" = ?");
                //拼接查询条件值
                selectionArgs[i] = valueListStr[i].trim().substring(1,valueListStr[i].length() - 2);
                Log.d(TAG,"nameListStr["+i+"] : " + nameListStr[i]);
                Log.d(TAG,"selectionArgs["+i+"] : " + selectionArgs[i]);
                //制作插入条件的值
                values.put(nameListStr[i],valueListStr[i].trim().substring(1,valueListStr[i].length() - 2));
            }
            else
            {
                //拼接查询条件
                sb.append(nameListStr[i]);
                sb.append(" = ? and");
                //拼接查询条件值
                selectionArgs[i] = valueListStr[i].trim().substring(1);
                Log.d(TAG,"nameListStr["+i+"] : " + nameListStr[i]);
                Log.d(TAG,"selectionArgs["+i+"] : " + selectionArgs[i]);
                //制作插入条件的值
                values.put(nameListStr[i],valueListStr[i].trim().substring(1));
            }
        }
        Log.d(TAG,"nameListStr stringbuffer : " + sb.toString());
        cursor = new DataBaseUtil(con).getDataBase().query(desTableName,null,sb.toString(),selectionArgs,null,null,null);
        
        
        if (cursor != null && cursor.getCount() > 0)
        {
            Log.e(TAG,"Execute error: ");
            cursor.close();
            return false;
        }
        else
        {
            cursor.close();
            long result = new DataBaseUtil(con).getDataBase().insert(desTableName,null,values);
            if (result == -1)
            {
                Log.e(TAG,"Execute error: ");
                return false;
            }
            else
            {
                Log.d(TAG,"Execute success;");
                return true;
            }
        }
    }
}
