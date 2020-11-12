package com.wuss.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @program fastCopy
 * @description:
 * @author: wuss@wjs.com
 */
public class MethodUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodUtil.class);


    public static List<Method> getAllMethod(Class clazz) {
        List<Method> list = new ArrayList<>();
        while (true) {
            if (Object.class.equals(clazz)) {
                break;
            }
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                method.setAccessible(true);
                list.add(method);
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }


    /**
     * 筛选指定前缀的Method
     * @author: wuss@wjs.com
     * @date: 2020-10-10 11:06 AM
     * @param: [methodList, methodNamePre]
     * @return: java.util.Map<java.lang.String,java.lang.reflect.Method>
     */
    public static Map<String, Method> filterAndInitMap(List<Method> methodList, String methodNamePre) {
        if (CollectionUtils.isEmpty(methodList) ){
            throw new RuntimeException("methodList can not be null.");
        }
        if (StringUtils.isBlank(methodNamePre)){
            methodNamePre = "";
        }
        Map<String, Method> methodMap = new HashMap<>();
        int preLen = methodNamePre.length();
        for (int i = methodList.size() - 1; i >= 0; i--) {
            Method method = methodList.get(i);
            String methodName = method.getName();
            if (methodName.startsWith(methodNamePre)) {
                methodMap.put(methodName.substring(preLen), method);
            }

        }
        return methodMap;
    }


    public static void checkParameterType(Map<String, Method> getMethodMap, Map<String, Method> setMethodMap) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, Method>> iterator = setMethodMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Method> entry = iterator.next();
            String key = entry.getKey();
            Method setMethod = entry.getValue();
            Method getMethod = getMethodMap.get(key);
            if (getMethod == null) {
                iterator.remove();
                continue;
            }

            Class returnType = getMethod.getReturnType();
            Class<?>[] parameterTypes = setMethod.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                if (!parameterType.equals(returnType)) {
                    stringBuilder.append(key+":"+returnType.getName() + ",和" + parameterType.getName() + " 不匹配;");
                    iterator.remove();
                }
            }

        }
        if (stringBuilder.length() > 0){
            LOGGER.info("拷贝存在同名参数类型不一致，结果如下："+ stringBuilder.toString());
        }
    }
}
