package com.lj.cloudbox.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "projectconfig")
@Data
public class ProjectProperties {
    private String home;
}
