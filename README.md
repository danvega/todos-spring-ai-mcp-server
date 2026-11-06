# Todo inbox

A todo inbox you can use from a web page. This branch is the starting code. It has the web page and the service behind it, but no MCP server yet. We add the dependencies and the tools that let AI agents use the same inbox. The finished app is on `main`.

![The inbox page, with a yellow capture band above a list of open todos](images/inbox.jpg)

## Tested with

Java 27, Spring Boot 4.0.9, jte 3.2.5.

## What you need

- JDK 27

## Run it

```bash
./mvnw spring-boot:run
```

Open http://localhost:8093. The inbox is saved to `todos.json` in the project folder.

## Try it

Type a title on the big line in the yellow band and press Enter. Click the circle to finish an item. Click a title to edit its notes, priority, or due date, or to delete it. Press `n` or `/` to jump back to the capture line.

## What we add

- The Spring AI MCP server starter and the Spring AI BOM in `pom.xml`.
- The MCP server settings in `application.yaml`.
- A `TodoTools` class that turns the actions in `TodoService` into MCP tools.

## Where to look

- [TodoService.java](src/main/java/dev/danvega/todos/todo/TodoService.java) holds every action. The page calls it today, and the MCP tools will call it too.
- [InboxController.java](src/main/java/dev/danvega/todos/web/InboxController.java) serves the page and its form posts.

## Good to know

- The "Connect an agent" panel already shows the `/mcp` address. That address returns 404 until the MCP server is in place.
- The page polls `/api/version` every 2.5 seconds. That is how it will pick up changes an agent makes.
- There is no login. Anyone who can reach port 8093 can read and change the inbox. Keep it on localhost.
- jte runs in development mode, so template edits show up on refresh.
