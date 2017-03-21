package com.hmoneoju.evalapi.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableCaching
@Import({RestConfig.class})
public class EvalApiConfig {
}
