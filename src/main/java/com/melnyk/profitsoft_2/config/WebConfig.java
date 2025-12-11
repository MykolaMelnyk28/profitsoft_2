package com.melnyk.profitsoft_2.config;

import com.melnyk.profitsoft_2.config.props.CorsProps;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebConfig {

    @Bean
    public CorsFilter corsFilter(CorsProps corsProps) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(corsProps.getOrigins());
        config.setAllowedMethods(corsProps.getMethods());
        config.setAllowCredentials(corsProps.isCredentials());
        config.setAllowedHeaders(corsProps.getHeaders());
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean(CorsProps corsProps) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter(corsProps));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
