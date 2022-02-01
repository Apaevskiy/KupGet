package kup.get.controller.socket;

import kup.get.model.FileOfProgram;
import kup.get.model.Version;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SocketController {
    private final RSocketRequester requester;

    public SocketController(RSocketRequester requester) {
        this.requester = requester;
    }
    public Version getActualVersion(){
        return requester
                .route("getActualVersion")
                .retrieveMono(Version.class)
                .block();
    }
    public List<FileOfProgram> getUpdateFiles(Version version){
        return  requester
                .route("getUpdates")
                .data(version)
                .retrieveFlux(FileOfProgram.class)
                .collect(Collectors.toList())
                .block();
    }
    public List<FileOfProgram> getSavedFiles(){
        return requester.route("filesSaved")
                .retrieveFlux(FileOfProgram.class)
                .collect(Collectors.toList())
                .block();
    }
    public List<Version> getUpdateInformation(Version version){
        return requester.route("informationAboutUpdate")
                .data(version)
                .retrieveFlux(Version.class)
                .collect(Collectors.toList())
                .block();
    }
}
