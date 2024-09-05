package com.dummycompany.test.framework.core.splitjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dummycompany.test.framework.core.Constants;
import com.dummycompany.test.framework.core.rerun.RemoveFailedScenariosFromJson;
import com.dummycompany.test.framework.core.rerun.model.Feature;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SplitJson {

  private static final Logger LOG = LogManager.getLogger(SplitJson.class);

  private SplitJson() {
  }

  public static void main(String[] args) throws IOException {
    splitJsonFiles();
  }

  public static void splitJsonFiles() {
    try {
      LOG.info("Splitting json file {}", RemoveFailedScenariosFromJson.FIRST_RUN_CUCUMBER_JSON_FILE);
      Feature[] features = new ObjectMapper().readValue(new File(RemoveFailedScenariosFromJson.FIRST_RUN_CUCUMBER_JSON_FILE),
          Feature[].class);
      FileUtils.deleteQuietly(new File(RemoveFailedScenariosFromJson.FIRST_RUN_CUCUMBER_JSON_FILE));
      Feature feature = Objects.requireNonNull(features)[0];
      List<List<Feature>> writableFeatures = new LinkedList<>();
      feature.getElements().forEach(element -> {
        Feature writableFeature = new Feature();
        writableFeature.setUri(feature.getUri());
        writableFeature.setId(feature.getId());
        writableFeature.setName(feature.getName());
        writableFeature.setKeyword(feature.getKeyword());
        writableFeature.setTags(feature.getTags());
        writableFeature.setElements(Collections.singletonList(element));
        writableFeature.setDescription(feature.getDescription());
        writableFeature.setLine(feature.getLine());
        writableFeatures.add(Collections.singletonList(writableFeature));
      });
      ObjectMapper mapper = new ObjectMapper();
      LOG.info("Will create {} json files", writableFeatures.size());
      for (int i = 0; i < writableFeatures.size(); i++) {
        FileUtils.writeStringToFile(
            Paths.get(Constants.CURRENT_DIR, "target", "cucumber-reports", "cucumber-" + i + ".json").toFile(),
            mapper.writeValueAsString(writableFeatures.get(i)),
            Charset.defaultCharset());
      }
      LOG.info("Successfully split json file {}", RemoveFailedScenariosFromJson.FIRST_RUN_CUCUMBER_JSON_FILE);
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
  }
}

