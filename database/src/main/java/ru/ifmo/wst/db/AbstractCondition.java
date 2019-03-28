package ru.ifmo.wst.db;

import lombok.Value;
import lombok.experimental.NonFinal;


@Value
@NonFinal
abstract class AbstractCondition implements Condition {
  private final String columnName;
  private final Object value;
  private final Class<?> type;
}