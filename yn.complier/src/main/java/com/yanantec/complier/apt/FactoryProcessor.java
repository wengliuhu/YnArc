package com.yanantec.complier.apt;

import com.google.auto.service.AutoService;
import com.yanantec.annotation.AbstractFactory;
import com.yanantec.annotation.Produce;
import com.yanantec.annotation.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/3
 * Describe:抽象工厂自动生成
 */
@AutoService(Processor.class)
public class FactoryProcessor extends BaseProcessor
{
    private List<Factory> mFactoryList = new ArrayList<>();
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        filterAnnotations(annotations);
        return true;
    }

    /**
     * 筛选注解
     */
    private void filterAnnotations(Set<? extends TypeElement> annotations){

    }

    /**
     * 生成java文件
     */
    private void generateJavaFile(TypeElement element){

    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set set =  super.getSupportedAnnotationTypes();
        set.add(AbstractFactory.class);
        set.add(Produce.class);
        set.add(Product.class);
        return set;
    }

    /**
     * 抽象工厂模型
     */
    class Factory{
        // 工厂
        TypeElement factoryImp;
        List<TypeElement> products;

        public TypeElement getFactoryImp()
        {
            return factoryImp;
        }

        public void setFactoryImp(TypeElement factoryImp)
        {
            this.factoryImp = factoryImp;
        }

        public List<TypeElement> getProducts()
        {
            return products;
        }

        public void setProducts(List<TypeElement> products)
        {
            this.products = products;
        }
    }
}
