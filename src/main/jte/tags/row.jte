@import dev.danvega.todos.todo.Priority
@import dev.danvega.todos.todo.Todo
@import dev.danvega.todos.web.InboxView
@param Todo todo
@param InboxView view

<li class="todo ${todo.done() ? "is-done" : ""} ${todo.priority() == Priority.HIGH ? "priority-high" : ""}" data-id="${todo.id()}">
    <form method="post" action="/todos/${todo.id()}/toggle" class="check-form">
        <button type="submit" class="check" aria-pressed="${todo.done()}" aria-label="Done: ${todo.title()}">
            <svg viewBox="0 0 16 16" aria-hidden="true"><path fill-rule="evenodd" d="M12.416 3.376a.75.75 0 0 1 .208 1.04l-5 7.5a.75.75 0 0 1-1.154.114l-3-3a.75.75 0 0 1 1.06-1.06l2.353 2.353 4.493-6.74a.75.75 0 0 1 1.04-.207Z" clip-rule="evenodd"/></svg>
            <span class="touch-target" aria-hidden="true"></span>
        </button>
    </form>
    <button type="button" class="todo-body" data-edit
            data-title="${todo.title()}"
            data-notes="${todo.notes()}"
            data-priority="${todo.priority().name()}"
            data-due="${todo.dueDate() == null ? "" : todo.dueDate().toString()}"
            data-source="${view.addedBy(todo)}">
        <span class="todo-title">${todo.title()}</span>
        @if(todo.notes() != null)
            <span class="todo-notes">${todo.notes()}</span>
        @endif
        <span class="todo-meta">
            @if(view.fromAgent(todo))
                <span class="agent-pill">${view.addedBy(todo)}</span>
            @endif
            @if(todo.priority() == Priority.HIGH && !todo.done())
                <span class="meta-priority">High priority</span>
            @endif
            @if(todo.dueDate() != null && !todo.done())
                <span class="meta-due ${view.overdue(todo) ? "is-overdue" : ""} ${view.dueSoon(todo) ? "is-soon" : ""}">${view.dueLabel(todo)}</span>
            @endif
            @if(!view.fromAgent(todo))
                <span>${view.addedBy(todo)}</span>
            @endif
        </span>
    </button>
</li>
