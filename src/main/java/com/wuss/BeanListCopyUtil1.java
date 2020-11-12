package com.wuss;

import com.alibaba.fastjson.JSON;

import com.wuss.util.MethodUtil;
import lombok.Data;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program tmc
 * @description:
 * @author: wuss@wjs.com
 * @create: 2020/09/21 16:48
 */
public class BeanListCopyUtil1 {




    /**
     * Spring beanUtil 拷贝
     * @author: wuss@wjs.com
     * @date: 2020-09-22 2:40 PM
     * @param: [originList, targetClass]
     * @return: java.util.List<V>
     */
    static <T, V> List<V> copyListBeanUtils(List<T> originList, Class<V> targetClass) {
        List<V> result = new ArrayList<>(originList.size());
        for (T record : originList) {
            try {
                V v = targetClass.newInstance();
                BeanUtils.copyProperties(record, v);
                result.add(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }



    public static <T, V> List<V> copyListJson(List<T> originList, Class<V> targetClass) {
        List<V> result = new ArrayList<>(originList.size());
        for (T record : originList) {
            try {
//                V v = targetClass.newInstance();

                result.add(JSON.parseObject(JSON.toJSONString(record), targetClass));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 自己实现的list 拷贝，效率比 spring 的效率高
     *
     * @author: wuss@wjs.com
     * @date: 2020-09-22 2:40 PM
     * @param: [orgList, orgClass, targetClass]
     * @return: java.util.List<T>
     */
    public static <T, K> List<T> copyList(List<K> orgList, Class<K> orgClass, Class<T> targetClass) {

        List<T> resultList = new ArrayList<>(orgList.size());
        if (CollectionUtils.isEmpty(orgList) || targetClass == null || orgClass == null) {
            return resultList;
        }

        List<Method> orgAllMethod = MethodUtil.getAllMethod(orgClass);
        List<Method> targetAllMethod = MethodUtil.getAllMethod(targetClass);
        Map<String, Method> getMap = MethodUtil.filterAndInitMap(orgAllMethod, "get");
        Map<String, Method> setMap = MethodUtil.filterAndInitMap(targetAllMethod, "set");

        MethodUtil.checkParameterType(getMap, setMap);
        Set<Map.Entry<String, Method>> getMapEntry = setMap.entrySet();


        Method getMethod, setMethod;
        for (int limit = orgList.size(), i = 0; i < limit; i++) {
            K record = orgList.get(i);
            try {
                T target = targetClass.newInstance();
                for (Map.Entry<String, Method> entry : getMapEntry) {
                    getMethod = getMap.get(entry.getKey());
                    if (getMethod == null) {
                        continue;
                    }
                    setMethod = entry.getValue();
                    setMethod.invoke(target, new Object[]{getMethod.invoke(record)});
                }


                resultList.add(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultList;

    }


    private static List<Field> getAllField(Class clazz) {
        List<Field> list = new ArrayList<>();
        while (true) {
            if (Object.class.equals(clazz)) {
                break;
            }
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                list.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }


//    private static Map<String, Method> initMap(List<Method> methodList, String methodNamePre) {
//        Map<String, Method> methodMap = new HashMap<>();
//        int preLen = methodNamePre.length();
//        for (int i = methodList.size() - 1; i >= 0; i--) {
//            Method method = methodList.get(i);
//            String methodName = method.getName();
//            if (methodName.startsWith(methodNamePre)) {
//                char[] chars = methodName.toCharArray();
//                int len = chars.length;
//                if (len <= preLen) {
//                    continue;
//                }
//                chars[preLen] = Character.toLowerCase(chars[preLen]);
//                String filedName = new String(chars, preLen, len - preLen);
//                methodMap.put(filedName, method);
//            }
//
//        }
//        return methodMap;
//    }




}
