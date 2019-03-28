package ru.ifmo.wst;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;
import javax.xml.ws.Endpoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.wst.dao.AntibioticsDAO;
import ru.ifmo.wst.utils.Configuration;
import ru.ifmo.wst.ws.AntibioticService;
import ru.ifmo.wst.ws.TestService;

@Slf4j
public class App {
  public static void main(String[] args) {
    Configuration conf = new Configuration("config.properties");
    String scheme = conf.get("scheme", "http:");
    String host = conf.get("host", "localhost");
    String port = conf.get("port", "8080");
    String baseUrl = scheme + "//" + host + ":" + port;

    String antibioticsServiceName = conf.get("antibiotics.service.name");
    String antibioticsUrl = baseUrl + "/" + antibioticsServiceName;

    DataSource dataSource = initDataSource();
    log.info("Start application");
    Endpoint.publish(baseUrl + "/test", new TestService());
    Endpoint.publish(antibioticsUrl, new AntibioticService(new AntibioticsDAO(dataSource)));
    log.info("Application was successfully started");
  }

  @SneakyThrows
  private static DataSource initDataSource() {
    InputStream dsPropsStream = App.class.getClassLoader()
        .getResourceAsStream("datasource.properties");
    Properties dsProps = new Properties();
    dsProps.load(dsPropsStream);
    HikariConfig hikariConfig = new HikariConfig(dsProps);
    return new HikariDataSource(hikariConfig);
  }
}
