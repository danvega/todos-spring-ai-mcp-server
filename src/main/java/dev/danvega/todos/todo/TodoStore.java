package dev.danvega.todos.todo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

/** Saves the whole inbox to one JSON file. */
@Component
public class TodoStore {

	private final Path file;
	private final JsonMapper mapper;

	public TodoStore(@Value("${todos.file}") String file, JsonMapper mapper) {
		this.file = Path.of(file);
		this.mapper = mapper;
	}

	public List<Todo> load() {
		if (!Files.exists(file)) {
			return List.of();
		}
		return List.of(mapper.readValue(file.toFile(), Todo[].class));
	}

	public void save(List<Todo> todos) {
		mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), todos);
	}
}
