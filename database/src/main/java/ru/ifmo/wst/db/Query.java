package ru.ifmo.wst.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Query {
  @Getter
  private final String queryString;
  private final List<Condition> conditions;

  public void initPreparedStatement(PreparedStatement ps) throws SQLException {
    int i = 1;
    for (Condition condition : conditions) {
      int sqlType = classToSQLType(condition.getType());
      if (condition.getValue() == null) {
        ps.setNull(i, sqlType);
      } else {
        switch (sqlType) {
          case Types.BIGINT:
            ps.setLong(i, (Long) condition.getValue());
            break;
          case Types.VARCHAR:
            ps.setString(i, (String) condition.getValue());
            break;
          case Types.INTEGER:
            ps.setInt(i, (Integer) condition.getValue());
            break;
          default:
            throw new RuntimeException(condition.toString());
        }
      }
      i++;
    }
  }

  private int classToSQLType(Class<?> cls) {
    if (cls == Long.class) {
      return Types.BIGINT;
    } else if (cls == String.class) {
      return Types.VARCHAR;
    } else if (cls == Integer.class) {
      return Types.INTEGER;
    }
    throw new IllegalArgumentException(cls.getName());
  }
}

