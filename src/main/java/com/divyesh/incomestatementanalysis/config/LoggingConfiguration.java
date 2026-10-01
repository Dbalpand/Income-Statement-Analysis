package com.divyesh.incomestatementanalysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LoggingConfiguration {
    // Enables AspectJ auto-proxying across the Spring Application Context
}