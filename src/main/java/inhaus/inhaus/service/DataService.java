package inhaus.inhaus.service;

import java.util.*;
import java.time.LocalDate;

import inhaus.inhaus.data.Data;
import inhaus.inhaus.data.DataDto;
import inhaus.inhaus.repository.DataRepository;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Configuration
@Service
@Transactional
public class DataService {

    // DataRepository 주입
    private final DataRepository dataRepository;

    // 생성자를 통한 의존성 주입
    public DataService(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    @Autowired
    public DataService dataService() { return new DataService(dataRepository); }

    // 데이터베이스에 data 저장.
    public void saveData(Data data) {
        dataRepository.save(data);
    }

    public List<Data> getDatasByDate(int year, int month, int day){
        return dataRepository.findByYearAndMonthAndDay(year, month, day);
    }


    // 코드 축약을 위한 메서드
    public static void newTap(WebDriver driver){
        // 현재 탭의 핸들(식별자) 저장
        String currentHandle = driver.getWindowHandle();

        // 새로운 탭의 핸들 찾기
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                // 새로운 탭으로 전환
                driver.switchTo().window(handle);
                break;
            }
        }
    }


    public List<Data> crawlData() throws InterruptedException {

        // ChromeDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", "크롬 드라이버 경로를 입력하세요");

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // 전제촤면으로 실행
        options.addArguments("--disable-popup-blocking"); // 팝업 무시
        options.addArguments("--disable-default-app"); // 기본앱 사용안함

        // WebDriver 객체 생성
        WebDriver driver = new ChromeDriver();

        // ****여기서부터 크롤링을 위한 페이지 이동 코드****

        // 로그인 페이지로 이동
        driver.get("https://portal.inha.ac.kr/");
        Thread.sleep(2000); // 페이지 로딩 대기 시간 주기.

        // 아이디/비밀번호 입력
        driver.findElement(By.name("user_id")).sendKeys("아이디를 입력하세요");
        driver.findElement(By.name("user_password")).sendKeys("비밀번호를 입력하세요");

        // 로그인 버튼 클릭
        driver.findElement(By.cssSelector("input[type='button'][onclick='doLogin()']")).click();
        Thread.sleep(2000); // 페이지 로딩 대기 시간 주기.

        newTap(driver);

        // 게시판으로 이동하는 버튼 클릭
        driver.findElement(By.cssSelector("a[href='/ins/index.jsp'][target='_blank']")).click();
        Thread.sleep(2000);

        newTap(driver);

        LocalDate now = LocalDate.now();

        // 반환할 Data들의 리스트 생성.
        List<Data> datas = new ArrayList<>();

        for(int i = 1; i <= 12; i++) { //now.getMonthValue()를 이용해서 현재 월부터 추출도 가능.
            String Month = i + "월";
            driver.get("https://ins2.inha.ac.kr/ITIS/ADM/SS/SS_04002/UseSearch_Pop.aspx?EquipCode=");
            driver.findElement(By.name("ddlYear")).sendKeys("2023");
            driver.findElement(By.name("ddlMonth")).sendKeys(Month);
            driver.findElement(By.name("ddlEquipCode")).sendKeys("운동장(축구장)");
            String originalHandle = driver.getWindowHandle();
            driver.findElement(By.name("ibtnReservationPrint")).click();
            driver.switchTo().window(originalHandle);


            // 요소를 식별하기 위한 XPath 또는 CSS 선택자를 사용하여 해당 요소를 찾습니다.
            WebElement element = driver.findElement(By.xpath("//script[contains(text(),'ReservationView_xml.aspx?Value=')]"));

            // 요소의 내용을 가져옵니다.
            String scriptContent = element.getAttribute("innerHTML");

            // 값을 추출하기 위한 정규식 패턴을 설정합니다.
            String valuePattern = "ReservationView_xml.aspx\\?Value=([^']+)";
            Pattern pattern = Pattern.compile(valuePattern);
            Matcher matcher = pattern.matcher(scriptContent);

            String value = "";
            if (matcher.find()) {
                value = matcher.group(1);
            }
            if(i == 4){ // 4, 7, 10, 11, 12월은 어째서인지 값이 추출이 안됨. 그래서 직접 주소를 따옴.
                driver.get("https://ins2.inha.ac.kr/ITIS/ADM/SS/SS_04002/ReservationView_xml.aspx?Value=%252fTY0ZAUJtFZp28repOxRNeqbbywQT1uW%252bvC5tXnLwx8eojQ7CnoNgg%253d%253d");
                Thread.sleep(2000);
            }
            else if(i == 7){
                driver.get("https://ins2.inha.ac.kr/ITIS/ADM/SS/SS_04002/ReservationView_xml.aspx?Value=2t%252bn8zLkRE2a2V43q%252fJk2KFCiVk75nQRa8GbnfQrogpYH%252blnDSf%252bMA%253d%253d");
                Thread.sleep(2000);
            }
            else if(i == 10){
                driver.get("https://ins2.inha.ac.kr/ITIS/ADM/SS/SS_04002/ReservationView_xml.aspx?Value=Lf2qHn1IRzSK%252bXUQDPeLGds1oOTnzyz3ay3HkhNJBrqU5ImSnU%252fTcQ%253d%253d");
                Thread.sleep(2000);
            }
            else if(i == 11){
                driver.get("https://ins2.inha.ac.kr/ITIS/ADM/SS/SS_04002/ReservationView_xml.aspx?Value=mytxBVZriM%252bph1flZ5YB5TnLTryMJa%252br75EDSbBNwQp%252btzIRlcO73A%253d%253d");
                Thread.sleep(2000);
            }
            else if(i == 12){
                driver.get("https://ins2.inha.ac.kr/ITIS/ADM/SS/SS_04002/ReservationView_xml.aspx?Value=ZrZsi2b99mTc4nxslxYNrihbQ5SnvOa%252b9HgONRn5zesOPSwpc9M0Uw%253d%253d");
                Thread.sleep(2000);
            }
            else {
                driver.get("https://ins2.inha.ac.kr/ITIS/ADM/SS/SS_04002/ReservationView_xml.aspx?Value=" + value);
                Thread.sleep(2000);
            }

            // ****페이지 이동 종료****

            // ****Data 추출 코드****

            List<String> day = Arrays.asList("sun", "mon", "tue", "wed", "thu", "fri", "sat", "sun", "mon", "tue", "wed", "thu", "fri", "sat");
            ArrayList<String> arrayList = new ArrayList<>();
            for (int j = 0; j < 32; j++) {
                arrayList.add("0");
            }

            String text;
            int count = 1;

            for (int j = 0; j < 7; j++) {
                List<WebElement> el = driver.findElements(By.tagName(day.get(LocalDate.of(2023, i, 1).getDayOfWeek().getValue() + j)));
                for (int k = 0; k < el.size(); k++) {
                    text = el.get(k).getText();
                    if (text.length() != 0) {
                        if (arrayList.get(Integer.parseInt(text.substring(0, 2))).equals("0")) {
                            count++;
                            arrayList.set(Integer.parseInt(text.substring(0, 2)), text);
                        }
                    }
                }
            }

            int index = -1;
            for (int j = 1; j < arrayList.size(); j++) {
                if (arrayList.get(j).length() < 5){
                    continue;
                }
                String [] contents = arrayList.get(j).split("/");
                for(int k = 0; k < contents.length; k++ ){
                    if(contents[k].contains("~")){
                        index = contents[k].indexOf("~");
                        Data data = new Data();
                        data.setYear(2023);
                        data.setMonth(i);
                        data.setDay(Integer.parseInt(arrayList.get(j).substring(0, 2)));
                        data.setStart_time(contents[k].substring(index-5, index));
                        data.setEnd_time(contents[k].substring(index+1, index+6));
                        data.setContents(contents[k].substring(0, index-5));

                        saveData(data); // 데이터베이스에 data 저장.
                        datas.add(data); // data객체를 datas에 넣어 반환 준비.
                    }
                }
            }
            count = 1;
        }
        // WebDriver 종료
        driver.quit();

        // DataDto 리스트 반환
        return datas;
    }
}
