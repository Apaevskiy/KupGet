package kup.get.service.traffic;

import kup.get.entity.postgres.traffic.TrafficItemType;
import kup.get.entity.postgres.traffic.TrafficPerson;
import kup.get.repository.postgres.traffic.TrafficItemRepository;
import kup.get.repository.postgres.traffic.TrafficItemTypeRepository;
import kup.get.repository.postgres.traffic.TrafficPersonRepository;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TrafficItemService {
    private final TrafficItemRepository itemRepository;
    private final TrafficPersonRepository personRepository;
    private final TrafficItemTypeRepository typeRepository;

    public List<TrafficPerson> getBriefingOfPeople(LocalDate date) {
//        Long id = typeRepository.findFirstByName("Инструктажи по охране труда").getId();
        return personRepository.findAllByItemsTypeIdAndItemsDateFinishAfter(1L, date);
    }

    @Synchronized
    public void saveType(List<TrafficItemType> type) {
        System.out.println(type);
        typeRepository.saveAll(type);
    }

    public List<TrafficItemType> getAllItemTypes() {
        return typeRepository.findAll();
    }
}
