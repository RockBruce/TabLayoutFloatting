package cn.edsmall.lib_section.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseDaoFactory {

    //这里直接给数据库名
    private static final Application application;

    //反射获取application
    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            Log.e("BaseDaoFactory", "Failed to get current application from AppGlobals." + e.getMessage());
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                Log.e("BaseDaoFactory", "Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            application = app;
        }
    }

    private static final BaseDaoFactory ourInstance = new BaseDaoFactory();

    public static BaseDaoFactory getOurInstance() {

        return ourInstance;
    }

    private SQLiteDatabase sqLiteDatabase;

    //定义见数据库的路径d:建议写在SD卡中，好处app删除了，下次安装的时候数据还在
    private String sqLiteDataBasePath;

    private BaseDaoFactory() {
        Log.e("BaseDaoFactory", "有否有SD卡=" + checkSdCard());
        try {
            sqLiteDataBasePath = application.getFilesDir().toString() + "/" + "statistics.db";
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLiteDataBasePath, null);
        } catch (Exception e) {

        }

    }

    /**
     * 检查SD卡是否存在
     */
    private static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 用来生产BaseDao对象
     */
    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;

        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return baseDao;
    }


}
