package com.github.joyrun;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public class TrackCondition extends BaseModel {

    @PrimaryKey
    String event;  //埋点的字段

//    @Column
//    String currentPagerName; //带包名的类名
//
//    @Column
//    String fromPagerName;//进入前的pager
//
//    @Column
//    int trigViewId;//触发这个行为的 空间的id

    @Column
    String conditionJson;
}