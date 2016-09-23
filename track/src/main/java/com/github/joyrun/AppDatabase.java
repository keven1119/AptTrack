package com.github.joyrun;

import android.database.Cursor;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.QueryBuilder;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 数据库定义类
 * 数据库版本:
 * 2.0.0 = 6
 * 2.1.0 = 7
 * 2.1.0.0720 = 9
 * 2.1.0.0721 = 10
 * 2.2.2.0805 = 11
 * 2.2.2.0808 = 12
 * 2.3.0 = 13
 * Created by Wiki on 16/3/2.
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    //数据库名称
    public static final String NAME = "AppDatabase";
    //数据库版本号
    public static final int VERSION = 13;

    @Migration(version = AppDatabase.VERSION, database = AppDatabase.class)
    public static class Migration2 extends BaseMigration {


        @Override
        public void migrate(DatabaseWrapper database) {
            Log.d("joyrun","==>数据库升级");
            List<Class<? extends Model>> classes = FlowManager.getDatabase(AppDatabase.NAME).getModelClasses();

            for (Class c : classes) {
                try {
                    Cursor cursor = database.rawQuery("SELECT * FROM " + c.getSimpleName(), null);
                    Field[] fields = c.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Column.class)) {
                            if (cursor.getColumnIndex(field.getName()) < 0) {
                                //缺少的字段
                                QueryBuilder queryBuilder = new QueryBuilder().append("ALTER")
                                        .appendSpaceSeparated("TABLE")
                                        .appendSpaceSeparated(c.getSimpleName())
                                        .appendSpaceSeparated("ADD COLUMN")
                                        .appendSpaceSeparated(QueryBuilder.quoteIfNeeded(field.getName()));
                                String sql = queryBuilder.getQuery();
                                Log.d("joyrun","==>缺少字段");
                                database.execSQL(sql);
                            }
                        }
                    }
                    cursor.close();
                } catch (Exception e) {
                    Log.d("joyrun","exception ==>" + e.toString());
                }
            }

        }
    }
}
