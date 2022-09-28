package com.waben.option.core.aop;

//@Slf4j
//@Component
//@Aspect
public class MasterAspect {

//    @Pointcut("@annotation(com.waben.option.common.annotation.Master)")
//    public void execute() {
//    }
//
//    @Around("execute()")
//    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
//        HintManager.getInstance().setMasterRouteOnly();
//        return pjp.proceed();
//    }
//
//    @Before("within(@com.waben.option.common.annotation.TargetDataSource *) || @annotation(com.waben.option.common.annotation.TargetDataSource)")
//    public void changeDataSource(JoinPoint point) {
//        MethodSignature joinPointObject = (MethodSignature) point.getSignature();
//        TargetDataSource targetDataSource = null;
//        if (joinPointObject.getDeclaringType().isAnnotationPresent(TargetDataSource.class)) {
//            targetDataSource = (TargetDataSource) joinPointObject.getDeclaringType().getAnnotation(TargetDataSource.class);
//        }
//        Method method = joinPointObject.getMethod();
//        if (method.isAnnotationPresent(TargetDataSource.class)) {
//            targetDataSource = method.getAnnotation(TargetDataSource.class);
//        }
//        if (targetDataSource.isDatabaseShardingOnly()) {
//            //获取当前的指定的数据源;
//            DataSourceType dsId = targetDataSource.value();
//            HintManager.getInstance().setDatabaseShardingValue(dsId.getIdentity());
//        }
//
//    }
//    
//    @After(value = "@annotation(com.waben.option.common.annotation.TargetDataSource)")
//    public void restoreDataSource(JoinPoint point) {
//
//        //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
//        HintManager.clear();
//
//    }


}
