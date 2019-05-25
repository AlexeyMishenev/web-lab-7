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

public class Client {
  private static AntibioticService service;

  public static void main(String[] args) throws IOException {
    URL url = new URL(args[0]);
    AntibioticServiceService antibioticService = new AntibioticServiceService(url);

    service = antibioticService.getAntibioticServicePort();

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
          System.out.println("8. Выйти");
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


  private static int readState(int cur, BufferedReader reader) {
    try {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      return cur;
    }
  }

}
