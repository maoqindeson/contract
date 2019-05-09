package com.contract.wechat.sgin.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
@Aspect
@Component
public class WebLogAspect {


    @Pointcut("@annotation(com.contract.wechat.sgin.aop.WebRecord)")
    public void webLog() {

    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String MethodName = joinPoint.getSignature().getName();
        // 记录下请求内容
        log.warn("请求URL为 : " + MethodName + " " + request.getRequestURL().toString());
//        logger.info("HTTP_METHOD : " + request.getMethod());
//        logger.info("IP : " + request.getRemoteAddr());
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String name = (String) enu.nextElement();
            log.warn("name:{},value:{}", name, request.getParameter(name));
        }
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
//        if (!url.contains("List")) {
            // 处理完请求，返回内容
            log.warn(url+"[baselog][timeout] 接口返回值 : " + ret);
//        }
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        long time = end - start;
        if (time > 1000) {
            log.error("[baselog][timeout] 接口响应时间过长 : " + url + " 耗时 : " + time + "毫秒");
        } else {
            log.warn("[baselog][timeout] 接口: " + url + " 耗时" + time + "毫秒");
        }
        return result;
    }
}