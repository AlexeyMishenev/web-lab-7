package ru.ifmo.wst.client;


import static ru.ifmo.wst.client.Utils.readInt;
import static ru.ifmo.wst.client.Utils.readLong;
import static ru.ifmo.wst.client.Utils.readString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

public class Client {
  public static void main(String[] args) throws IOException {
    URL url = new URL(args[0]);
    AntibioticServiceService antibioticService = new AntibioticServiceService(url);
    AntibioticService antibioticPort = antibioticService.getAntibioticServicePort();

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
          System.out.println("5. Выйти");
          curState = readState(curState, reader);
          break;
        case 1:
          System.out.println("Найдено:");
          antibioticPort.findAllAntibiotics().forEach(System.out::println);
          curState = 0;
          break;
        case 2:
          System.out.println("Найдено:");
          antibioticPort.findAll().stream().map(Object::toString).forEach(System.out::println);
          curState = 0;
          break;
        case 3:
          System.out.println("\nЗаполните все поля");
          String findName;
          do {
            System.out.println("Название:");
            findName = readString(reader);
          } while (findName == null);

          System.out.println("Метод введения:");
          String findMethod = readString(reader);

          System.out.println("СКФ (мл/мин):");
          Integer skf = readInt(reader);

          System.out.println("Найдено:");
          System.out.println(antibioticPort.findDosage(findName, findMethod, skf));
          curState = 0;
          break;
        case 4:
          System.out.println("\nЧтобы не применять фильтр, оставьте значение пустым");
          System.out.println("id:");
          Long id = readLong(reader);
          System.out.println("Название:");
          String name = readString(reader);
          System.out.println("Метод введения:");
          String method = readString(reader);
          System.out.println("СКФ от:");
          Integer from = readInt(reader);
          System.out.println("СКФ до:");
          Integer to = readInt(reader);
          System.out.println("Найдено:");
          antibioticPort.filter(id, name, method, from, to, null, null).stream()
              .map(Objects::toString).forEach(System.out::println);
          curState = 0;
          break;
        case 5:
          return;
        default:
          curState = 0;
          break;
      }
    }
  }

  private static int readState(int cur, BufferedReader reader) {
    try {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      return cur;
    }
  }

}
