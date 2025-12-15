package dev.codefortress.starter.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * An {@link ApplicationListener} that logs a banner to the console when the application is ready.
 * This banner provides information about the CodeFortress library and instructions for database setup.
 */
public class CodeFortressLifecycleLogger implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(CodeFortressLifecycleLogger.class);

    /**
     * Handles the {@link ApplicationReadyEvent} and logs the CodeFortress banner.
     *
     * @param event the application ready event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String banner = """
                
           ______vZ________
          /   CODEFORTRESS  \\    - SECURITY SHIELD: ACTIVE
         |    Community     |   -------------------------------------
          \\  dev.codefortress/   domain: dev.codefortress
           ----------------      
                                
           - IMPORTANT / DATABASE SETUP:
              If you are not using hibernate.ddl-auto, you MUST execute the SQL script
              included in this JAR to create the necessary tables (cf_users, cf_roles...).
              
              - Location: classpath:scripts/codefortress-schema.sql
              
        """;

        System.out.println(banner);
    }
}
