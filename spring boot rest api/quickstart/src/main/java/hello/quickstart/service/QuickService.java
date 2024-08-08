package hello.quickstart.service;

import hello.quickstart.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QuickService {

    public Boolean resisterItem(ItemDto itemDto) {
        // TODO : DB insert 성공하면 true
        log.info("service...");
        return true;
    }

}
