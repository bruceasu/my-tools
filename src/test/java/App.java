public class App {
  public static void main(String[] args) {
    // *Note* just for test, use shell script for run in product environment
    //        and add the properties to JVM

//        System.setProperty("vertx.pool.worker.size", "40"); //default 20, used in all verticles
//        // System.setProperty("vertx.pool.eventloop.size", "40"); // Runtime.getRuntime().availableProcessors() * 2
//
//        String module_base_path = "D:\\workspace\\sociality\\sociality-service";
//        System.setProperty("vertx.logger-delegate-factory-class-name",
//                "io.vertx.core.logging.Log4jLogDelegateFactory");
//        String log4jConfigFile = "file:"+ module_base_path +
//                "\\src\\main\\resources\\log4j.properties";
//        // log4j.configurationFile 默认找classpath, 文件系统要加上file:
//        System.setProperty("log4j.configurationFile", log4jConfigFile);
//        System.setProperty("log4j.configuration", log4jConfigFile);
//        List<String> params = new LinkedList<>();
//        params.add("run");
//
//        params.add(ServiceVerticle.class.getName());
//        // 集群
//        params.add("-cluster");
//
//        params.add("-instances");
//        int cpus = Runtime.getRuntime().availableProcessors();
//        params.add(String.valueOf((cpus+1)/2));
//
//        String configFile = module_base_path +
//                "\\src\\main\\resources\\socialityservice.json";
//        params.add("-conf");
//        params.add(configFile);
//
//        Launcher.main(params.toArray(new String[0]));
  }
}
