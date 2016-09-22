package com.github.joyrun;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2016/9/22 0022.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface TrackPager {

    int openTime();

    int trackTarget();
}
