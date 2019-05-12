package com.hk.ioclibrary;

import android.app.Activity;
import android.view.View;

import com.hk.ioclibrary.annotation.ContentView;
import com.hk.ioclibrary.annotation.EventBase;
import com.hk.ioclibrary.annotation.InjectView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by hk on 2019/5/12.
 */
public class InjectManager {

    public static void inject(Activity activity) {

        injectLayout(activity);
        injectView(activity);
        injectEvents(activity);

    }

    private static void injectLayout(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {
            int layoutId = contentView.value();

            try {
                //                activity.setContentView(layoutId);
                Method setContentView = clazz.getMethod("setContentView", int.class);
                setContentView.invoke(activity, layoutId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static void injectView(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            InjectView injectView = declaredField.getAnnotation(InjectView.class);
            if (injectView != null) {
                int viewId = injectView.value();

                try {
                    Method findViewById = clazz.getMethod("findViewById", int.class);
                    Object view = findViewById.invoke(activity, viewId);
                    declaredField.setAccessible(true);
                    declaredField.set(activity, view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectEvents(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType != null) {
                    EventBase eventBase = annotationType.getAnnotation(EventBase.class);

                    if (eventBase != null) {
                        String listenerSetter = eventBase.listenerSetter();
                        Class<?> listenerType = eventBase.listenerType();
                        String callBackListener = eventBase.callBackListener();

                        ListenerInvocationHandler handler = new ListenerInvocationHandler(activity);
                        handler.addMethod(callBackListener, method);

                        Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(),
                                new Class[]{listenerType}, handler);
                        try {
                            Method valueMethod = annotationType.getDeclaredMethod("value");
                            int[] valueIds = (int[]) valueMethod.invoke(annotation);

                            for (int valueId : valueIds) {

                                View view = activity.findViewById(valueId);
                                Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                                setter.invoke(view, listener);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }
}
