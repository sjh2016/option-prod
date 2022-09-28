package com.waben.option.common.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.converter.LongToStringConverter;
import feign.Contract;
import feign.MethodMetadata;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.core.annotation.AnnotationUtils.synthesizeAnnotation;

@Slf4j
@Configuration
public class BaseConfig implements InitializingBean {

    @Value("${workerId:0}")
    private int workerId;

    @Value("${longtostr:true}")
    private Boolean longToStr;

    @Resource
    private Environment env;

    @Bean
    public IdWorker idWorker() {
        String value = env.getProperty("_workerId");
        if (value != null) {
            workerId = Integer.parseInt(value);
        }
        log.info("value:{}, workerId:{}", value, workerId);
        System.setProperty("workerIdValue", String.valueOf(workerId));
        return new IdWorker(workerId);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
        SimpleModule simpleModule = new SimpleModule();
        if (longToStr != null && longToStr) {
            simpleModule.addSerializer(long.class, new LongToStringConverter());
            simpleModule.addSerializer(Long.class, new LongToStringConverter());
        }
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    @Bean
    public Contract feignContract() {
        DefaultConversionService service = new DefaultConversionService();
        service.addConverter(new Converter<LocalDateTime, String>() {

            private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public String convert(LocalDateTime source) {
                return source.format(df);
            }

        });
        service.addConverter(new Converter<LocalDate, String>() {

            private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String convert(LocalDate source) {
                return source.format(df);
            }

        });
        service.addConverter(new Converter<LocalTime, String>() {

            private final DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");

            @Override
            public String convert(LocalTime source) {
                return source.format(df);
            }

        });

        SpringMvcContract contract = new HierarchicalContract(Collections.emptyList(), service);
        return contract;
    }

    public static class HierarchicalContract extends SpringMvcContract {

        private ResourceLoader resourceLoader;

        public HierarchicalContract(
                List<AnnotatedParameterProcessor> annotatedParameterProcessors,
                ConversionService conversionService) {
            super(annotatedParameterProcessors, conversionService);
        }

        @Override
        public List<MethodMetadata> parseAndValidateMetadata(final Class<?> targetType) {
            Util.checkState(targetType.getTypeParameters().length == 0,
                    "Parameterized types unsupported: %s",
                    targetType.getSimpleName());
            final Map<String, MethodMetadata> result = new LinkedHashMap<>();
            for (final Method method : targetType.getMethods()) {
                if (method.getDeclaringClass() == Object.class || (method.getModifiers() & Modifier.STATIC) != 0
                        || Util.isDefault(method)) {
                    continue;
                }
                final MethodMetadata metadata = this.parseAndValidateMetadata(targetType, method);
                Util.checkState(!result.containsKey(metadata.configKey()), "Overrides unsupported: %s", metadata.configKey());
                result.put(metadata.configKey(), metadata);
            }
            return new ArrayList<>(result.values());
        }

        @Override
        public MethodMetadata parseAndValidateMetadata(final Class<?> targetType, final Method method) {
            final MethodMetadata methodMetadata = super.parseAndValidateMetadata(targetType, method);

            final LinkedList<Class<?>> classHierarchy = new LinkedList<>();
            classHierarchy.add(targetType);
            this.findClass(targetType, method.getDeclaringClass(), classHierarchy);
            classHierarchy.stream()
                    .map(this::processPathValue)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .ifPresent((path) -> methodMetadata.template().insert(0, path));
            return methodMetadata;
        }

        private Optional<String> processPathValue(final Class<?> clz) {
            Optional<String> result = Optional.empty();
            final RequestMapping classAnnotation = clz.getAnnotation(RequestMapping.class);
            if (classAnnotation != null) {
                final RequestMapping synthesizeAnnotation = synthesizeAnnotation(classAnnotation, clz);
                // Prepend path from class annotation if specified
                if (synthesizeAnnotation.value().length > 0) {
                    String pathValue = Util.emptyToNull(synthesizeAnnotation.value()[0]);
                    pathValue = this.resolveValue(pathValue);
                    if (!pathValue.startsWith("/")) {
                        pathValue = "/" + pathValue;
                    }
                    result = Optional.of(pathValue);
                }
            }
            return result;
        }

        private String resolveValue(final String value) {
            if (StringUtils.hasText(value) && this.resourceLoader instanceof ConfigurableApplicationContext) {
                return ((ConfigurableApplicationContext) this.resourceLoader).getEnvironment().resolvePlaceholders(value);
            }
            return value;
        }

        @Override
        protected void processAnnotationOnClass(final MethodMetadata data, final Class<?> clz) {
            // skip this step
        }

        private boolean findClass(final Class<?> currentClass, final Class<?> searchClass,
                                  final LinkedList<Class<?>> classHierarchy) {
            if (currentClass == searchClass) {
                return true;
            }
            final Class<?>[] interfaces = currentClass.getInterfaces();
            for (final Class<?> currentInterface : interfaces) {
                classHierarchy.add(currentInterface);
                final boolean findClass = this.findClass(currentInterface, searchClass, classHierarchy);
                if (findClass) {
                    return true;
                }
                classHierarchy.removeLast();
            }
            return false;
        }

        @Override
        public void setResourceLoader(final ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
            super.setResourceLoader(resourceLoader);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
