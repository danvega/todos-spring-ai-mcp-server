// Forms post with fetch, then the list reloads in place.
// Every few seconds the page checks for changes made by agents.

const inbox = document.getElementById("inbox");
const status = document.getElementById("status");
const editor = document.getElementById("editor");
const editorForm = editor.querySelector("form");
const capture = document.getElementById("capture");

let version = currentVersion();

function currentVersion() {
    return Number(inbox.firstElementChild.dataset.version);
}

async function refresh(highlightNew = false) {
    const oldIds = [...inbox.querySelectorAll(".todo")].map(li => li.dataset.id);
    const doneOpen = inbox.querySelector("details")?.open;

    inbox.innerHTML = await (await fetch("/fragments/inbox")).text();
    version = currentVersion();

    const done = inbox.querySelector("details");
    if (done) done.open = doneOpen;
    if (highlightNew) {
        inbox.querySelectorAll(".todo")
            .forEach(li => { if (!oldIds.includes(li.dataset.id)) li.classList.add("just-arrived"); });
    }
}

document.addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.target;
    const action = event.submitter?.getAttribute("formaction") ?? form.action;

    const response = await fetch(action, { method: "POST", body: new URLSearchParams(new FormData(form)) });
    if (!response.ok) {
        status.textContent = await response.text();
        return;
    }
    status.textContent = "";
    await refresh();

    if (form === capture) {
        capture.reset();
        capture.elements.title.focus();
    }
    if (form === editorForm) editor.close();
});

// Click a todo to edit it
inbox.addEventListener("click", event => {
    const todo = event.target.closest("[data-edit]");
    if (!todo) return;
    const id = todo.closest(".todo").dataset.id;

    editorForm.action = `/todos/${id}`;
    editorForm.querySelector("[data-delete]").setAttribute("formaction", `/todos/${id}/delete`);
    const fields = editorForm.elements;
    fields.title.value = todo.dataset.title;
    fields.notes.value = todo.dataset.notes ?? "";
    fields.priority.value = todo.dataset.priority;
    fields.dueDate.value = todo.dataset.due;
    editor.querySelector("[data-source]").textContent = todo.dataset.source;
    editor.showModal();
});

editor.querySelector("[data-close]").addEventListener("click", () => editor.close());

document.querySelectorAll("[data-copy]").forEach(button => {
    button.addEventListener("click", async () => {
        await navigator.clipboard.writeText(document.getElementById(button.dataset.copy).textContent);
        button.textContent = "Copied";
    });
});

// Pick up changes made by agents
setInterval(async () => {
    if (editor.open) return;
    const latest = Number(await (await fetch("/api/version")).text());
    if (latest !== version) {
        await refresh(true);
        status.textContent = "Inbox updated.";
    }
}, 2500);
