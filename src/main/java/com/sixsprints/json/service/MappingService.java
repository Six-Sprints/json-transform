package com.sixsprints.json.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.dto.TransformerChange;
import com.sixsprints.json.dto.TransformerData;
import com.sixsprints.json.dto.TransformerMetaInfo;
import com.sixsprints.json.dto.TransformerResponse;
import com.sixsprints.json.util.SpecFactory;
import com.sixsprints.json.util.TransformerUtil;

public class MappingService {

  private static final String ROOT_MAPPING = "[" +
    "  {" +
    "    \"operation\": \"shift\"," +
    "    \"spec\": {" +
    "      \"{{ROOT_ELEMENT}}\": {" +
    "        \"*\": \"&\"" +
    "      }" +
    "    }" +
    "  }" +
    "]";

  @SuppressWarnings("unchecked")
  public static TransformerResponse convert(Mapping mapping, String inputMessage) {

    Object spec = generateSpec(mapping);

    Map<String, Object> input = JsonUtils.jsonToMap(inputMessage);
    Chainr chainr = Chainr.fromSpec(spec);
    Map<String, Object> output = (Map<String, Object>) chainr.transform(input);
    List<TransformerMetaInfo> metaChanges = new ArrayList<TransformerMetaInfo>();
    if (mapping.getTransformerData() != null && !mapping.getTransformerData().isEmpty()) {
      for (TransformerData transformerData : mapping.getTransformerData()) {
        SpecTransformer transformer = SpecFactory.getInstance(transformerData.getSpecTransformerKey());
        List<TransformerChange> changes = transformer.transform(input, output, transformerData.getContext());
        String idKey = transformerData.getContext().get(SpecTransformer.CONTEXT_ID_KEY).toString();
        String idValue = TransformerUtil.readSingle(input, idKey).toString();
        metaChanges.add(TransformerMetaInfo.builder().changes(changes).idKey(idKey).idValue(idValue).build());
      }
    }
    return TransformerResponse.builder().output(output).transformerMetaInfo(metaChanges).build();
  }

  private static Object generateSpec(Mapping mapping) {

    if (!isBlank(mapping.getRootElement())) {
      return JsonUtils.jsonToObject(ROOT_MAPPING.replace("{{ROOT_ELEMENT}}", mapping.getRootElement()));
    }

    if (!isBlank(mapping.getSpecJsonString())) {
      return JsonUtils.jsonToObject(mapping.getSpecJsonString());
    }

    return JsonUtils.jsonToObject(mapping.getSpecJsonStream());
  }

  private static boolean isBlank(String string) {

    return string == null || string.isEmpty();

  }

}
