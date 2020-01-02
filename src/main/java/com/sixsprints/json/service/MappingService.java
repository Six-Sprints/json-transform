package com.sixsprints.json.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

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

  @SuppressWarnings("unchecked")
  public static TransformerResponse convert(Mapping mapping, String inputMessage) {
    Object spec = JsonUtils.jsonToObject(mapping.getSpecJsonStream());
    Map<String, Object> input = JsonUtils.jsonToMap(inputMessage);
    Chainr chainr = Chainr.fromSpec(spec);
    Map<String, Object> output = (Map<String, Object>) chainr.transform(input);
    List<TransformerMetaInfo> metaChanges = new ArrayList<TransformerMetaInfo>();
    if (!CollectionUtils.isEmpty(mapping.getTransformerData())) {
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

}
