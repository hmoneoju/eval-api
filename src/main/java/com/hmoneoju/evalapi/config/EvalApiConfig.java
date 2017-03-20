package com.hmoneoju.evalapi.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableCaching
@Import({RestConfig.class})
public class EvalApiConfig {
}
