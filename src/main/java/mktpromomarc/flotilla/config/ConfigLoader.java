package mktpromomarc.flotilla.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


    public class ConfigLoader {

        private static ConfigLoader instance;
        private Properties properties;

        private ConfigLoader() {

            properties = new Properties();

           try (InputStream input = new FileInputStream("SWGFC\\src\\main\\resources\\config.properties")) {
               if (input == null) {
                    System.out.println("Lo siento, no se pudo encontrar config.properties");
                    return;
                }
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static ConfigLoader getInstance() {
            if (instance == null) {
                instance = new ConfigLoader();
            }
            return instance;
        }

        public String getProperty(String key) {
            return properties.getProperty(key);
        }
        public boolean isEmpty() {
            return properties.isEmpty();  // Retorna true si no hay propiedades cargadas
        }
    }
