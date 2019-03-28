package ru.ifmo.wst.beans;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import lombok.Data;
import ru.ifmo.wst.dao.AntibioticsDAO;

@Data
@ApplicationScoped
public class DAOFactory {

  @Resource(lookup = "jdbc/antibiotics")
  private DataSource dataSource;

  @Produces
  public AntibioticsDAO antibioticDAO() {
    return new AntibioticsDAO(dataSource);
  }
}
