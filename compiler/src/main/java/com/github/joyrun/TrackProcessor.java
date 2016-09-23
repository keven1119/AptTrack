package com.github.joyrun;

import com.google.auto.service.AutoService;
import com.sun.org.apache.xalan.internal.xslt.Process;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by keven on 16/9/22.
 */

@AutoService(Processor.class)
public class TrackProcessor extends AbstractProcessor{

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(TrackPager.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() == 0) {
            return false;
        }

        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(TrackPager.class);


        return false;
    }
}
