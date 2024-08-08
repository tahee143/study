package hello.quickstart.controller;

import hello.quickstart.dto.ItemDto;
import hello.quickstart.dto.ResponseDto;
import hello.quickstart.service.QuickService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class QuickController {

    @Autowired
    private QuickService quickService;

    @GetMapping("/dummy")
    public String dummy() {
        log.info("dummy");
        return "{}";
    }

    @GetMapping("/dummy2")
    public String dummy2() {
        log.info("dummy2");
        return "dummy2";
    }

    @GetMapping("/member")
    public String getMember(@RequestParam("empNo") String empNo
        , @RequestParam("year") int year){
        log.info("empNo : {}", empNo);
        log.info("year: {}", year);
        return "ok";
    }

    @GetMapping("/company/{id}")
    public String getCompany(@PathVariable("id") String id){
        log.info("id : {}", id);
        return "ok";
    }

    @PostMapping("/item")
    public ResponseDto registerItem(@RequestBody ItemDto item){
        log.info("item : {}", item);

        Boolean b = quickService.resisterItem(item);

        ResponseDto responseDto = new ResponseDto();

        if(b == true){
            responseDto.setMessage("ok");
            return responseDto;
        }

        responseDto.setMessage("fail");
        return responseDto;
    }
}
