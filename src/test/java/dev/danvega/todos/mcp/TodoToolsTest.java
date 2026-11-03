package dev.danvega.todos.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dev.danvega.todos.todo.Priority;
import dev.danvega.todos.todo.Todo;
import dev.danvega.todos.todo.TodoService;
import dev.danvega.todos.todo.TodoStore;
import tools.jackson.databind.json.JsonMapper;

class TodoToolsTest {

	@TempDir
	Path dir;

	TodoService todos;
	TodoTools tools;

	@BeforeEach
	void setUp() {
		todos = new TodoService(new TodoStore(dir.resolve("todos.json").toString(), JsonMapper.builder().build()));
		tools = new TodoTools(todos);
	}

	@Test
	void updateKeepsFieldsYouLeaveOut() {
		Todo todo = todos.add("Draft README", "Cover both sides", Priority.HIGH, LocalDate.of(2026, 11, 4), "web");

		Todo updated = tools.updateTodo(todo.id(), "Write README", null, null, null);

		assertThat(updated).isEqualTo(new Todo(todo.id(), "Write README", "Cover both sides", Priority.HIGH,
				LocalDate.of(2026, 11, 4), false, "web"));
	}

	@Test
	void updateWorksWhenOptionalFieldsAreEmpty() {
		Todo todo = todos.add("Draft README", null, null, null, "web");

		assertThat(tools.updateTodo(todo.id(), null, null, Priority.LOW, null).priority()).isEqualTo(Priority.LOW);
	}

	@Test
	void listShowsOpenTodosByDefault() {
		Todo open = todos.add("Open", null, null, null, "web");
		Todo done = tools.completeTodo(todos.add("Done", null, null, null, "web").id());

		assertThat(tools.listTodos(null)).containsExactly(open);
		assertThat(tools.listTodos(true)).containsExactly(done);
	}
}
