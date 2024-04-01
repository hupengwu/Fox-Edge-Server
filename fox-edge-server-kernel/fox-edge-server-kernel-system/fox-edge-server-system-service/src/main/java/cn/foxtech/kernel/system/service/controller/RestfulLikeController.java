package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.utils.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestfulLikeController {
    private final Map<String, Object> controllerMethod = new HashMap<>();
    @Autowired
    private ApplicationContext applicationContext;


    public void initialize() {
        String[] beanDefinitionNames = this.applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            Object bean = this.applicationContext.getBean(name);
            if (bean == null) {
                continue;
            }

            Class clazz = bean.getClass();

            // 检测：是否为Controller，根据该Bean对象是否包含RequestMapping注解判断
            if (!clazz.isAnnotationPresent(RequestMapping.class)) {
                continue;
            }

            // 从类注解上取出path信息
            RequestMapping requestAnnotation = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
            String[] classPaths = requestAnnotation.value();
            if (classPaths == null || classPaths.length == 0) {
                continue;
            }

            String classPath = classPaths[0];


            // 检测方法：是否包含PostMapping等注解
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                Class[] parameterTypes = method.getParameterTypes();

                if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping operAnnotation = method.getAnnotation(PostMapping.class);
                    String[] methodPaths = operAnnotation.value();
                    if (methodPaths == null || methodPaths.length == 0) {
                        continue;
                    }

                    String res = classPath + "/" + methodPaths[0];
                    String methodKey = res + ":" + "POST";
                    Maps.setValue(this.controllerMethod, methodKey, "bean", bean);
                    Maps.setValue(this.controllerMethod, methodKey, "method", method);
                    continue;
                }
                if (method.isAnnotationPresent(PutMapping.class)) {
                    PutMapping operAnnotation = method.getAnnotation(PutMapping.class);
                    String[] methodPaths = operAnnotation.value();
                    if (methodPaths == null || methodPaths.length == 0) {
                        continue;
                    }

                    String res = classPath + "/" + methodPaths[0];
                    String methodKey = res + ":" + "PUT";
                    Maps.setValue(this.controllerMethod, methodKey, "bean", bean);
                    Maps.setValue(this.controllerMethod, methodKey, "method", method);
                    continue;
                }
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping operAnnotation = method.getAnnotation(GetMapping.class);
                    String[] methodPaths = operAnnotation.value();
                    if (methodPaths == null || methodPaths.length == 0) {
                        continue;
                    }

                    String res = classPath + "/" + methodPaths[0];
                    String methodKey = res + ":" + "GET";
                    Maps.setValue(this.controllerMethod, methodKey, "bean", bean);
                    Maps.setValue(this.controllerMethod, methodKey, "method", method);
                    continue;
                }
                if (method.isAnnotationPresent(DeleteMapping.class)) {
                    DeleteMapping operAnnotation = method.getAnnotation(DeleteMapping.class);
                    String[] methodPaths = operAnnotation.value();
                    if (methodPaths == null || methodPaths.length == 0) {
                        continue;
                    }

                    String res = classPath + "/" + methodPaths[0];
                    String methodKey = res + ":" + "DELETE";
                    Maps.setValue(this.controllerMethod, methodKey, "bean", bean);
                    Maps.setValue(this.controllerMethod, methodKey, "method", method);
                    continue;
                }
            }
        }
    }

    public String getResource(String url) {
        int index = url.indexOf("?");
        if (index < 0) {
            return url;
        }

        return url.substring(0, index);
    }

    public List<Object> getParams(String url, Method method) {
        int index = url.indexOf("?");
        if (index < 0) {
            return new ArrayList<>();
        }

        String params = url.substring(index + 1);

        List<String> lines = new ArrayList<>();
        index = params.indexOf("&");
        if (index < 0) {
            lines.add(params);
        }

        List<String> itemList = new ArrayList<>();
        for (String line : lines) {
            String[] items = line.split("=");
            if (items.length != 2) {
                continue;
            }

            itemList.add(items[1]);
        }

        Class[] paramTypes = method.getParameterTypes();

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < paramTypes.length; i++) {
            Class clazz = paramTypes[i];
            String param = itemList.get(i);


            if (clazz.equals(String.class)) {
                result.add(param);
            } else if (clazz.equals(Integer.class)) {
                result.add(Integer.parseInt(param));
            } else if (clazz.equals(Long.class)) {
                result.add(Long.parseLong(param));
            } else if (clazz.equals(Float.class)) {
                result.add(Float.parseFloat(param));
            } else if (clazz.equals(Double.class)) {
                result.add(Double.parseDouble(param));
            }
        }

        return result;
    }

    public Object getBean(String methodKey) {
        return Maps.getValue(this.controllerMethod, methodKey, "bean");
    }

    public Object getMethod(String methodKey) {
        return Maps.getValue(this.controllerMethod, methodKey, "method");
    }
}
