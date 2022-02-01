package kup.get.service;

import kup.get.entity.energy.Log;
import kup.get.repository.energy.LogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LogService {
    private final LogRepository repository;

    public LogService(LogRepository repository) {
        this.repository = repository;
    }
    public void addLog(String action){
        Log log = new Log();
        log.setAction(action);
        log.setDate(LocalDate.now());
        log.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        repository.save(log);
    }
    public List<Log> findAll(LocalDate begin, LocalDate end){
        return repository.findAll(LogRepository.searchForLogs(begin,end),
                LogRepository.getPageable()).getContent();
    }
}
