package kup.get.service.energy;

import kup.get.entity.postgres.energy.Waybills;
import kup.get.repository.postgres.energy.WaybillsRepository;
import kup.get.service.LogService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class WaybillsService {
    private final WaybillsRepository repository;
    private final LogService logService;

    public WaybillsService(WaybillsRepository repository, LogService logService) {
        this.repository = repository;
        this.logService = logService;
    }

    public boolean checkWaybills(Long waybillsId) {
        return repository.existsById(waybillsId);
    }

    @Transactional
    public void save(Waybills waybills) {
        repository.saveAndFlush(waybills);
        logService.addLog("Изменение накладной " + waybills.getId());
    }
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
        logService.addLog("Удаление накладной " + id);
    }
    public void addWaybills(Waybills waybills) {
        repository.save(waybills);
        logService.addLog("Добавление накладной " + waybills.getId());
    }

    public List<Waybills> getSortWaybills(Long id,
                                          String person,
                                          String department,
                                          LocalDate dateBegin,
                                          LocalDate dateEnd) {
        return repository.findAll(
                WaybillsRepository.search(id, person, department, dateBegin, dateEnd),
                WaybillsRepository.getPageable()).getContent();
    }

    public Waybills findById(Long id) {
        return repository.getById(id);
    }
}
