package ru.ifmo.wst.client;


import static java.text.MessageFormat.format;
import static ru.ifmo.wst.client.Utils.readInt;
import static ru.ifmo.wst.client.Utils.readLong;
import static ru.ifmo.wst.client.Utils.readNotNullLong;
import static ru.ifmo.wst.client.Utils.readNotNullString;
import static ru.ifmo.wst.client.Utils.readString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.ws.BindingProvider;
import lombok.SneakyThrows;
import org.apache.juddi.api_v3.AccessPointType;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;

public class Client {
  private static AntibioticService service;
  private static JUDDIClient juddiClient;

  public static void main(String[] args) throws IOException {
    URL url = new URL(args[0]);
    AntibioticServiceService antibioticService = new AntibioticServiceService(url);
    service = antibioticService.getAntibioticServicePort();

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    String username = readNotNullString("Enter JUDDI username", reader);
    String password = readNotNullString("Enter JUDDI user password", reader);

    juddiClient = new JUDDIClient("META-INF/uddi.xml");
    juddiClient.authenticate(username, password);

    int curState = 0;

    while (true) {
      switch (curState) {
        case 0:
          System.out.println("\nВыберите один из пунктов:");
          System.out.println("1. Вывести все антибиотики");
          System.out.println("2. Вывести все дозировки антибиотиков");
          System.out.println("3. Получить дозу антибиотика по уровню СКФ");
          System.out.println("4. Применить фильтры");
          System.out.println("5. Создать");
          System.out.println("6. Изменить");
          System.out.println("7. Удалить");
          System.out.println("8. jUDDI");
          System.out.println("9. Выйти");
          curState = readState(curState, reader);
          break;
        case 1:
          try {
            findAll();
          } catch (AntibioticServiceException e) {
            System.err.println(e.getFaultInfo().getMessage());
          } finally {
            curState = 0;
          }
          break;
        case 2:
          try {
            findAllDosages();
          } catch (AntibioticServiceException e) {
            System.err.println(e.getFaultInfo().getMessage());
          } finally {
            curState = 0;
          }
          break;
        case 3:
          try {
            getDosageBySKF(reader);
          } catch (AntibioticServiceException e) {
            System.err.println(e.getFaultInfo().getMessage());
          } finally {
            curState = 0;
          }
          break;
        case 4:
          try {
            filter(reader);
          } catch (AntibioticServiceException e) {
            System.err.println(e.getFaultInfo().getMessage());
          } finally {
            curState = 0;
          }
          break;
        case 5:
          try {
            create(reader);
          } catch (AntibioticServiceException e) {
            System.err.println(e.getFaultInfo().getMessage());
          } finally {
            curState = 0;
          }
          break;
        case 6:
          try {
            update(reader);
          } catch (AntibioticServiceException e) {
            System.err.println(e.getFaultInfo().getMessage());
          } finally {
            curState = 0;
          }
          break;
        case 7:
          try {
            delete(reader);
          } catch (AntibioticServiceException e) {
            System.err.println(e.getFaultInfo().getMessage());
          } finally {
            curState = 0;
          }
          break;
        case 8:
          int state = 0;
          boolean br = false;
          while (!br) {
            switch (state) {
              case 0:
                System.out.println("\nВыберите один из пунктов:");
                System.out.println("1. Вывести все бизнесы");
                System.out.println("2. Зарегистрировать бизнес");
                System.out.println("3. Зарегистрировать сервис");
                System.out.println("4. Найти и использовать сервис");
                System.out.println("5. Выйти");
                state = readState(curState, reader);
                break;
              case 1:
                listBusinesses();
                state = 0;
                break;
              case 2:
                System.out.println("Введите имя бизнеса");
                String bnn = readString(reader);
                if (bnn != null) {
                  createBusiness(bnn);
                }
                state = 0;
                break;
              case 3:
                listBusinesses();
                String bbk = readNotNullString("Введите ключ бизнеса", reader);
                String ssn = readNotNullString("Введите имя сервиса", reader);
                String ssurl = readNotNullString("Введите ссылку на wsdl", reader);
                createService(bbk, ssn, ssurl);
                state = 0;
                break;
              case 4:
                String ffsn = readString("Введите имя сервиса для поиска", reader);
                filterServices(ffsn);
                String kkey = readString("Введите ключ сервиса", reader);
                if (kkey != null) {
                  useService(kkey);
                }
                br = true;
                break;
              case 5:
                br = true;
                break;
              default:
                state = 0;
                break;
            }
          }
          curState = 0;
          break;
        case 9:
          return;
        default:
          curState = 0;
          break;
      }
    }
  }

  private static void findAll() throws AntibioticServiceException {
    List<String> drugs = service.findAllAntibiotics();

    if (drugs.size() == 0) {
      System.out.println("Ничего не найдено");
    } else {
      System.out.println("Найдено:");
      drugs.forEach(System.out::println);
    }
  }

  private static void findAllDosages() throws AntibioticServiceException {
    List<Antibiotics> drugs = service.findAll();

    if (drugs.size() == 0) {
      System.out.println("Ничего не найдено");
    } else {
      System.out.println("Найдено:");
      drugs.stream().map(Antibiotics::toString).forEach(System.out::println);
    }
  }

  private static void getDosageBySKF(BufferedReader reader) throws AntibioticServiceException {
    System.out.println("\nЗаполните все поля");

    String name = readNotNullString("Название:", reader);
    String method = readString("Метод введения:", reader);
    Integer skf = readInt("СКФ (мл/мин):", reader);

    System.out.println("Найдено:");
    System.out.println(service.findDosage(name, method, skf));
  }

  private static void filter(BufferedReader reader) throws AntibioticServiceException {
    System.out.println("\nЧтобы не применять фильтр, оставьте значение пустым");

    Long id = readLong("id:", reader);
    String name = readString("Название:", reader);
    String method = readString("Метод введения:", reader);
    Integer from = readInt("СКФ от:", reader);
    Integer to = readInt("СКФ до:", reader);
    String dosage = readString("Дозировка:", reader);
    String additional = readString("Дополнительно:", reader);

    List<Antibiotics> drugs = service.filter(id, name, method, from, to, dosage, additional);

    if (drugs.size() == 0) {
      System.out.println("Ничего не найдено");
    } else {
      System.out.println("Найдено:");
      drugs.stream().map(Antibiotics::toString).forEach(System.out::println);
    }
  }

  private static void create(BufferedReader reader) throws AntibioticServiceException {
    System.out.println("\nЗаполните поля (* - обязательные)");

    String name = readNotNullString("* Название:", reader);
    String method = readString("Метод введения:", reader);
    Integer from = readInt("СКФ От (0 если пустое):", reader, 0);
    Integer to = readInt("СКФ До (1000 если пустое):", reader, 1000);
    String dosage = readNotNullString("* Дозировка:", reader);
    String additional = readString("Дополнительно:", reader);

    if (additional != null && !dosage.endsWith("*")) {
      dosage += "*";
    }

    System.out.println(format("ID новой записи: {0}",
        service.create(name, method, from, to, dosage, additional)));
  }

  private static void update(BufferedReader reader) throws AntibioticServiceException {
    long id = readNotNullLong("id изменяемой записи (0 для отмены операции):", reader);

    if (id == 0L) {
      return;
    }

    String name = readString("Название:", reader);
    String method = readString("Метод введения:", reader);
    Integer from = readInt("СКФ От (0 если пустое):", reader, 0);
    Integer to = readInt("СКФ До (1000 если пустое):", reader, 100);
    String dosage = readString("Дозировка:", reader);
    String additional = readString("Дополнительно:", reader);

    if (additional != null && dosage != null && !dosage.endsWith("*")) {
      dosage += "*";
    }

    System.out.println(format("Изменено {0} строк(а)",
        service.update(id, name, method, from, to, dosage, additional)));
  }

  private static void delete(BufferedReader reader) throws AntibioticServiceException {
    long id = readNotNullLong("id удаляемой записи (0 для отмены операции):", reader);

    if (id == 0L) {
      return;
    }

    System.out.println(format("Удалено {0} строк(а)", service.delete(id)));
  }

  @SneakyThrows
  private static void useService(String serviceKey) {

    ServiceDetail serviceDetail = juddiClient.getService(serviceKey.trim());
    if (serviceDetail == null || serviceDetail.getBusinessService() == null || serviceDetail
        .getBusinessService().isEmpty()) {
      System.out.printf("Can not find service by key '%s'\b", serviceKey);
      return;
    }
    List<BusinessService> services = serviceDetail.getBusinessService();
    BusinessService businessService = services.get(0);
    BindingTemplates bindingTemplates = businessService.getBindingTemplates();
    if (bindingTemplates == null || bindingTemplates.getBindingTemplate().isEmpty()) {
      System.out.printf("No binding template found for service '%s' '%s'\n", serviceKey,
          businessService.getBusinessKey());
      return;
    }
    for (BindingTemplate bindingTemplate : bindingTemplates.getBindingTemplate()) {
      AccessPoint accessPoint = bindingTemplate.getAccessPoint();
      if (accessPoint.getUseType().equals(AccessPointType.END_POINT.toString())) {
        String value = accessPoint.getValue();
        System.out.printf("Use endpoint '%s'\n", value);
        changeEndpointUrl(value);
        return;
      }
    }
    System.out.printf("No endpoint found for service '%s'\n", serviceKey);
  }

  @SneakyThrows
  private static void createService(String businessKey, String serviceName, String wsdlUrl) {
    List<ServiceDetail> serviceDetails = juddiClient
        .publishUrl(businessKey.trim(), serviceName.trim(), wsdlUrl.trim());
    System.out.printf("Services published from wsdl %s\n", wsdlUrl);
    JUDDIUtil.printServicesInfo(serviceDetails.stream()
        .map(ServiceDetail::getBusinessService)
        .flatMap(List::stream)
        .collect(Collectors.toList())
    );
  }

  @SneakyThrows
  private static void createBusiness(String businessName) {
    businessName = businessName.trim();
    BusinessDetail business = juddiClient.createBusiness(businessName);
    System.out.println("New business was created");
    for (BusinessEntity businessEntity : business.getBusinessEntity()) {
      System.out.printf("Key: '%s'\n", businessEntity.getBusinessKey());
      System.out.printf("Name: '%s'\n",
          businessEntity.getName().stream().map(Name::getValue).collect(Collectors.joining(" ")));
    }
  }

  private static void changeEndpointUrl(String endpointUrl) {
    ((BindingProvider) service).getRequestContext()
        .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl.trim());
  }


  @SneakyThrows
  private static void filterServices(String filterArg) {
    List<BusinessService> services = juddiClient.getServices(filterArg);
    JUDDIUtil.printServicesInfo(services);
  }

  @SneakyThrows
  private static void listBusinesses() {
    JUDDIUtil.printBusinessInfo(juddiClient.getBusinessList().getBusinessInfos());
  }


  private static int readState(int cur, BufferedReader reader) {
    try {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      return cur;
    }
  }

}
