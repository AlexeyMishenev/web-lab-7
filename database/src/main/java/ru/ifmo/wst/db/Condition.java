package ru.ifmo.wst.db;

public interface Condition {
  String build();
  Object getValue();
  Class<?> getType();
  String getColumnName();
}
