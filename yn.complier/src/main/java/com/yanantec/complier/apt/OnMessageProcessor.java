package com.yanantec.complier.apt;

import com.google.auto.service.AutoService;

import java.util.LinkedHashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/11/16
 * Describe:
 */
@AutoService(OnMessageProcessor.class)
public class OnMessageProcessor extends AbstractProcessor
{
    private Types mTypeUtils;
    private Messager mMessager;
    private Filer mFiler;
    private Elements mElementUtils;
//    private Map<String, FactoryGroupedClasses> factoryClasses = new LinkedHashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        return false;
    }
}
