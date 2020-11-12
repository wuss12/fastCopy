package com.wuss.test;

import com.wuss.BeanListCopyUtil1;
import com.wuss.domain.BindCardInfoVo;

import com.wuss.domain.BindCardInfoVo1;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program fastCopy
 * @description:
 * @author: wuss@wjs.com
 * @create: 2020/10/10 10:57
 */
public class TestFastCopy {
    @Test
    public void testResult(){

        List<BindCardInfoVo1> list = new ArrayList<>();
        int limit = 20;
        for (int i = 0; i < limit; i++) {
            BindCardInfoVo1 vo1 = new BindCardInfoVo1();
            vo1.setBankName("bankName" + i);
            vo1.setMoney(new BigDecimal(i));
            vo1.setBankAccoName("bankAccoName" + i);
            vo1.setBankAccount("bankAccount" + i);
            vo1.setBankNo("bankNo" + i);
            vo1.setCnapsCode("cnapsCode" + i);
            vo1.setCreateDate(20101111);
            vo1.setCreateDatetime(1L + i);
            vo1.setCustomerId("customerId" + i);
            vo1.setId(i + 2L);
            vo1.setProtocolId("proId" + i);
            list.add(vo1);
        }

        long start = System.currentTimeMillis();
        List<BindCardInfoVo> bindCardInfoVos1 = BeanListCopyUtil1.copyList(list, BindCardInfoVo1.class, BindCardInfoVo.class);
        long start1 = System.currentTimeMillis();
        List<BindCardInfoVo> bindCardInfoVos = copyListBeanUtils(list, BindCardInfoVo.class);
        long start2 = System.currentTimeMillis();
        List<BindCardInfoVo> bindCardInfoVos2 = BeanListCopyUtil1.copyListJson(list, BindCardInfoVo.class);
        long start3 = System.currentTimeMillis();



        System.out.println("start1:"+(start1 - start));
        System.out.println("start2:"+(start2 - start1));
        System.out.println("start3:"+(start3 - start2));

        System.out.println(equal(bindCardInfoVos1,bindCardInfoVos));
        System.out.println(equal(bindCardInfoVos2,bindCardInfoVos));


    }


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


    static boolean equal(List<BindCardInfoVo> list1, List<BindCardInfoVo> list2){
        int size = list1.size();
        if (size  != list2.size()){
            return false;
        }
        List<Method> allMethod = getAllMethod(BindCardInfoVo.class);
        Map<String, Method> getMap = initMap(allMethod, "get");
        for (int i=0;i<size;i++){

            BindCardInfoVo vo1 = list1.get(i);
            BindCardInfoVo vo2 = list2.get(i);
            for (Method value : getMap.values()) {
                try {
                    Object invoke = value.invoke(vo1);
                    Object invoke1 = value.invoke(vo2);
                    boolean equals = ObjectUtils.equals(invoke1, invoke);
                    if (!equals){
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


    private static List<Method> getAllMethod(Class clazz) {
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

        private static Map<String, Method> initMap(List<Method> methodList, String methodNamePre) {
        Map<String, Method> methodMap = new HashMap<>();
        int preLen = methodNamePre.length();
        for (int i = methodList.size() - 1; i >= 0; i--) {
            Method method = methodList.get(i);
            String methodName = method.getName();
            if (methodName.startsWith(methodNamePre)) {
                char[] chars = methodName.toCharArray();
                int len = chars.length;
                if (len <= preLen) {
                    continue;
                }
                chars[preLen] = Character.toLowerCase(chars[preLen]);
                String filedName = new String(chars, preLen, len - preLen);
                methodMap.put(filedName, method);
            }

        }
        return methodMap;
    }
}
