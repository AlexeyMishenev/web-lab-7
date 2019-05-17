package ru.ifmo.wst.ws;

import java.util.List;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import ru.ifmo.wst.dao.AntibioticsDAO;
import ru.ifmo.wst.entity.Antibiotics;

@WebService
@NoArgsConstructor
@AllArgsConstructor
public class AntibioticService {

  @Inject
  private AntibioticsDAO antibioticsDAO;

  @WebMethod
  @SneakyThrows
  public List<Antibiotics> findAll() {
    return antibioticsDAO.findAll();
  }

  @WebMethod
  @SneakyThrows
  public List<String> findAllAntibiotics() {
    return antibioticsDAO.findAllAntibiotics();
  }

  @WebMethod
  @SneakyThrows
  public String findDosage(
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "skf") Integer skf) {
    return antibioticsDAO.findDosage(name, method, skf);
  }

  @WebMethod
  @SneakyThrows
  public List<Antibiotics> filter(
      @WebParam(name = "id") Long id,
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "from") Integer from,
      @WebParam(name = "to") Integer to,
      @WebParam(name = "dosage") String dosage,
      @WebParam(name = "additional") String additional) {
    return antibioticsDAO.filter(id, name, method, to, from, dosage, additional);
  }

  @WebMethod
  @SneakyThrows
  public long create(
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "from") Integer from,
      @WebParam(name = "to") Integer to,
      @WebParam(name = "dosage") String dosage,
      @WebParam(name = "additional") String additional) {
    return antibioticsDAO.create(name, method, to, from, dosage, additional);
  }

  @WebMethod
  @SneakyThrows
  public int delete(@WebParam(name = "id") long id) {
    return antibioticsDAO.delete(id);
  }

  @WebMethod
  @SneakyThrows
  @WebResult(name = "updatedCount")
  public long update(
      @WebParam(name = "id") Long id,
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "from") Integer from,
      @WebParam(name = "to") Integer to,
      @WebParam(name = "dosage") String dosage,
      @WebParam(name = "additional") String additional) {
    return antibioticsDAO.update(id, name, method, from, to, dosage, additional);
  }
}
