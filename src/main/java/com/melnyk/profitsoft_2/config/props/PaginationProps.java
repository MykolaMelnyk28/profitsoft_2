package com.melnyk.profitsoft_2.config.props;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties("pagination.default")
public class PaginationProps {

    private int page;
    private int size;
    private String sort;

}
