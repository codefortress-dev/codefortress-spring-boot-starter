package dev.codefortress.starter.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class CodeFortressLifecycleLogger implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(CodeFortressLifecycleLogger.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String banner = """
                
           ______vZ______
          /   CODEFORTRESS  \\    - SECURITY SHIELD: ACTIVE
         |    Community     |   -------------------------------------
          \\  dev.codefortress/   domain: dev.codefortress
           ----------------      
                                
           - IMPORTANT / DATABASE SETUP:
              If you are not using hibernate.ddl-auto, you MUST execute the SQL script
              included in this JAR to create the necessary tables (cf_users, cf_roles...).
              
              - Location: classpath:scripts/codefortress-schema.sql
              
           - CURRENT STATUS:
              - Auth Mode:     JWT + Refresh Token
              - Rate Limit:    Enabled (In-Memory)
              - Pass Policy:   Active
              - Multi-Session: Configurable (check max-sessions)
              
            ️  Running in Community Mode. For Enterprise features (Dashboard, SSO, Redis),
              please upgrade to CodeFortress Pro.
        """;

        // Usamos System.out para asegurar que salga limpio en la consola,
        // o log.info si prefieres el formato de Spring.
        System.out.println(banner);
    }
}