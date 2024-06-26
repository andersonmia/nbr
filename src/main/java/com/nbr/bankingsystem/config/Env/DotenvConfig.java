package com.nbr.bankingsystem.config.Env;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for loading environment variables using Dotenv.
 * Dotenv allows loading environment variables from a `.env` file into the application.
 */
@Configuration
public class DotenvConfig {

    /**
     * Bean for Dotenv to load environment variables.
     * This method configures and loads the Dotenv instance, which will read the `.env` file
     * located at the root of the project or as specified in the configuration.
     *
     * @return a configured Dotenv instance with the environment variables loaded.
     */
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().load();
    }
}
