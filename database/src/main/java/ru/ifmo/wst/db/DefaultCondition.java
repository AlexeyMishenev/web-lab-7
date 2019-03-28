package ru.ifmo.wst.db;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class DefaultCondition extends AbstractCondition {

  private DefaultCondition(String columnName, Object value, Class<?> type) {
    super(columnName, value, type);
  }

  public static <T> DefaultCondition defaultCondition(String columnName, T value, Class<T> type) {
    return new DefaultCondition(columnName, value, type);
  }

  @Override
  public String build() {
    if (getValue() != null) {
      return getColumnName() + " = ?";
    }
    return null;
  }
}