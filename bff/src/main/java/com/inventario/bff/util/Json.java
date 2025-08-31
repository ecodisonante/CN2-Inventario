// util/Json.java
package com.inventario.bff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Json {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static <T> T read(String body, Class<T> type) throws JsonProcessingException {
    return MAPPER.readValue(body, type);
  }

  public static String write(Object obj) throws JsonProcessingException {
    return MAPPER.writeValueAsString(obj);
  }
}
