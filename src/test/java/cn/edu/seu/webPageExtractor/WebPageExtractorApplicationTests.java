package cn.edu.seu.webPageExtractor;

import cn.edu.seu.webPageExtractor.core.TaskInfo;
import cn.edu.seu.webPageExtractor.service.repository.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebPageExtractorApplicationTests {

	@Autowired
	TaskRepository taskRepository;
	@Test
	public void contextLoads() {
	}

	@Test
	public void  dbTest(){
		TaskInfo task = new TaskInfo();
		task.setDomain("手机领域");
		task.setKeyword("华为手机");
		task.setName("手机抽取任务");
		task.setTime(new Date());
		task.setLink("www.taobao.com");
		task.setState(10);
		taskRepository.save(task);
		System.out.println("good");
	}

}

