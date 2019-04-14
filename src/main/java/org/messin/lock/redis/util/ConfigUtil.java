package org.messin.lock.redis.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.config.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Load config from redis-lock.property to {@link Config}
 */
public final class ConfigUtil {

    private static final Logger logger = LogManager.getLogger(ConfigUtil.class);

    private static final String REDIS_LOCK_PROPERTIES_FILE = "redis-lock.property";

    public static void loadConfig() throws IOException, IllegalAccessException {
        logger.info("Loading config from {}.", REDIS_LOCK_PROPERTIES_FILE);


        /**  check if redis-lock.property exist  */
        try {
            File configPropertyFile = new File(ConfigUtil.class.getClassLoader().getResource(REDIS_LOCK_PROPERTIES_FILE).toURI());
            if (!configPropertyFile.exists()) {
                logger.info("{} does not exist, will not load config and will use default value.", configPropertyFile.getAbsoluteFile());
                return;
            }
        } catch (URISyntaxException e) {
            // ignore
        }


        /**  load config from file  */
        Properties properties = new Properties();
        InputStream configPropertyInputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(REDIS_LOCK_PROPERTIES_FILE);
        properties.load(configPropertyInputStream);

        Field[] fields = Config.class.getFields();
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            String fieldName = field.getName();
            String value = properties.getProperty(fieldName);
            field.set(null, value);
            logger.info("Set {}={}.", fieldName, value);
        }
    }
}
