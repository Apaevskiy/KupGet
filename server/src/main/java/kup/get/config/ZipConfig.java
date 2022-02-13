package kup.get.config;

import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.service.update.ZipService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ZipConfig {
    private final ZipService zipService;

    public ZipConfig(ZipService zipService) {
        this.zipService = zipService;
    }

    @Bean
    public List<FileOfProgram> getZipEntry() {
        File file = new File("program.jar");
        if (file.exists()){
            return zipService.readFile(file);
        } else {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }
}
