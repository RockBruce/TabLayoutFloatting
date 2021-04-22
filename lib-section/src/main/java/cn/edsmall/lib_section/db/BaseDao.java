package cn.edsmall.lib_section.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edsmall.lib_section.dbannotation.DbField;
import cn.edsmall.lib_section.dbannotation.DbTable;

public class BaseDao<T> implements IBaseDao<T> {
    //持有数据库操作的引用
    private SQLiteDatabase sqLiteDatabase;
    //表名
    private String tableName;
    //持有操作数据库所对应的java类型
    private Class<T> entityClass;
    //标识：用来表示是否做过初始化操作
    private boolean isInit = false;
    //定义一个缓存空间（key-数据库字段名 value-成员变量:通过反射获取对象的所有成员变量）
    private HashMap<String, Field> cacheMap;
    private String id;

    //框架内部的逻辑，最好不要提供构造方法给调用层调用
    protected boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        //可以根据传入的entityClass类型来建立表，只需要建一次
        if (!isInit) {
            //自动建表
            //取到表名
            if (entityClass.getAnnotation(DbTable.class) == null) {
                //反射到类名
                tableName = entityClass.getSimpleName();
            } else {
                //取注解上的名字
                tableName = entityClass.getAnnotation(DbTable.class).value();
            }
            //建表操作
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            //单独用个方法来生成create命令
            String createTableSql = getCreateTableSql();
            sqLiteDatabase.execSQL(createTableSql);
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;

        }
        return isInit;

    }

    private void initCacheMap() {
        //1.取得所有的列名
        String sql = "select * from " + tableName + " limit 1,0"; //空表
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames(); //数据库全部列名
         id  = columnNames[0];
        //2.反射获取得所有的成员变量
        Field[] columnFields = entityClass.getDeclaredFields();
        //把所有字段的访问权限打开
        for (Field declaredField : columnFields) {
            declaredField.setAccessible(true);
        }
        //3.对1和2进行映射
        for (String columnName : columnNames) { //数据库中的全部列名
            Field columnField = null;
            for (Field field : columnFields) { //实体类中全部成员变量
                String fieldName = null;
                if (field.getAnnotation(DbField.class) != null) {
                    //如果成员变量中有注解
                    fieldName = field.getAnnotation(DbField.class).value();
                } else {
                    fieldName = field.getName();
                }
                if (columnName.equals(fieldName)) {
                    //如果列名==成员变量名
                    columnField = field;
                    break;
                }
            }
            if (columnField != null) {
                cacheMap.put(columnName, columnField);
            }

        }
    }

    private String getCreateTableSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName + "(  id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        //反射得到多有的成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType(); //拿到成员类型
            if (field.getAnnotation(DbField.class) != null) {
                if (type == String.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BLOB,");
                } else {
                    continue;
                }
            } else {
                if (type == String.class) {
                    stringBuffer.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getName() + " BLOB,");
                } else {
                    continue;
                }
            }
        }
        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    private class Condition {
        private String whereCause;
        private String[] whereArgs;

        public Condition(Map<String, String> whereCause) {
            ArrayList list = new ArrayList();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("1=1");
            //取得所有的字段名
            Set<String> keys = whereCause.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = whereCause.get(key);
                if (value != null) {
                    stringBuffer.append(" and " + key + "=?");
                    list.add(value);
                }

            }
            this.whereCause = stringBuffer.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }

    @Override
    public long insert(T entity) {
        //准备好ContentValue
        Map<String, String> map = getValue(entity);
        ContentValues contentValues = getContentValues(map);
        long insert = sqLiteDatabase.insert(tableName, null, contentValues);
        return insert;
    }

    @Override
    public long update(T entity, T where) {
        Map value = getValue(entity);
        ContentValues contentValues = getContentValues(value);
        Map whereCause = getValue(where);
        Condition condition = new Condition(whereCause);
        int result = sqLiteDatabase.update(tableName, contentValues, condition.whereCause, condition.whereArgs);
        return result;
    }

    @Override
    public long delete(T where,int limit) {
        Map whereCause = getValue(where);
        Condition condition = new Condition(whereCause);
//        sqLiteDatabase.delete(tableName, condition.whereCause, condition.whereArgs);
        String sql = "delete from " + tableName +
                " where " + id + " in(" +
                "select " + id + " from " + tableName +
                " order by " + id +
                " limit " + limit+ ")";
        sqLiteDatabase.execSQL(sql);
        return 0;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, 0, 10);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map map = getValue(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName, null, null, null, null,null, orderBy, limitString);
//        sqLiteDatabase.rawQuery(null,null);

        //定义一个用来解析游标的方法
        List<T> result = getResult(cursor, where);
        return result;
    }

    /**
     * 把游标封装成 obj格式的对象
     * @param cursor
     * @param obj    要生成对象的结构
     * @return
     */
    private List<T> getResult(Cursor cursor, T obj) {
            ArrayList list=new ArrayList();
            Object item=null;
            while (cursor.moveToNext()){
                try {
                    item=obj.getClass().newInstance();
                    Iterator iterator = cacheMap.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry entry  = (Map.Entry) iterator.next();
                        String columnName = (String) entry.getKey();
                        Integer columnIndex = cursor.getColumnIndex(columnName);
                        Field field= (Field) entry.getValue();
                        Class type = field.getType();
                        if (columnIndex!=-1){
                            if (type == String.class) {
                              field.set(item,cursor.getString(columnIndex));
                            } else if (type == Integer.class) {
                                field.set(item,cursor.getInt(columnIndex));
                            } else if (type == Long.class) {
                                field.set(item,cursor.getLong(columnIndex));
                            } else if (type == Double.class) {
                                field.set(item,cursor.getDouble(columnIndex));
                            } else if (type == byte[].class) {
                                field.set(item,cursor.getBlob(columnIndex));
                            } else {
                                continue;
                            }
                        }

                    }
                    list.add(item);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        return list;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }

        return contentValues;
    }

    /**
     * 根据实体类获取对于
     *
     * @param entity
     * @return
     */
    private Map<String, String> getValue(T entity) {
        HashMap<String, String> map = new HashMap<>();
        //返回的是所有的成员变量
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next(); //获取成员变量
            field.setAccessible(true); //设置修饰符的权限
            try {
                Object obj = field.get(entity); //通过成员变量映射实体类获取成员变量的具体值
                if (obj == null) {
                    continue;
                }
                String value = obj.toString();
                //获取列名
                String key = null;
                if (field.getAnnotation(DbField.class) != null) {
                    key = field.getAnnotation(DbField.class).value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
