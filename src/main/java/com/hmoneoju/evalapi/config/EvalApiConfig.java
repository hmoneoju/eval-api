package com.hmoneoju.evalapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestConfig.class})
public class EvalApiConfig {
}
