const url = new URL(window.location);

// URLSearchParams 객체
const urlParams = url.searchParams;

// URLSearchParams.get() 쿼리에서 년/원/일 받아오기
const date = urlParams.get("date");
const month = urlParams.get("month");
const year = urlParams.get("year");
console.log(date);
const Ndate = Number(date); //리스트 인덱싱을 위한 문자열 숫자로 변환
const Nmonth = Number(month); //리스트 인덱싱을 위한 문자열 숫자로 변환
const Nyear = Number(year); //리스트 인덱싱을 위한 문자열 숫자로 변환
console.log(Ndate);

// Load the JSON file

Contents = fetch("http://localhost:8080/data/crawl")
  .then((response) => response.json()) // parse the response as JSON
  .then((data) => {
    // .then의 역할?
    const Schedule = data; //schedule에 schedule.json 의 schedule data 배치
    // iterate over each day in the schedule
    let DayTitle = `${Nyear}년 ${Nmonth}월 ${Ndate}일` + "<br>";
    let OutPut = ``;
    //문자열이 아닌 리스트를 문자로 출력하려면 ${}를 사용하자
    //i를 data 크기만큼 반복
    //데이터와 선택한 날짜의 년/월/일이 일치시 일치하는 셀데이터 연속으로 출력
    for (let i = 0; i<Schedule.length; i++) {
      if(Schedule[i].year == Nyear && Schedule[i].month == Nmonth && Schedule[i].day == Ndate) {
        OutPut += `${Schedule[i].start_time}-${Schedule[i].end_time} : ${Schedule[i].contents} ` + "<br>"  + "<br>";
      }
    }
    
    HdayContents.innerHTML = OutPut;
    HdayTitle.innerHTML = DayTitle;
    console.log(OutPut);
  })
  .catch((error) => console.error(error)); // handle any errors

//date를 int로 만들어주는 함수
