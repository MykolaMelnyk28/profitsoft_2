package com.melnyk.profitsoft_2.config.props;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "cors.allowed")
public class CorsProps {

    private List<String> origins;
    private List<String> methods;
    private boolean credentials;

}
