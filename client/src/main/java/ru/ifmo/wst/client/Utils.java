package ru.ifmo.wst.client;

import java.io.BufferedReader;

class Utils {

  static String readString(BufferedReader reader) {
    try {
      String trim = reader.readLine().trim();
      if (trim.isEmpty()) {
        return null;
      }
      return trim;
    } catch (Exception e) {
      return null;
    }
  }

  static Long readLong(BufferedReader reader) {
    try {
      return Long.parseLong(reader.readLine());
    } catch (Exception e) {
      return null;
    }
  }

  static Integer readInt(BufferedReader reader) {
    try {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      return null;
    }
  }

  static Integer readInt(BufferedReader reader, int defaultValue) {
    try {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      return defaultValue;
    }
  }
}
