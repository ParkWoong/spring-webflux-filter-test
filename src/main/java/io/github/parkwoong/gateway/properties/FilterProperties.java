package io.github.parkwoong.gateway.properties;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("filter")
public class FilterProperties {

    public static final String ID = "glob_id";
    public static final String DATA = "data";

    private String defaultGlobIdGenerationInfix;
}
