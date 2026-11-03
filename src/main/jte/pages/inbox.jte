@import dev.danvega.todos.web.InboxView
@param InboxView view

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inbox</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Mona+Sans:wdth,wght@75..125,200..900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/inbox.css">
    <link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32'%3E%3Crect width='32' height='32' rx='8' fill='%23FFE45C'/%3E%3Cpath d='M9 16.5l4.5 4.5L23 11.5' fill='none' stroke='%2309090B' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E">
    <script src="/inbox.js" defer></script>
</head>
<body>
<div class="app">
    <header class="band">
        <div class="band-inner">
            <div class="masthead">
                <a href="/" class="brand" aria-label="Homepage">
                    <svg class="brand-mark" viewBox="0 0 16 16" aria-hidden="true"><path fill-rule="evenodd" d="M12.416 3.376a.75.75 0 0 1 .208 1.04l-5 7.5a.75.75 0 0 1-1.154.114l-3-3a.75.75 0 0 1 1.06-1.06l2.353 2.353 4.493-6.74a.75.75 0 0 1 1.04-.207Z" clip-rule="evenodd"/></svg>
                    <span>Inbox</span>
                </a>
                <details class="connect">
                    <summary>Connect an agent</summary>
                    <div class="connect-panel">
                        <p>Agents see this same inbox over MCP. Point any MCP client at this address.</p>
                        <div class="copy-row">
                            <code id="mcp-url">${view.mcpUrl()}</code>
                            <button type="button" class="secondary" data-copy="mcp-url">Copy</button>
                        </div>
                        <p class="hint">For Claude Code, run this once.</p>
                        <div class="copy-row">
                            <code id="mcp-cmd">claude mcp add --transport http todos ${view.mcpUrl()}</code>
                            <button type="button" class="secondary" data-copy="mcp-cmd">Copy</button>
                        </div>
                    </div>
                </details>
            </div>

            <form id="capture" class="capture" method="post" action="/todos" autocomplete="off">
                <h1 class="visually-hidden">Inbox</h1>
                <label class="visually-hidden" for="capture-title">New todo</label>
                <input id="capture-title" name="title" class="capture-title" placeholder="What needs doing?"
                       required>
                <div class="capture-extras">
                    <label class="field" for="capture-due">
                        <span>Due</span>
                        <input id="capture-due" type="date" name="dueDate">
                    </label>
                    <label class="field" for="capture-priority">
                        <span>Priority</span>
                        <span class="select">
                            <select id="capture-priority" name="priority">
                                <option value="NORMAL" selected>Normal</option>
                                <option value="HIGH">High</option>
                                <option value="LOW">Low</option>
                            </select>
                            <svg viewBox="0 0 8 5" width="8" height="5" fill="none" aria-hidden="true"><path d="M.5.5 4 4 7.5.5" stroke="currentcolor"/></svg>
                        </span>
                    </label>
                    <label class="field field-wide" for="capture-notes">
                        <span>Notes</span>
                        <input id="capture-notes" name="notes" placeholder="Optional">
                    </label>
                    <button type="submit" class="primary">Add to inbox</button>
                </div>
            </form>
        </div>
    </header>

    <div class="page">
        <p id="status" class="status-line" role="status" aria-live="polite"></p>
        <main id="inbox">
            @template.fragments.inbox(view = view)
        </main>
    </div>
</div>

<dialog id="editor" class="editor" aria-labelledby="editor-heading">
    <form method="post">
        <h2 id="editor-heading">Edit todo</h2>
        <label class="field" for="edit-title">
            <span>Title</span>
            <input id="edit-title" name="title" required>
        </label>
        <label class="field" for="edit-notes">
            <span>Notes</span>
            <textarea id="edit-notes" name="notes" rows="4" placeholder="Anything worth remembering"></textarea>
        </label>
        <div class="field-pair">
            <label class="field" for="edit-due">
                <span>Due</span>
                <input id="edit-due" type="date" name="dueDate">
            </label>
            <label class="field" for="edit-priority">
                <span>Priority</span>
                <span class="select">
                    <select id="edit-priority" name="priority">
                        <option value="HIGH">High</option>
                        <option value="NORMAL">Normal</option>
                        <option value="LOW">Low</option>
                    </select>
                    <svg viewBox="0 0 8 5" width="8" height="5" fill="none" aria-hidden="true"><path d="M.5.5 4 4 7.5.5" stroke="currentcolor"/></svg>
                </span>
            </label>
        </div>
        <p class="editor-source" data-source></p>
        <div class="editor-actions">
            <button type="submit" class="secondary danger" data-delete formnovalidate>Delete</button>
            <span class="spacer"></span>
            <button type="button" class="secondary" data-close>Cancel</button>
            <button type="submit" class="primary">Save changes</button>
        </div>
    </form>
</dialog>
</body>
</html>
