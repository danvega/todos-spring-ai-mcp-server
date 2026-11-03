package dev.danvega.todos.mcp;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import dev.danvega.todos.todo.Priority;
import dev.danvega.todos.todo.Todo;
import dev.danvega.todos.todo.TodoService;

@Component
public class TodoTools {

	private final TodoService todos;

	public TodoTools(TodoService todos) {
		this.todos = todos;
	}

	@McpTool(description = "List open todos, or done todos when done is true")
	public List<Todo> listTodos(@McpToolParam(description = "Set to true for done todos", required = false) Boolean done) {
		return todos.list(Boolean.TRUE.equals(done));
	}

	@McpTool(description = "Add a todo to the inbox")
	public Todo addTodo(McpSyncServerExchange exchange,
			@McpToolParam(description = "Short title") String title,
			@McpToolParam(description = "Extra notes", required = false) String notes,
			@McpToolParam(description = "LOW, NORMAL or HIGH", required = false) Priority priority,
			@McpToolParam(description = "Due date as YYYY-MM-DD", required = false) LocalDate dueDate) {
		var client = exchange.getClientInfo();
		String source = Objects.requireNonNullElse(client.title(), client.name());
		return todos.add(title, notes, priority, dueDate, source);
	}

	@McpTool(description = "Update a todo. Fields you leave out stay the same.")
	public Todo updateTodo(@McpToolParam(description = "Todo id") long id,
			@McpToolParam(description = "New title", required = false) String title,
			@McpToolParam(description = "New notes", required = false) String notes,
			@McpToolParam(description = "LOW, NORMAL or HIGH", required = false) Priority priority,
			@McpToolParam(description = "New due date as YYYY-MM-DD", required = false) LocalDate dueDate) {
		Todo old = todos.get(id);
		return todos.update(id,
				title != null ? title : old.title(),
				notes != null ? notes : old.notes(),
				priority != null ? priority : old.priority(),
				dueDate != null ? dueDate : old.dueDate());
	}

	@McpTool(description = "Mark a todo as done")
	public Todo completeTodo(@McpToolParam(description = "Todo id") long id) {
		return todos.setDone(id, true);
	}

	@McpTool(description = "Move a done todo back to open")
	public Todo reopenTodo(@McpToolParam(description = "Todo id") long id) {
		return todos.setDone(id, false);
	}

	@McpTool(description = "Delete a todo")
	public Todo deleteTodo(@McpToolParam(description = "Todo id") long id) {
		return todos.delete(id);
	}

	@McpTool(description = "Delete all done todos")
	public String clearDone() {
		return "Deleted " + todos.clearDone() + " done todos";
	}
}
