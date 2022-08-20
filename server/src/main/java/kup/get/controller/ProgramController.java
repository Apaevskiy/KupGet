package kup.get.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@RestController
public class ProgramController {

    @GetMapping("/program")
    public Mono<Void> downloadFile(ServerHttpResponse response) {
        String fileName = "program.zip";
        ZeroCopyHttpOutputMessage zeroCopyResponse =
                (ZeroCopyHttpOutputMessage) response;
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename="+fileName+"");
        response.getHeaders().setContentType(MediaType.
                APPLICATION_OCTET_STREAM);
//        ClassPathResource resource = new ClassPathResource(Paths.get(fileName).toString());
        File file = new File(fileName);
        return zeroCopyResponse.writeWith(file, 0,
                file.length());
    }
}
