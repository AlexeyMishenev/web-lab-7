package ru.ifmo.wst.client;

import lombok.Value;

@Value
class Pair<L, R> {
  private final L left;
  private final R right;

}