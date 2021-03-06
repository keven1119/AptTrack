package com.github.joyrun;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.oracle.tools.packager.RelativeFileSet;
import com.squareup.javapoet.ClassName;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by keven on 16/9/21.
 */

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {


    private Elements elementUtils;

    private Types typeUtils;

    private Filer filer;

    private static final ClassName VIEW_BINDER = ClassName.get("com.github.joyrun.apt","ViewBinder");

    private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";//生成类的后缀 以后会用反射去取

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();

        types.add(BindView.class.getCanonicalName());

        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

//        LinkedHashMap<TypeElement, List<FieldViewBinding>> targetClassMap = new LinkedHashMap<>();
//        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)){
//            if(!SuperficialValidation.validateElement(element))
//                continue;
//
//            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
//
//            boolean hasError = isInaccessibleViaGeneratedCode(BindView.class,"fields",element) || isBindingInWrongPackage(BindView.class,element);
//
//            TypeMirror elementType = element.asType();
//
//            if(elementType.getKind() == TypeKind.TYPEVAR){
//                TypeVariable typeVariable = (TypeVariable)elementType;
//
//                elementType = typeVariable.getUpperBound();
//            }
//
////            if(!isSubtypeO)
//
//        }
//
        return false;
    }


    private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
                                                   String targetThing, Element element){
        boolean hasError = false;

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify method modifiers.
        Set<Modifier> modifiers = element.getModifiers();

        if(modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)){
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,String.format("@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(),targetThing, enclosingElement.getQualifiedName(), element.getSimpleName()));

            hasError = true;
        }

        // Verify containing type
        if(enclosingElement.getKind() != ElementKind.CLASS){
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,String.format("@%s %s may only be contained in classes. (%s.%s)",
            annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName()));

            hasError = true;
        }

        // Verify containing class visibility is not private.
        // Verify containing class visibility is not private.
        if (enclosingElement.getModifiers().contains(Modifier.PRIVATE)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@%s %s may not be contained in private classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName()));
            hasError = true;
        }

        return hasError;
    }

    private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass,
                                            Element element){
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if(qualifiedName.startsWith("android.")){
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
        }

        if (qualifiedName.startsWith("java.")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
            return true;
        }

        return false;

    }

    private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType){
        if(otherType.equals(typeMirror.toString())){
            return true;
        }

        if(typeMirror.getKind() != TypeKind.DECLARED){
            return false;
        }

        DeclaredType declaredType = (DeclaredType)typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        if(typeArguments.size() > 0){
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());

            typeString.append('<');
            for(int i = 0; i < typeArguments.size(); i++){
                if(i > 0){
                    typeString.append(';');
                }

                typeString.append('?');
            }

            typeString.append('>');

            if(typeString.toString().equals(otherType)){
                return true;
            }
        }

        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInterface(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == ElementKind.INTERFACE;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
