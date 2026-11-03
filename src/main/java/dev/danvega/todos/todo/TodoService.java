package dev.danvega.todos.todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** The web page and the MCP tools both use this, so they always see the same inbox. */
@Service
public class TodoService {

	private final TodoStore store;
	private final List<Todo> todos;
	private long nextId;
	private long version;

	public TodoService(TodoStore store) {
		this.store = store;
		this.todos = new ArrayList<>(store.load());
		this.nextId = todos.stream().mapToLong(Todo::id).max().orElse(0) + 1;
	}

	public synchronized List<Todo> list(boolean done) {
		return todos.stream()
				.filter(todo -> todo.done() == done)
				.sorted(Comparator.comparingLong(Todo::id).reversed())
				.toList();
	}

	public synchronized Todo get(long id) {
		return todos.stream()
				.filter(todo -> todo.id() == id)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No todo with id " + id));
	}

	public synchronized Todo add(String title, String notes, Priority priority, LocalDate dueDate, String source) {
		Todo todo = new Todo(nextId++, checkTitle(title), blankToNull(notes),
				priority == null ? Priority.NORMAL : priority, dueDate, false, source);
		todos.add(todo);
		save();
		return todo;
	}

	public synchronized Todo update(long id, String title, String notes, Priority priority, LocalDate dueDate) {
		Todo old = get(id);
		return replace(new Todo(id, checkTitle(title), blankToNull(notes), priority, dueDate, old.done(), old.source()));
	}

	public synchronized Todo setDone(long id, boolean done) {
		Todo old = get(id);
		return replace(new Todo(id, old.title(), old.notes(), old.priority(), old.dueDate(), done, old.source()));
	}

	public synchronized Todo delete(long id) {
		Todo todo = get(id);
		todos.remove(todo);
		save();
		return todo;
	}

	public synchronized int clearDone() {
		int before = todos.size();
		todos.removeIf(Todo::done);
		save();
		return before - todos.size();
	}

	/** Goes up on every change. The web page polls it to notice changes made by agents. */
	public synchronized long version() {
		return version;
	}

	private Todo replace(Todo updated) {
		todos.replaceAll(todo -> todo.id() == updated.id() ? updated : todo);
		save();
		return updated;
	}

	private void save() {
		version++;
		store.save(todos);
	}

	private static String checkTitle(String title) {
		if (!StringUtils.hasText(title)) {
			throw new IllegalArgumentException("A todo needs a title");
		}
		return title.strip();
	}

	private static String blankToNull(String text) {
		return StringUtils.hasText(text) ? text.strip() : null;
	}
}
