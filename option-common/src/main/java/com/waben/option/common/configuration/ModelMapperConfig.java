package com.waben.option.common.configuration;

import com.waben.option.common.util.TimeUtil;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class ModelMapperConfig {

    private final Converter<LocalDateTime, Long> timestampConverter = new AbstractConverter<LocalDateTime, Long>() {

        @Override
        protected Long convert(LocalDateTime source) {
            if (source != null) {
                return TimeUtil.getTimeMillis(source);
            }
            return null;
        }

    };

    private final Converter<LocalDate, Long> timestampConverter2 = new AbstractConverter<LocalDate, Long>() {

        @Override
        protected Long convert(LocalDate source) {
            if (source != null) {
                return TimeUtil.getTimeMillis(source);
            }
            return null;
        }

    };

    private final Converter<Long, LocalDate> localDateConverter = new AbstractConverter<Long, LocalDate>() {

        @Override
        protected LocalDate convert(Long source) {
            if (source != null) {
                return TimeUtil.getDateTime(source).toLocalDate();
            }
            return null;
        }

    };

    private final Converter<Long, LocalDateTime> localDateTimeConverter = new AbstractConverter<Long, LocalDateTime>() {

        @Override
        protected LocalDateTime convert(Long source) {
            if (source != null) {
                return TimeUtil.getDateTime(source);
            }
            return null;
        }

    };

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.addConverter(timestampConverter);
        modelMapper.addConverter(timestampConverter2);
        modelMapper.addConverter(localDateTimeConverter);
        modelMapper.addConverter(localDateConverter);
        return modelMapper;
    }

}
