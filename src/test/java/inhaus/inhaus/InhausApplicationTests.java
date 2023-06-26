package inhaus.inhaus;

import inhaus.inhaus.data.Data;
import inhaus.inhaus.repository.DataRepository;
import inhaus.inhaus.service.DataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InhausApplicationTests {

	@Autowired
	DataService dataService;
	@Autowired
	DataRepository dataRepository;


	@Test
	public void eventAdd() throws Exception{
		Data data = new Data();
		data.setMonth(13);
		data.setDay(1);
		data.setStart_time("12:00");
		data.setEnd_time("15:00");
		data.setContents("test1");

		dataRepository.save(data);

	}
	void contextLoads() {
	}

}