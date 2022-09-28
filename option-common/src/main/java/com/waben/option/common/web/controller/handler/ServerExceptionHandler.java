package com.waben.option.common.web.controller.handler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.waben.option.common.exception.ServerException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {

	@Resource
	protected HttpServletRequest request;

	@ExceptionHandler({ AuthenticationException.class })
	public ResponseEntity<?> handleAuthenticationException(Exception ex) {
		return new ResponseEntity<>(RestError.builder().restStatus(HttpStatus.UNAUTHORIZED).message(ex.getMessage()).build(),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<?> handleAccessDeniedException(Exception ex) {
		return new ResponseEntity<>(RestError.builder().restStatus(HttpStatus.FORBIDDEN).message(ex.getMessage()).build(),
				HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({ ServerException.class })
	public ResponseEntity<?> handleApiException(ServerException ex) {
		log.error("", ex);
		return new ResponseEntity<>(RestError.builder().code(ex.getCode()).message(ex.getMsg()).build(), HttpStatus.OK);
	}
	
	@ExceptionHandler({ InvocationTargetException.class })
	public ResponseEntity<?> handleApiException(InvocationTargetException ex) {
		log.error("", ex);
		Throwable targetException = ex.getTargetException();
		if(targetException instanceof ServerException) {
			ServerException exception = (ServerException)targetException;
			return new ResponseEntity<>(
					RestError.builder().code(exception.getCode()).message(ex.getMessage()).build(), HttpStatus.OK);
		}
		return new ResponseEntity<>(RestError.builder().code(-1).message(ex.getMessage()).build(),
				HttpStatus.OK);
	}
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<?> handleUnknownException(Exception ex) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		String servletPath = "";
		if(servletRequestAttributes != null) {
			servletPath = "handleUnknownException path: " +  servletRequestAttributes.getRequest().getServletPath();
		}
		log.error(servletPath, ex);
		ServerException serverException = new ServerException(1000);
		return new ResponseEntity<RestError>(RestError.builder().code(serverException.getCode())
				.message(serverException.getMsg()).build(), HttpStatus.OK);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}
		return new ResponseEntity<>(RestError.builder().restStatus(status).message(ex.getMessage()).build(),
				headers, status);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<Object>(
				RestError.builder().restStatus(status).message("Message Not Readable").build(), headers, status);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		RestError restError = RestError.builder().restStatus(status).build();
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		for (FieldError fe : fieldErrors) {
			ServerException serverException = new ServerException(Integer.parseInt(fe.getDefaultMessage()));
			return new ResponseEntity<>(RestError.builder().code(serverException.getCode()).message(serverException.getMsg()).build(), HttpStatus.OK);
		}
		return new ResponseEntity<>(restError, HttpStatus.BAD_REQUEST);
	}

	@Data
	public static class RestError implements Serializable {

		private static final long serialVersionUID = 1472444186817360841L;

		private final Integer code;

		private final String msg;

		public RestError(Integer ret, String message) {
			this.code = ret;
			this.msg = message;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {

			private Integer code;
			private String msg;

			public Builder() {

			}

			public Builder restStatus(HttpStatus status) {
				this.code = status.value();
				this.msg = status.getReasonPhrase();
				return this;
			}

			public Builder code(Integer code) {
				this.code = code;
				return this;
			}

			public Builder message(String message) {
				this.msg = message;
				return this;
			}

			public RestError build() {
				return new RestError(this.code, this.msg);
			}
		}
	}

}