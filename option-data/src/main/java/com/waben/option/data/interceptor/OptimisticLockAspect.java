package com.waben.option.data.interceptor;

import com.waben.option.common.exception.ServerException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OptimisticLockAspect {

	@AfterReturning(pointcut = "execution(* com.waben.option.data.repository.BaseRepository.update*(..))",
			returning = "retValue")
	public void afterExecute(final JoinPoint joinPoint, int retValue) {
		if(retValue == 0) {
			throw new ServerException(1009);
		}
	}
	
}
