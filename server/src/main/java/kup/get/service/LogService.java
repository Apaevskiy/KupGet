package kup.get.service;

import kup.get.entity.postgres.energy.Log;
import kup.get.repository.postgres.energy.LogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

@Service
@Deprecated
public class LogService {
    private final LogRepository repository;

    public LogService(LogRepository repository) {
        this.repository = repository;
    }

    public void addLog(String action) {
        Log log = new Log();
        log.setAction(action);
        log.setDate(LocalDate.now());
        log.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
//        repository.save(log).subscribe();
    }

    public List<Log> findAllByDateIsBetween(LocalDate begin, LocalDate end) {
        return repository.findAllByDateIsBetween(begin, end);
    }
}
