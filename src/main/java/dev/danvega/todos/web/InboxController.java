package dev.danvega.todos.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.danvega.todos.todo.Priority;
import dev.danvega.todos.todo.TodoService;

@Controller
public class InboxController {

	record TodoForm(String title, String notes, Priority priority, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
	}

	private final TodoService todos;

	public InboxController(TodoService todos) {
		this.todos = todos;
	}

	@GetMapping("/")
	String inbox(Model model) {
		model.addAttribute("view", view());
		return "pages/inbox";
	}

	/** Just the list. The page script swaps it in after every change. */
	@GetMapping("/fragments/inbox")
	String list(Model model) {
		model.addAttribute("view", view());
		return "fragments/inbox";
	}

	@GetMapping("/api/version")
	@ResponseBody
	long version() {
		return todos.version();
	}

	@PostMapping("/todos")
	String add(TodoForm form) {
		todos.add(form.title(), form.notes(), form.priority(), form.dueDate(), "web");
		return "redirect:/";
	}

	@PostMapping("/todos/{id}")
	String update(@PathVariable long id, TodoForm form) {
		todos.update(id, form.title(), form.notes(), form.priority(), form.dueDate());
		return "redirect:/";
	}

	@PostMapping("/todos/{id}/toggle")
	String toggle(@PathVariable long id) {
		todos.setDone(id, !todos.get(id).done());
		return "redirect:/";
	}

	@PostMapping("/todos/{id}/delete")
	String delete(@PathVariable long id) {
		todos.delete(id);
		return "redirect:/";
	}

	@PostMapping("/todos/clear-done")
	String clearDone() {
		todos.clearDone();
		return "redirect:/";
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<String> badRequest(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	private InboxView view() {
		String mcpUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/mcp").toUriString();
		return new InboxView(todos.list(false), todos.list(true), todos.version(), mcpUrl);
	}
}
