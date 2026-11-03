package dev.danvega.todos.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import dev.danvega.todos.todo.Todo;

/** What the inbox templates need, plus a few helpers for labels. */
public record InboxView(List<Todo> open, List<Todo> done, long version, String mcpUrl) {

	public boolean fromAgent(Todo todo) {
		return !"web".equals(todo.source());
	}

	public String addedBy(Todo todo) {
		return fromAgent(todo) ? "Added by " + todo.source() : "Added by you";
	}

	public boolean overdue(Todo todo) {
		return daysLeft(todo) < 0;
	}

	public boolean dueSoon(Todo todo) {
		long days = daysLeft(todo);
		return days == 0 || days == 1;
	}

	public String dueLabel(Todo todo) {
		long days = daysLeft(todo);
		if (days < 0) return "Overdue";
		if (days == 0) return "Due today";
		if (days == 1) return "Due tomorrow";
		if (days < 7) return "Due " + todo.dueDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		return "Due " + todo.dueDate().format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH));
	}

	private static long daysLeft(Todo todo) {
		return ChronoUnit.DAYS.between(LocalDate.now(), todo.dueDate());
	}
}
