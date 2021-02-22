package com.yanantec.complier.apt;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yanantec.annotation.LinkedHashMapCreate;
import com.yanantec.annotation.MapCreate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/22
 * Describe
 */
@AutoService(Processor.class)
public class LinkedHashMapProcessor extends BaseProcessor
{

    // 用于存储map属性名和map存储的
    private Map<String, List<MapBean>> actionMaps = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "---MapProcessor-process----:");
        actionMaps.clear();
        Set<? extends Element>  elements = roundEnv.getElementsAnnotatedWith(LinkedHashMapCreate.class);
        // 筛选注解
        Iterator<? extends  Element> iterator = elements.iterator();
        while (iterator.hasNext()){
            Element element = iterator.next();
            if (element.getKind() == ElementKind.CLASS){
                LinkedHashMapCreate linkedHashMapCreate = element.getAnnotation(LinkedHashMapCreate.class);
                String filedName = linkedHashMapCreate.mapFiled();
                List<MapBean> elementList;
                if (actionMaps.containsKey(filedName)){
                    elementList = actionMaps.get(filedName);
                }else {
                    elementList = new ArrayList<>();
                }
                elementList.add(new MapBean(linkedHashMapCreate.position(), (TypeElement) element));
                Collections.sort(elementList);
                actionMaps.put(filedName, elementList);
            }
        }

        // 生成MAP类
        if (actionMaps.size() > 0){
            // 创建一个类
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder("KimLinkedHashMap").addModifiers(PUBLIC);
            CodeBlock.Builder codeBuilder =CodeBlock.builder();
            // 用于提示统一个集合是否存在重复的key
            Map<String, String> allrRepeatKey = new HashMap<>();
            for (Map.Entry<String, List<MapBean>> entry : actionMaps.entrySet())
            {
                List<MapBean> fileds = entry.getValue();
                // 判断当前map属性名所包含的键值对是否为空
                if (fileds == null || fileds.size() < 0) continue;
                // map对应的属性名称
                String mapName = entry.getKey();
                // HashMap
                ClassName linkedHashMapClasssName = ClassName.get("java.util","LinkedHashMap");
                // String
                ClassName stringClassName = ClassName.get("java.lang", "String");
                // HashMap<String, String>
                TypeName linkedHashMapStringClassName = ParameterizedTypeName.get(linkedHashMapClasssName, stringClassName, stringClassName);
                // 生成Map属性，并初始化,生成代码：P  public static final HashMap MAP_CREATE = new HashMap<String, String>();
                FieldSpec fieldSpec = FieldSpec.builder(LinkedHashMap.class, mapName)
                        .addModifiers(PUBLIC, FINAL, STATIC)
                        .initializer("new $T()", linkedHashMapStringClassName)
                        .build();
                typeBuilder.addField(fieldSpec);

                // 添加类名和key的对应关系
                Iterator<MapBean> iterator1 = fileds.iterator();
                // 找出类中重复的元素:<key值, 类名>，
                Map<String, String> repeatKeys = new HashMap<>();
                while (iterator1.hasNext()){
                    // 静态代码块中存入key-类名
                    TypeElement element = iterator1.next().element;
                    String value = element.getQualifiedName().toString();
                    String key = element.getAnnotation(LinkedHashMapCreate.class).key();
                    codeBuilder.addStatement("$L.put($S,$S)", mapName, key, value);
                    if (repeatKeys.containsKey(key)){
                        // 保存重复的
                        String oldValue = repeatKeys.get(key);
                        if (!allrRepeatKey.containsKey(oldValue)){
                            allrRepeatKey.put(oldValue, key);
                        }
                        allrRepeatKey.put(value, key);
                    }else {
                        repeatKeys.put(key, value);
                    }
                }

                if (allrRepeatKey.size() > 0){
                    for (Map.Entry<String, String> stringEntry : allrRepeatKey.entrySet())
                    {
                        mMessager.printMessage(Diagnostic.Kind.ERROR, stringEntry.getKey() + "中的key:" + stringEntry.getValue() + ",已在其他类中存在");
                    }
                    return false;
                }
            }
            // 构建类文件
            TypeSpec typeSpec = typeBuilder.addStaticBlock(codeBuilder.build()).build();
            // 生成.java文件
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, typeSpec).build();
            try
            {
                javaFile.writeTo(mFiler);
            } catch (Exception e)
            {
                e.printStackTrace();
                mMessager.printMessage(Diagnostic.Kind.ERROR, "写文件错误:" + e);
            }

        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        //        Set<String> types =   new LinkedHashSet<>();
        //        types.add(MapCreate.class.getCanonicalName());
        //        return types;
        return Collections.singleton(LinkedHashMapCreate.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }


    @Override
    public Set<String> getSupportedOptions()
    {
        Set<String> stringSet = new LinkedHashSet<>();
        stringSet.add("kim.applicationId");
        return stringSet;
    }

    class MapBean implements Comparable{
        int position;
//        String className;
        TypeElement element;

        public MapBean(int position, TypeElement element)
        {
            this.position = position;
//            this.className = className;
            this.element = element;
        }

        @Override
        public int compareTo(Object o)
        {
            if (o instanceof MapBean){
                return position > ((MapBean) o).position ? 1 : -1;
            }else return 0;
        }
    }
}
