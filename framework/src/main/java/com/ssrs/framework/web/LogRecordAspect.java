package com.ssrs.framework.web;

import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.exception.NoPrivException;
import com.ssrs.framework.web.util.LogUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller统一切点日志处理
 */
@Component
@Aspect
public class LogRecordAspect {

    @Pointcut("(within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *))")
    @SuppressWarnings("EmptyMethod")
    public void pointCut() {
    }

    @Before("(within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)) ")
    public void requestLimit(final JoinPoint joinPoint) {
        try {
            String pkg = joinPoint.getTarget().getClass().getPackage().getName();
            if (pkg.startsWith("org.springframework")) {
                return;
            }
            Method[] methods = joinPoint.getTarget().getClass().getMethods();
            List<Method> method = Arrays.asList(methods).stream()
                    .filter(m -> m.getName().equals(joinPoint.getSignature().getName()))
                    .filter(m -> m.getParameterTypes().length == joinPoint.getArgs().length)
                    .collect(Collectors.toList());
            Method target = null;
            Object[] args = joinPoint.getArgs();
            for (Method m : method) {
                boolean match = true;
                for (int i = 0; i < m.getParameterTypes().length; i++) { // 检查参数类型是否匹配
                    Class<?> type = m.getParameterTypes()[i];
                    if (type.isPrimitive()) { // 基本类型
                        try {
                            Object ntype = args[i].getClass().getField("TYPE").get(null);
                            if (!ntype.equals(type)) {
                                match = false;
                                break;
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    } else { // 对象
                        if (args[i] != null) {
                            if (!type.isInstance(args[i])) {
                                match = false;
                                break;
                            }
                        }
                    }

                }
                if (match) {
                    target = m;
                    break;
                }
            }
            if (target != null) {
                Priv[] privs = target.getAnnotationsByType(Priv.class);
                if (privs.length == 0) {
                    throw new NoPrivException("方法需要添加@Priv注解：" + joinPoint.getSignature().toString());
                }
            } else {
                throw new NoPrivException("方法未匹配：" + joinPoint.getSignature().toString());
            }
        } catch (SecurityException e) {
            // e.printStackTrace();
        }

    }


    @AfterReturning(returning = "ret", pointcut = "pointCut()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        String pkg = joinPoint.getTarget().getClass().getPackage().getName();
        if (pkg.startsWith("org.springframework")) {
            return;
        }
        LogUtils.doAfterReturning(ret);
    }

}
