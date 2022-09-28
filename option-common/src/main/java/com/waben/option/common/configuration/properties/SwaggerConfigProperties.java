package com.waben.option.common.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.swagger")
@Data
public class SwaggerConfigProperties {

    private String title;

    private String description;

    private String contactName;

    private String contactEmail;

    private String contactUrl;

    private String version;

    private boolean enable;

}
