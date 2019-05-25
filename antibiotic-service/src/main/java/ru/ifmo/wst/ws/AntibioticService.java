package ru.ifmo.wst.ws;

import static java.text.MessageFormat.format;

import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.ifmo.wst.dao.AntibioticsDAO;
import ru.ifmo.wst.entity.Antibiotics;

@WebService
@NoArgsConstructor
@AllArgsConstructor
public class AntibioticService {

  @Inject
  private AntibioticsDAO antibioticsDAO;

  @WebMethod
  public List<Antibiotics> findAll() throws AntibioticServiceException {
    return wrapException(() -> antibioticsDAO.findAll());
  }

  @WebMethod
  public List<String> findAllAntibiotics() throws AntibioticServiceException {
    return wrapException(() -> antibioticsDAO.findAllAntibiotics());
  }

  @WebMethod
  public String findDosage(
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "skf") Integer skf) throws AntibioticServiceException {
    return wrapException(() -> antibioticsDAO.findDosage(name, method, skf));
  }

  @WebMethod
  public List<Antibiotics> filter(
      @WebParam(name = "id") Long id,
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "from") Integer from,
      @WebParam(name = "to") Integer to,
      @WebParam(name = "dosage") String dose,
      @WebParam(name = "additional") String additional) throws AntibioticServiceException {
    return wrapException(() -> antibioticsDAO.filter(id, name, method, to, from, dose, additional));
  }

  @WebMethod
  public long create(
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "from") Integer from,
      @WebParam(name = "to") Integer to,
      @WebParam(name = "dosage") String dosage,
      @WebParam(name = "additional") String additional) throws AntibioticServiceException {
    checkNotNullArg("name", name);
    checkNotNullArg("from", from);
    checkNotNullArg("to", to);
    checkNotNullArg("dosage", dosage);
    return wrapException(() -> antibioticsDAO.create(name, method, to, from, dosage, additional));
  }

  @WebMethod
  public long delete(@WebParam(name = "id") long id) throws AntibioticServiceException {
    return wrapException(() -> {
      long deletedCount = antibioticsDAO.delete(id);
      if (deletedCount <= 0) {
        throw new AntibioticServiceException(format("No records with id {0} found to delete", id));
      }
      return deletedCount;
    });
  }

  @WebMethod
  @WebResult(name = "updatedCount")
  public long update(
      @WebParam(name = "id") Long id,
      @WebParam(name = "name") String name,
      @WebParam(name = "method") String method,
      @WebParam(name = "from") Integer from,
      @WebParam(name = "to") Integer to,
      @WebParam(name = "dosage") String dosage,
      @WebParam(name = "additional") String additional) throws AntibioticServiceException {
    checkNotNullArg("name", name);
    checkNotNullArg("from", from);
    checkNotNullArg("to", to);
    checkNotNullArg("dosage", dosage);
    return wrapException(() -> {
      long updatedCount = antibioticsDAO.update(id, name, method, from, to, dosage, additional);
      if (updatedCount <= 0) {
        throw new AntibioticServiceException(format("No records with id {0} found to update", id));
      }
      return updatedCount;
    });
  }

  private void checkNotNullArg(String argName, Object argValue) throws AntibioticServiceException {
    if (argValue == null) {
      throw new AntibioticServiceException(format("{0} must be not null", argName));
    }
  }

  private <T> T wrapException(Supplier<T> supplier) throws AntibioticServiceException {
    try {
      return supplier.produce();
    } catch (AntibioticServiceException exc) {
      throw exc;
    } catch (SQLException exc) {
      String message = "Unexpected SQL exception with message " + exc.getMessage() +
          " and sql state " + exc.getSQLState();
      throw new AntibioticServiceException(message, exc);
    } catch (Exception exc) {
      String message = "Unexpected exception " + exc.getClass().getName() +
          " with message " + exc.getMessage();
      throw new AntibioticServiceException(message, exc);
    }
  }

  private interface Supplier<T> {
    T produce() throws Exception;
  }
}
