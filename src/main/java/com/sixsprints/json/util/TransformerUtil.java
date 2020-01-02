package com.sixsprints.json.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.utils.JoltUtils;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.sixsprints.json.dto.TransformerChange;

public class TransformerUtil {

  public static String convertToJson(Object source) {
    return JsonUtils.toJsonString(source);
  }

  public static List<TransformerChange> writeSingleValue(Object source, String key, Object value) {
    List<TransformerChange> dto = Lists.newArrayList();
    List<Object[]> paths = getKey(source, key);
    for (Object[] path : paths) {
      dto.add(TransformerChange.builder().path(toString(path)).originalValue(JoltUtils.navigate(source, path))
        .transformedValue(value.toString()).loosePath(toLoosePath(toString(path))).build());
      JoltUtils.store(source, value, path);
    }
    return dto;
  }

  public static List<TransformerChange> write(Object source, String key, LinkedList<Object> values) {
    List<TransformerChange> dto = Lists.newArrayList();
    List<Object[]> paths = getKey(source, key);
    int index = 0;
    for (Object[] path : paths) {
      Object val = values.get(0);
      if (index < values.size()) {
        val = values.get(index);
      }
      try {
        dto.add(TransformerChange.builder().path(toString(path)).originalValue(JoltUtils.navigate(source, path))
          .transformedValue(val.toString()).loosePath(toLoosePath(toString(path))).build());
      } catch (Exception ex) {
      }
      JoltUtils.store(source, val, path);
      index++;
    }
    return dto;
  }

  public static LinkedList<Object> read(Object source, String key) {
    return readWithDefaultValue(source, key, "");
  }

  public static Object readSingle(Object source, String key) {
    LinkedList<Object> values = read(source, key);
    if (CollectionUtils.isEmpty(values)) {
      return null;
    }
    return values.get(0);
  }

  public static LinkedList<Object> readWithDefaultValue(Object source, String key, Object defaultValue) {
    List<Object[]> paths = getKey(source, key);
    LinkedList<Object> values = Lists.newLinkedList();
    for (Object[] path : paths) {
      values.add(JoltUtils.navigateOrDefault(defaultValue, source, path));
    }
    return values;
  }

  private static List<Object[]> getKey(Object inputMap, String key) {
    List<Object> parsedKey = parseKey(key);
    List<Object[]> chains = JoltUtils.listKeyChains(inputMap);
    List<Object[]> result = Lists.newArrayList();
    for (Object[] chain : chains) {
      List<Object> res = Lists.newArrayList();
      int index = 0;
      for (Object c : chain) {
        if (index >= parsedKey.size()) {
          continue;
        }
        if (isEqual(c, parsedKey.get(index))) {
          res.add(c);
        }
        index++;
      }
      if (res.size() == parsedKey.size() && !contains(result, res.toArray())) {
        result.add(res.toArray());
      }
    }
    return result;
  }

  private static boolean contains(List<Object[]> result, Object[] array) {
    for (Object[] res : result) {
      if (res.length == array.length) {
        int index = 0;
        boolean equal = true;
        for (Object x : array) {
          if (!isEqual(x, res[index])) {
            equal = false;
          }
        }
        if (equal) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isEqual(Object c, Object object) {
    return Objects.equals(c, object) || object.equals("*");
  }

  private static List<Object> parseKey(String key) {
    List<Object> result = Lists.newArrayList();
    String[] keys = key.split("\\.");
    for (int i = 0; i < keys.length; i++) {
      Integer intKey = Ints.tryParse(keys[i]);
      result.add(intKey == null ? keys[i] : intKey);
    }
    return result;
  }

  private static String toString(Object[] paths) {
    StringBuilder result = new StringBuilder();
    for (Object path : paths) {
      result.append(path.toString()).append(".");
    }
    return result.substring(0, result.length() - 1).toString();
  }

  private static String toLoosePath(Object path) {
    return path.toString().replaceAll("\\.[0-9]+", ".*");
  }

}
