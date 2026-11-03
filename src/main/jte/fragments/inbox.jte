@import dev.danvega.todos.web.InboxView
@param InboxView view

<div data-version="${view.version()}">
    <section aria-labelledby="open-heading">
        <h2 id="open-heading" class="section-heading">${view.open().size()} open</h2>
        @if(view.open().isEmpty())
            <div class="empty">
                <p class="empty-title">Your inbox is clear.</p>
                <p>Type on the yellow line to capture something, or let an agent add it for you.</p>
            </div>
        @else
            <ul class="todo-list" role="list">
                @for(var todo : view.open())
                    @template.tags.row(todo = todo, view = view)
                @endfor
            </ul>
        @endif
    </section>

    @if(!view.done().isEmpty())
        <details class="done-section">
            <summary>Done <span class="done-count">${view.done().size()}</span></summary>
            <ul class="todo-list" role="list">
                @for(var todo : view.done())
                    @template.tags.row(todo = todo, view = view)
                @endfor
            </ul>
            <form method="post" action="/todos/clear-done" class="clear-form">
                <button type="submit" class="secondary">Clear done items</button>
            </form>
        </details>
    @endif
</div>
