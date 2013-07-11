package mpi.aida.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mpi.aida.util.ClassPathUtils;

/**
 * Main configuration path for global settings.
 */
public class AidaConfig {
  private static final Logger logger = 
      LoggerFactory.getLogger(AidaConfig.class);

  public static final String SERVICENAME = "serviceName";

  public static final String SERVERPORT = "serverport";

  public static final String CLIENTPORT = "clientport";

  public static final String STORAGEPATH = "storagePath";

  public static final String DATAACCESS = "dataAccess";

  public static final String DATAACCESS_IP = "dataAccessIP";

  public static final String DATAACCESS_PORT = "dataAccessPort";

  public static final String DATAACCESS_SERVICENAME = "dataAccessServiceName";

  public static final String DATAACCESS_DIRECT_PATH = "dataAccessDirectPath";

  public static final String RMI_TOKENIZER_LANGUAGE = "tokenizerLanguage";

  public static final String LOG_TO_DB = "logToDB";

  public static final String LOG_TABLENAME = "logTableName";

  public static final String LOG_STATS_TABLENAME = "logStatsTableName";
  
  public static final String MAX_NUM_CANDIDATE_ENTITIES_FOR_GRAPH = "maxNumCandidateEntitiesForGraph";
  
  public static final String EE_NUM_THREADS = "eeNumThreads";
  
  public static final String LOAD_HYENA_MODELS = "loadHyenaModels";
  
  public static final String CACHE_WORD_EXPANSIONS = "cacheWordExpansions";
  
  public static final String ENTITIES_CONTEXT_CACHE_SIZE = "entitiesContextCacheSize";  

  private Properties properties;

  private String path = "aida.properties";

  private static AidaConfig config = null;

  private AidaConfig() {
    properties = new Properties();
    try {
    	properties = ClassPathUtils.getPropertiesFromClasspath(path);
    } catch (Exception e) {
      properties = new Properties();
      logger.error("Main settings file missing. " +
      		"Copy 'sample_settings/aida.properties' to the 'settings/' " +
      		"directory and adjust it.");
    }
  }

  private static AidaConfig getInstance() {
    if (config == null) {
      config = new AidaConfig();
    }
    return config;
  }

  private String getValue(String key) {
    return (String) properties.get(key);
  }
  
  private void setValue(String key, String value) {
    properties.setProperty(key, value);
  }

  private boolean hasKey(String key) {
    return properties.containsKey(key);
  }

  public static String get(String key) {
    String value = null;
    if (AidaConfig.getInstance().hasKey(key)) {
      value = AidaConfig.getInstance().getValue(key);
    } else {
      // Some default values.
      if (key.equals(EE_NUM_THREADS)) {
        value = "8";
      } else if (key.equals(MAX_NUM_CANDIDATE_ENTITIES_FOR_GRAPH)) {
        // 0 means no limit.
        value = "0";
      } else if (key.equals(CACHE_WORD_EXPANSIONS)) {
        value = "true";
      } else if (key.equals(ENTITIES_CONTEXT_CACHE_SIZE)) {
        value = "10";
      } else {
        logger.error("" +
        		"Missing key in properties file with no default value: " + key);
      }
    }
    return value;
  }
  
  public static int getAsInt(String key) {
    String value = get(key);
    return Integer.parseInt(value);
  }
  
  public static boolean getBoolean(String key) {
    return Boolean.parseBoolean(get(key));
  }
  
  public static void set(String key, String value) {
    AidaConfig.getInstance().setValue(key, value);
  }
}
