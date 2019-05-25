package ru.ifmo.wst.client;

import java.io.BufferedReader;

class Utils {

  static String readString(BufferedReader reader, String defaultValue) {
    try {
      String trim = reader.readLine().trim();
      if (trim.isEmpty()) {
        return defaultValue;
      }
      return trim;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  static Long readLong(BufferedReader reader, Long defaultValue) {
    try {
      return Long.parseLong(reader.readLine());
    } catch (Exception e) {
      return defaultValue;
    }
  }

  static Integer readInt(BufferedReader reader, Integer defaultValue) {
    try {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      return defaultValue;
    }
  }

  static String readString(BufferedReader reader) {
    return readString(reader, null);
  }

  static Integer readInt(BufferedReader reader) {
    return readInt(reader, null);
  }

  static Long readLong(BufferedReader reader) {
    return readLong(reader, null);
  }

  static String readNotNullString(String header, BufferedReader reader) {
    String value;
    do {
      System.out.println(header);
      value = readString(reader);
    } while (value == null);

    return value;
  }

  static int readNotNullInt(String header, BufferedReader reader) {
    Integer value;

    do {
      System.out.println(header);
      value = readInt(reader);
    } while (value == null);

    return value;
  }

  static long readNotNullLong(String header, BufferedReader reader) {
    Long value;

    do {
      System.out.println(header);
      value = readLong(reader);
    } while (value == null);

    return value;
  }

  static String readString(String header, BufferedReader reader) {
    System.out.println(header);
    return readString(reader, null);
  }

  static Integer readInt(String header, BufferedReader reader) {
    System.out.println(header);
    return readInt(reader, null);
  }

  static Long readLong(String header, BufferedReader reader) {
    System.out.println(header);
    return readLong(reader, null);
  }

  static String readString(String header, BufferedReader reader, String defaultValue) {
    System.out.println(header);
    return readString(reader, defaultValue);
  }

  static Integer readInt(String header, BufferedReader reader, Integer defaultValue) {
    System.out.println(header);
    return readInt(reader, defaultValue);
  }

  static Long readLong(String header, BufferedReader reader, Long defaultValue) {
    System.out.println(header);
    return readLong(reader, defaultValue);
  }

}
