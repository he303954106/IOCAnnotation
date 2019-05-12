package com.hk.ioclibrary;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by hk on 2019/5/12.
 */
public class ListenerInvocationHandler implements InvocationHandler {

    private Object target;
    private HashMap<String, Method> map = new HashMap<>();

    public ListenerInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        method = map.get(methodName);
        if (method != null) {
            if (method.getGenericParameterTypes().length == 0) {
                return method.invoke(target);
            }
            return method.invoke(target, args);
        }
        return null;
    }

    public void addMethod(String methodName, Method method) {
        map.put(methodName, method);
    }
}
