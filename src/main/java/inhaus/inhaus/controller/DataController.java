package inhaus.inhaus.controller;

import inhaus.inhaus.data.Data;
import inhaus.inhaus.service.DataService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {
    private List<Data> crawlData; // 크롤링 데이터 저장할 변수

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
        initCrawlData(); // 초기 크롤링 데이터 로드
    }

    private void initCrawlData() {
        try {
            crawlData = dataService.crawlData();
        } catch (InterruptedException e) {
            e.printStackTrace();
            crawlData = null;
        }
    }

    @GetMapping("/crawl")
    public List<Data> getCrawlData() {
        return crawlData;
    }

    @GetMapping("/crawlandfind")
    public List<Data> getDatasByDate(@RequestParam int year, @RequestParam int month, @RequestParam int day){
        return dataService.getDatasByDate(year, month, day);
    }
}