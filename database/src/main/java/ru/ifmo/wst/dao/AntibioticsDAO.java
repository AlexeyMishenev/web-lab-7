package ru.ifmo.wst.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.wst.db.DefaultCondition;
import ru.ifmo.wst.db.IgnoreCaseContainsCondition;
import ru.ifmo.wst.db.Query;
import ru.ifmo.wst.db.QueryBuilder;
import ru.ifmo.wst.entity.Antibiotics;

@RequiredArgsConstructor
@Slf4j
public class AntibioticsDAO {

  private final DataSource dataSource;

  private final String TABLE_NAME = "Antibiotics";
  private final String ID = "id";
  private final String NAME = "name";
  private final String METHOD = "method";
  private final String FROM = "skf_from";
  private final String TO = "skf_to";
  private final String DOSAGE = "dosage";
  private final String ADDITIONAL = "additional";

  private final List<String> columnNames = Arrays.asList(
      ID, NAME, METHOD, FROM, TO, DOSAGE, ADDITIONAL
  );

  public List<Antibiotics> findAll() throws SQLException {
    log.debug("Find all query");
    try (Connection connection = dataSource.getConnection()) {
      Statement statement = connection.createStatement();
      StringBuilder query = new StringBuilder();
      statement.execute(
          query.append("SELECT ")
              .append(String.join(", ", columnNames))
              .append(" FROM ")
              .append(TABLE_NAME)
              .toString()
      );
      return resultSetToList(statement.getResultSet());
    }
  }

  public String findDosage(String name, String method, int skf) throws SQLException {
    log.debug("Find Antibiotic dosage");
    QueryBuilder qb = new QueryBuilder()
        .tableName(TABLE_NAME)
        .selectColumns(ID, NAME, METHOD, FROM, TO, DOSAGE, ADDITIONAL)
        .condition(new IgnoreCaseContainsCondition(NAME, name));

    if (method != null) {
      qb.condition(new IgnoreCaseContainsCondition(METHOD, method));
    }

    Query query = qb.buildPreparedStatementQuery();

    log.debug("Built query {}", query.getQueryString());

    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement ps = connection.prepareStatement(query.getQueryString());
      query.initPreparedStatement(ps);
      List<Antibiotics> drugs = resultSetToList(ps.executeQuery());
      for (Antibiotics i : drugs) {
        if (i.getFrom() <= skf && skf < i.getTo()) {
          StringBuilder sb = new StringBuilder(i.getDosage());
          if (i.getAdditional() != null) {
            sb.append(" (").append(i.getAdditional()).append(")");
          }
          return sb.toString();
        }
      }
      return null;
    }

  }

  public List<String> findAllAntibiotics() throws SQLException {
    log.debug("Find all antibiotics");
    try (Connection connection = dataSource.getConnection()) {
      Statement statement = connection.createStatement();
      statement.execute(String.format("SELECT DISTINCT %s FROM %s", NAME, TABLE_NAME));
      return resultSetToStringList(statement.getResultSet());
    }
  }

  public List<Antibiotics> filter(Long id, String name, String method, Integer from,
      Integer to, String dosage, String additional) throws SQLException {
    log.debug(
        "Filter with args: {} {} {} {} {} {} {}", id, name, method, from, to, dosage, additional
    );

    if (Stream.of(id, name, method, from, to, dosage, additional).allMatch(Objects::isNull)) {
      log.debug("Args are empty");
      return findAll();
    }

    Query query = new QueryBuilder()
        .tableName(TABLE_NAME)
        .selectColumns(ID, NAME, METHOD, FROM, TO, DOSAGE, ADDITIONAL)
        .condition(DefaultCondition.defaultCondition(ID, id, Long.class))
        .condition(new IgnoreCaseContainsCondition(NAME, name))
        .condition(new IgnoreCaseContainsCondition(METHOD, method))
        .condition(DefaultCondition.defaultCondition(FROM, from, Integer.class))
        .condition(DefaultCondition.defaultCondition(TO, to, Integer.class))
        .condition(new IgnoreCaseContainsCondition(DOSAGE, dosage))
        .condition(new IgnoreCaseContainsCondition(ADDITIONAL, additional))
        .buildPreparedStatementQuery();

    log.debug("Built query {}", query.getQueryString());

    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement ps = connection.prepareStatement(query.getQueryString());
      query.initPreparedStatement(ps);
      return resultSetToList(ps.executeQuery());
    }
  }

  public long create(String name, String method, int from, int to, String dosage, String additional)
      throws SQLException {
    log.debug("Create with args {} {} {} {} {} {}", name, method, from, to, dosage, additional);
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      long newId;
      try (Statement idStatement = connection.createStatement()) {
        idStatement.execute("SELECT nextval('antibiotics_id_seq') nextval");
        try (ResultSet rs = idStatement.getResultSet()) {
          rs.next();
          newId = rs.getLong("nextval");
        }

      }
      try (PreparedStatement stmnt = connection.prepareStatement(
          "INSERT INTO " + TABLE_NAME + "(" + String.join(", ", columnNames) + ") " +
              "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
        stmnt.setLong(1, newId);
        stmnt.setString(2, name);
        stmnt.setString(3, method);
        stmnt.setInt(4, from);
        stmnt.setInt(5, to);
        stmnt.setString(6, dosage);
        stmnt.setString(7, additional);
        int count = stmnt.executeUpdate();
        if (count == 0) {
          throw new RuntimeException("Could not execute query");
        }
        connection.commit();
        connection.setAutoCommit(true);
        return newId;
      }
    }
  }

  public int delete(long id) throws SQLException {
    log.debug("Delete entity with id {}", id);
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(true);
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = ?")) {
        ps.setLong(1, id);
        return ps.executeUpdate();
      }
    }
  }

  public long update(long id, String name, String method, int from, int to,
      String dosage, String additional) throws SQLException {
    log.debug("Update with args {} {} {} {} {} {}", name, method, from, to, dosage, additional);
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(true);
      try (PreparedStatement statement = connection.prepareStatement(
          String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
              TABLE_NAME, NAME, METHOD, FROM, TO, DOSAGE, ADDITIONAL, ID)
      )) {
        statement.setString(1, name);
        statement.setString(2, method);
        statement.setInt(3, from);
        statement.setInt(4, to);
        statement.setString(5, dosage);
        statement.setString(6, additional);
        statement.setLong(7, id);
        int updated = statement.executeUpdate();
        log.debug("{} rows updated", updated);
        return updated;
      }
    }
  }

  private List<String> resultSetToStringList(ResultSet rs) throws SQLException {
    List<String> result = new ArrayList<>();
    while (rs.next()) {
      result.add(rs.getString(NAME));
    }
    log.debug("Result set was converted to string list {}", result);
    return result;
  }

  private List<Antibiotics> resultSetToList(ResultSet rs) throws SQLException {
    List<Antibiotics> result = new ArrayList<>();
    while (rs.next()) {
      result.add(resultSetToEntity(rs));
    }
    log.debug("Result set was converted to entity list {}", result);
    return result;
  }

  private Antibiotics resultSetToEntity(ResultSet rs) throws SQLException {
    long id = rs.getLong(ID);
    String name = rs.getString(NAME);
    String method = rs.getString(METHOD);
    int from = rs.getInt(FROM);
    int to = rs.getInt(TO);
    String dosage = rs.getString(DOSAGE);
    String additional = rs.getString(ADDITIONAL);
    return new Antibiotics(id, name, method, from, to, dosage, additional);
  }
}

