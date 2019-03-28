package ru.ifmo.wst.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Antibiotics {
  private Long id;
  private String name;
  private String method;
  private int from;
  private int to;
  private String dosage;
  private String additional;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(").append(id).append(") ")
        .append(name).append(":").append("\n");
    if (method != null) {
      builder.append("- Метод введения:\n\t").append(method).append("\n");
    }
    builder.append("- От: ").append(from).append("\n");
    builder.append("- До: ").append(to).append("\n");
    builder.append("- Доза:\n\t").append(dosage).append("\n");
    if (additional != null) {
      builder.append("- Дополнительно:\n\t").append(additional).append("\n");
    }
    return builder.toString();
  }
}
