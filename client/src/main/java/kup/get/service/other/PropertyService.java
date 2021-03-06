package kup.get.service.other;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class PropertyService {
    private final Map<String, String> properties = new HashMap<>();
    private File file;

    public PropertyService() {
        /*try {
            file = ResourceUtils.getFile("classpath:setting.properties");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] convertedString = line.split("=");
                properties.put(convertedString[0], convertedString[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void writeProperties() {
        try {
            StringBuilder sb = new StringBuilder();
            for (String key: properties.keySet()){
                sb.append(key).append('=').append(properties.get(key)).append("\r\n");
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
