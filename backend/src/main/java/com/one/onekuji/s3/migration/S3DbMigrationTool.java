package com.one.onekuji.s3.migration;

import com.one.onekuji.BackendApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

public class S3DbMigrationTool {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        try (ConfigurableApplicationContext ctx = app.run(args)) {
            S3MigrationService svc = ctx.getBean(S3MigrationService.class);
            svc.migrateAllTables();
        }
    }
}
