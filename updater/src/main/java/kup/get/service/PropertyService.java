package kup.get.service;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Component
public class PropertyService {
    private final Map<String, String> properties = new HashMap<>();

    public PropertyService() {
        try {
            File file = new File("setting.properties");
            if (!file.exists()) {
                Files.createFile(file.toPath());
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] convertedString = line.split("=");
                    if(convertedString.length == 2)
                        properties.put(convertedString[0], convertedString[1]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getVersionId() {
        try {
            return Long.parseLong(getProperty("version.id", "0"));
        } catch (Exception e){
            return 0L;
        }
    }

    public void saveVersion(Long latestVersionId) {
        properties.put("version.id", String.valueOf(latestVersionId));
        writeProperties();
    }

    private void writeProperties() {
        try {
            StringBuilder sb = new StringBuilder();
            for (String key : properties.keySet()) {
                sb.append(key).append('=').append(properties.get(key)).append("\r\n");
            }
            FileWriter fileWriter = new FileWriter("setting.properties");
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getServerAddress() {
        return "http://"+getProperty("server.ip", "192.168.0.2") + ":" + getProperty("server.port", "9090");
    }

    private String getProperty(String key, String value) {
        String val = properties.get(key);
        if(val == null){
            properties.put(key, value);
            writeProperties();
            return value;
        }
        return val;
    }
}
