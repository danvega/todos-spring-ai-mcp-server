package dev.danvega.todos.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tools.jackson.databind.json.JsonMapper;

class TodoServiceTest {

	@TempDir
	Path dir;

	TodoStore store;
	TodoService todos;

	@BeforeEach
	void setUp() {
		store = new TodoStore(dir.resolve("todos.json").toString(), JsonMapper.builder().build());
		todos = new TodoService(store);
	}

	@Test
	void addUsesDefaults() {
		Todo todo = todos.add("  Book the Porto flight  ", "", null, null, "web");

		assertThat(todo.title()).isEqualTo("Book the Porto flight");
		assertThat(todo.notes()).isNull();
		assertThat(todo.priority()).isEqualTo(Priority.NORMAL);
		assertThat(todos.list(false)).containsExactly(todo);
	}

	@Test
	void titleIsRequired() {
		assertThatThrownBy(() -> todos.add(" ", null, null, null, "web")).hasMessage("A todo needs a title");
	}

	@Test
	void setDoneMovesBetweenLists() {
		Todo todo = todos.add("Reply to J-Fall", null, null, null, "web");

		todos.setDone(todo.id(), true);
		assertThat(todos.list(false)).isEmpty();
		assertThat(todos.list(true)).extracting(Todo::id).containsExactly(todo.id());
	}

	@Test
	void clearDoneKeepsOpenTodos() {
		Todo open = todos.add("Keep me", null, null, null, "web");
		todos.setDone(todos.add("Finished", null, null, null, "web").id(), true);

		assertThat(todos.clearDone()).isEqualTo(1);
		assertThat(todos.list(false)).containsExactly(open);
	}

	@Test
	void everyChangeBumpsTheVersion() {
		Todo todo = todos.add("One", null, null, null, "web");
		todos.setDone(todo.id(), true);

		assertThat(todos.version()).isEqualTo(2);
	}

	@Test
	void todosSurviveARestart() {
		Todo todo = todos.add("Persist me", "notes", Priority.HIGH, LocalDate.of(2026, 11, 14), "web");

		TodoService restarted = new TodoService(store);

		assertThat(restarted.get(todo.id())).isEqualTo(todo);
		assertThat(restarted.add("Next", null, null, null, "web").id()).isEqualTo(todo.id() + 1);
	}

	@Test
	void unknownIdThrows() {
		assertThatThrownBy(() -> todos.get(42)).hasMessage("No todo with id 42");
	}
}
