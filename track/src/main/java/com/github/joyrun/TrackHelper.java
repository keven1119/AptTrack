package com.github.joyrun;

import android.content.Context;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keven on 16/9/22.
 */

public class TrackHelper {

    public static List<TrackCondition> trackConditionDatas = new ArrayList<>();
    private static volatile boolean sIsInit = false;

    public static void initAptTrack(Context context){
        FlowManager.init(context);

        for (TrackCondition trackCondition : trackConditionDatas){

        }
    }

    public static void saveModelTransaction(final List<? extends Model> models) {
        if (models == null || models.size() == 0) {
            return;
        }
        DatabaseDefinition database = FlowManager.getDatabaseForTable(models.get(0).getClass());
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                int count = models.size();
                for (int index = 0; index < count; index++) {
                    if (models.get(index).exists()) {
                        models.get(index).update();
                    } else {
                        models.get(index).save();
                    }
                }
            }
        }).build();
        transaction.execute();
    }

    @DebugLog
    public static void init(Context context, int userId) {
        context = context.getApplicationContext();
        FlowConfig.Builder flowConfig = new FlowConfig.Builder(context).openDatabasesOnInit(true);
        if (!sIsInit) {
            FlowManager.init(flowConfig.build());
        }

        sIsInit = true;
    }

}
