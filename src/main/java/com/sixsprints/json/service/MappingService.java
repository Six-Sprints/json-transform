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

  private static final String EXTRACT_VALUE = "[" +
    "  {" +
    "    \"operation\": \"shift\"," +
    "    \"spec\": {" +
    "      \"{{EXTRACT_VALUE}}\": \"\"" +
    "    }" +
    "  }" +
    "]";

  @SuppressWarnings("unchecked")
  public static TransformerResponse convert(Mapping mapping, String inputMessage) {

    Object spec = generateSpec(mapping);
    List<TransformerMetaInfo> metaChanges = new ArrayList<TransformerMetaInfo>();

    if (spec == null) {
      return TransformerResponse.builder().output(inputMessage).transformerMetaInfo(metaChanges).build();
    }
    Map<String, Object> input = JsonUtils.jsonToMap(inputMessage);
    Chainr chainr = Chainr.fromSpec(spec);

    if (!isBlank(mapping.getExtractValue())) {
      return TransformerResponse.builder().output(chainr.transform(input)).transformerMetaInfo(metaChanges).build();
    }

    Map<String, Object> output = (Map<String, Object>) chainr.transform(input);
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

    if (mapping == null) {
      return null;
    }

    if (!isBlank(mapping.getRootElement())) {
      return JsonUtils.jsonToObject(ROOT_MAPPING.replace("{{ROOT_ELEMENT}}", mapping.getRootElement()));
    }

    if (!isBlank(mapping.getExtractValue())) {
      return JsonUtils.jsonToObject(EXTRACT_VALUE.replace("{{EXTRACT_VALUE}}", mapping.getExtractValue()));
    }

    if (!isBlank(mapping.getSpecJsonString())) {
      return JsonUtils.jsonToObject(mapping.getSpecJsonString());
    }

    if (mapping.getSpecJsonStream() != null) {
      return JsonUtils.jsonToObject(mapping.getSpecJsonStream());
    }

    return null;
  }

  private static boolean isBlank(String string) {

    return string == null || string.isEmpty();

  }

}
