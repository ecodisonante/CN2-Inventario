// util/Json.java
package com.inventario.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Json {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static <T> T read(String body, Class<T> type) throws Exception {
    return MAPPER.readValue(body, type);
  }

  public static String write(Object obj) throws Exception {
    return MAPPER.writeValueAsString(obj);
  }
}
