package cn.foxtech.kernel.system.service.mqtt;

import cn.foxtech.common.mqtt.MqttClientService;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRequestVO;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MqttProxyController {
    private final Map<String, Object> controllerMethod = new HashMap<>();
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MqttClientService clientService;

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
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping operAnnotation = method.getAnnotation(RequestMapping.class);
                    String[] methodPaths = operAnnotation.value();
                    if (methodPaths == null || methodPaths.length == 0) {
                        continue;
                    }

                    if (!classPath.equals("/kernel/manager/proxy-redis-topic")) {
                        continue;
                    }

                    String res = classPath;
                    String methodKey = res + ":" + "**";
                    Maps.setValue(this.controllerMethod, methodKey, "bean", bean);
                    Maps.setValue(this.controllerMethod, methodKey, "method", method);
                    continue;
                }
            }
        }
    }

    public RestfulLikeRespondVO execute(RestfulLikeRequestVO requestVO) {
        try {
            String resource = this.getResource(requestVO.getResource());
            String methodName = requestVO.getMethod().toUpperCase();

            String methodKey = resource + ":" + methodName;
            Object bean = Maps.getValue(this.controllerMethod, methodKey, "bean");
            Object method = Maps.getValue(this.controllerMethod, methodKey, "method");
            if (method == null || bean == null) {
                throw new ServiceException("尚未支持的方法");
            }

            // 执行controller的bean函数
            Object value = null;
            if (methodName.equals("POST") || methodName.equals("PUT")) {
                value = ((Method) method).invoke(bean, requestVO.getBody());
            } else if (methodName.equals("GET") || methodName.equals("DELETE")) {
                List<Object> params = this.getParams(requestVO.getResource(), (Method) method);
                if (params.size() == 0) {
                    value = ((Method) method).invoke(bean, params);
                } else if (params.size() == 1) {
                    value = ((Method) method).invoke(bean, params.get(0));
                } else if (params.size() == 2) {
                    value = ((Method) method).invoke(bean, params.get(0), params.get(1));
                } else if (params.size() == 3) {
                    value = ((Method) method).invoke(bean, params.get(0), params.get(1), params.get(2));
                } else {
                    throw new ServiceException("尚未支持的方法");
                }
            } else {
                throw new ServiceException("尚未支持的方法");
            }


            RestfulLikeRespondVO respondVO = new RestfulLikeRespondVO();
            respondVO.bindVO(requestVO);
            respondVO.setBody(value);

            return respondVO;
        } catch (Exception e) {
            RestfulLikeRespondVO respondVO = RestfulLikeRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindVO(requestVO);
            }

            return respondVO;
        }
    }


    private String getResource(String url) {
        int index = url.indexOf("?");
        if (index < 0) {
            return url;
        }

        return url.substring(0, index);
    }

    private List<Object> getParams(String url, Method method) {
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


    public void publish(String pubTopic, String rspContext) {
        this.clientService.getMqttClient().publish(pubTopic, rspContext.getBytes(StandardCharsets.UTF_8));

    }
}
