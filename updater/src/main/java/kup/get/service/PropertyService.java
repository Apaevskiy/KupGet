package kup.get.service;

import kup.get.entity.Version;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class PropertyService {
    private final Map<String, String> properties = new HashMap<>();

    public PropertyService() {
        try {
            File file = ResourceUtils.getFile("classpath:setting.properties");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] convertedString = line.split("=");
                properties.put(convertedString[0], convertedString[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Version getVersion() {
        return Version
                .builder()
                .id(Long.parseLong(properties.get("version.id")))
                .release(properties.get("version.release"))
                .build();
    }

    public void saveVersion(Version actualVersion) {
        properties.put("version.id", String.valueOf(actualVersion.getId()));
        properties.put("version.release", actualVersion.getRelease());
        writeProperties();
    }

    private void writeProperties() {
        try {
            StringBuilder sb = new StringBuilder();
            for (String key : properties.keySet()) {
                sb.append(key).append('=').append(properties.get(key)).append("\r\n");
            }
            FileWriter fileWriter = new FileWriter(ResourceUtils.getFile("classpath:setting.properties"));
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
