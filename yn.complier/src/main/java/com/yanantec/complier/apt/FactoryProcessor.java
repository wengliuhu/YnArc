package com.yanantec.complier.apt;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yanantec.annotation.AbstractFactory;
import com.yanantec.annotation.Produce;
import com.yanantec.annotation.Product;
import com.yanantec.complier.apt.util.ProcessorUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.xml.crypto.Data;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/3
 * Describe:抽象工厂自动生成
 */
@AutoService(Processor.class)
public class FactoryProcessor extends BaseProcessor
{
    // 所有要要处理的抽象工厂
    private List<FactoryBean> mFactoryList = new ArrayList<>();
    // <父类名， 子类集合>
    private Map<String,List<TypeElement>> productsMap = new HashMap<>();

    private boolean dealed;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        try
        {
//            mLogUtil.d("-----FactoryProcessor---process-----id:" + FactoryProcessor.this);
            if (dealed) return false;
            filterAnnotations(roundEnv);

            Iterator<FactoryBean> iterator = mFactoryList.iterator();
            while (iterator.hasNext()){
                generateJavaFile(iterator.next());
            }
            dealed = true;
        } catch (Exception e)
        {
            e.printStackTrace();
            mLogUtil.e(e);
        }
        return true;
    }

    /**
     * 筛选注解
     */
    private boolean filterAnnotations(RoundEnvironment roundEnv) throws ClassNotFoundException
    {
        Set<? extends Element> factoryElements = roundEnv.getElementsAnnotatedWith(AbstractFactory.class);
        Set<? extends Element> productElements = roundEnv.getElementsAnnotatedWith(Product.class);
//        Set<? extends Element> produceElements = roundEnv.getElementsAnnotatedWith(Produce.class);
        // 筛选产品
        Iterator<? extends Element> productIterator = productElements.iterator();
        while (productIterator.hasNext())
        {
            TypeElement productElement = (TypeElement) productIterator.next();
            if (!ProcessorUtil.isValidClass(productElement, mMessager, Product.class.getName())){
                continue;
            }
//            Product product = productElement.getAnnotation(Product.class);
            // 获取要转化的类名
            List<String> superNames = new ArrayList<>();
            try
            {
                for (AnnotationMirror m: productElement.getAnnotationMirrors())
                {
                    if (m.getAnnotationType().toString().equals(Product.class.getName())){
                        for (Map.Entry e : m.getElementValues().entrySet())

                        {
                            ExecutableElement key = (ExecutableElement) e.getKey();
                            if (key.getSimpleName().toString().equals("superClass")){
                                AnnotationValue value = (AnnotationValue) e.getValue();
                                superNames.add(value.getValue().toString());
                            }else if (key.getSimpleName().toString().equals("superClasses")){
                                AnnotationValue value = (AnnotationValue) e.getValue();
                                List superclasses = (List) value.getValue();
                                for (int i = 0; i < superclasses.size(); i ++){
                                    superNames.add(superclasses.get(i).toString().replace(".class", ""));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            // 当前类名
//            String childName = productElement.getQualifiedName().toString();
            // 存入
            List<TypeElement> childClassNames;
            for (String superclassName : superNames) {
                if (productsMap.containsKey(superclassName)){
                    childClassNames = productsMap.get(superclassName);
                }else {
                    childClassNames = new ArrayList<>();
                }
                childClassNames.add(productElement);
                productsMap.put(superclassName, childClassNames);
            }
        }
        //生成抽象工厂类
        Iterator<? extends Element> factoryIterator = factoryElements.iterator();
        while (factoryIterator.hasNext()){
            Element itemElement = factoryIterator.next();

            // 判断当前类是否是抽象类或者接口
            if (itemElement.getKind() != ElementKind.INTERFACE && !itemElement.getModifiers().contains(Modifier.ABSTRACT)){
                mLogUtil.e("@AbstractFactory 只能作用于接口或者抽象类");
                return false;
            }
            TypeElement factoryElement = (TypeElement) itemElement;
            List<ExecutableElement> methodList = new ArrayList<>();
            // 获取该类的所有抽象方法
            for (Element element: factoryElement.getEnclosedElements())
            {
                if (element.getAnnotation(Produce.class) != null
                        && element.getModifiers().contains(Modifier.ABSTRACT)
                        && element.getModifiers().contains(Modifier.PUBLIC)
                        && element.getKind().equals(ElementKind.METHOD)){
                    if (((ExecutableElement)element).getParameters().size() == 0 ||
                            !((ExecutableElement)element).getParameters().get(0).asType().toString().equals("java.lang.String"))
                    {
                        mLogUtil.e("@Produce 作用的方法最少有 一个java.lang.String类型的参数，且String类型参数必须是第一个参数");
                        return false;
                    }
                    methodList.add((ExecutableElement) element);
                }
            }
            if (methodList.size() > 0){
                FactoryBean factory = new FactoryBean();
                factory.setFactoryImp(factoryElement);
                factory.setMethods(methodList);
                mFactoryList.add(factory);
            }
        }
        return true;
    }

    /**
     * 生成java文件
     */
    private void generateJavaFile(FactoryBean factoryBean){
//        mLogUtil.d("-----generateJavaFile----");
        AbstractFactory factory = factoryBean.getFactoryImp().getAnnotation(AbstractFactory.class);
        String createClassName = factory.name();
        String superClassName = factoryBean.getFactoryImp().getQualifiedName().toString();
        // 新建一个类
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(createClassName);
        typeSpecBuilder.addModifiers(Modifier.PUBLIC);
//        typeSpecBuilder.addOriginatingElement()
        ClassName superInterface = ClassName.get(ProcessorUtil.getPackageName(factoryBean.factoryImp), factoryBean.factoryImp.getSimpleName().toString());
        // 增加实现接口
        if (factoryBean.getFactoryImp().getKind() == ElementKind.INTERFACE){
            typeSpecBuilder.addSuperinterface(superInterface);
        } else {// 增加继承类
            typeSpecBuilder.superclass(superInterface);
        }

        // 重写方法
        for (ExecutableElement methodE : factoryBean.methods)
        {
            if (methodE.getParameters().size() == 0) continue;
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodE.getSimpleName().toString());
            // 方法的参数部分， 第一个string 类型的key参数
            List<ParameterSpec> paramTypeNames = new ArrayList<>();
            List<? extends VariableElement> paramElements = methodE.getParameters();
            for (int i = 0; i < paramElements.size(); i ++){
                VariableElement itemParamElement = paramElements.get(i);
//                TypeName typeName = new TypeName(itemParamElement.asType().toString());
//                Type type = itemParamElement.asType();

                String itemClassName = itemParamElement.asType().toString();
                if (itemClassName != null && itemClassName.length() > 0 && itemClassName.contains(".")){
                    String paramsPackageName = itemClassName.substring(0, itemClassName.lastIndexOf("."));
                    String paramsName = itemClassName.substring(itemClassName.lastIndexOf(".") + 1);
                    TypeName itemTypeName = ClassName.get(paramsPackageName,paramsName);
                    ParameterSpec parameterSpec2 = ParameterSpec.builder(itemTypeName, itemParamElement.getSimpleName().toString()).build();
                    paramTypeNames.add(parameterSpec2);
                }
            }

            String param = methodE.getParameters().get(0).toString();
//            ParameterSpec parameterSpec = ParameterSpec.builder(String.class, param)
//                    .build();
//            paramTypeNames.add(parameterSpec);
//
//            // 参数的类型名
//            String paramsClassName = ProcessorUtil.getClassName(methodE, Produce.class.getName(), "paramas");
//            TypeName paramsTymeName = null;
//            if (paramsClassName != null && paramsClassName.length() > 0 && paramsClassName.contains(".")){
//                String paramsPackageName = paramsClassName.substring(0, paramsClassName.lastIndexOf("."));
//                String paramsName = paramsClassName.substring(paramsClassName.lastIndexOf(".") + 1);
//                paramsTymeName = ClassName.get(paramsPackageName,paramsName);
//                ParameterSpec parameterSpec2 = ParameterSpec.builder(paramsTymeName, "parames").build();
//                paramTypeNames.add(parameterSpec2);
//            }

            // 方法的返回类型
            TypeMirror returnTypeMirror = methodE.getReturnType();
            // 所有要if-else实例化的对象
            List<TypeElement> entities = productsMap.get(returnTypeMirror.toString());

            if (entities == null || entities.size() == 0) continue;
            TypeName textUtilName = ClassName.get("android.text","TextUtils");

            for (int i = 0; i < entities.size(); i ++)
            {
                TypeElement element =  entities.get(i);
                String key = element.getAnnotation(Product.class).key();
                TypeName backEntity = ClassName.get(ProcessorUtil.getPackageName(element), element.getSimpleName().toString());
                if (i == 0){
                    methodSpecBuilder.beginControlFlow("if ($T.equals($S, $L))", textUtilName, key, param);
                }else {
                    methodSpecBuilder.nextControlFlow("else if ($T.equals($S, $L))", textUtilName, key, param);
                }
                if (paramTypeNames.size() > 1){
                    StringBuilder builder = new StringBuilder("return new $T(");
                    Iterator<ParameterSpec> iterator = paramTypeNames.iterator();
                    iterator.next();
                    Object paramFiledNames[] = new Object[paramTypeNames.size()];
                    paramFiledNames[0] = backEntity;
                    for (int k = 1; k < paramTypeNames.size(); k ++){
                        builder.append("$L");
                        if (k != paramTypeNames.size() - 1){
                            builder.append(",");
                        }
                       paramFiledNames[k] = paramTypeNames.get(k).name;
                    }
                    builder.append(")");
                    methodSpecBuilder.addStatement(builder.toString(), paramFiledNames);
                }else {
                    methodSpecBuilder.addStatement("return new $T()", backEntity);
                }
            }
            methodSpecBuilder.endControlFlow()
                    .addStatement("return null");

            MethodSpec methodSpec = methodSpecBuilder.returns(TypeName.get(returnTypeMirror))
                                        .addParameters(paramTypeNames)
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.PUBLIC)
                                        .build();

            typeSpecBuilder.addMethod(methodSpec);
        }

        // 生成.java文件
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, typeSpecBuilder.build()).build();
        try
        {
            javaFile.writeTo(mFiler);
        } catch (Exception e)
        {
            e.printStackTrace();
            mLogUtil.e(e);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set set =  super.getSupportedAnnotationTypes();
        set.add(AbstractFactory.class.getCanonicalName());
        set.add(Produce.class.getCanonicalName());
        set.add(Product.class.getCanonicalName());
        return set;
    }

    /**
     * 抽象工厂模型
     */
    class FactoryBean
    {
        // 工厂
        TypeElement factoryImp;
        // 生产的方法
        List<ExecutableElement> methods;

        public TypeElement getFactoryImp()
        {
            return factoryImp;
        }

        public void setFactoryImp(TypeElement factoryImp)
        {
            this.factoryImp = factoryImp;
        }

        public List<ExecutableElement> getMethods()
        {
            return methods;
        }

        public void setMethods(List<ExecutableElement> methods)
        {
            this.methods = methods;
        }
    }
}
