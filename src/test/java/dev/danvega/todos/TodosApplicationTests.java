package dev.danvega.todos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "todos.file=target/test-todos.json")
class TodosApplicationTests {

	@Test
	void contextLoads() {
	}

}
