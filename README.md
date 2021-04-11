# TOM (Cross Platform)

Tom is a recording system.

## Development

### Running the App

Start a temporary local web server, build the app with the `dev` profile, and serve the app,
browser test runner and karma test runner with hot reload:

```sh
lein watch
```

### Running the App with electron
```sh
lein watch

electron .
```

Please be patient; it may take over 20 seconds to see any output, and over 40 seconds to complete.

When `[:app] Build completed` appears in the output, browse to
[http://localhost:8280/](http://localhost:8280/).

[`shadow-cljs`](https://github.com/thheller/shadow-cljs) will automatically push ClojureScript code
changes to your browser on save. To prevent a few common issues, see
[Hot Reload in ClojureScript: Things to avoid](https://code.thheller.com/blog/shadow-cljs/2019/08/25/hot-reload-in-clojurescript.html#things-to-avoid).

Opening the app in your browser starts a
[ClojureScript browser REPL](https://clojurescript.org/reference/repl#using-the-browser-as-an-evaluation-environment),
to which you may now connect.

### Running Tests

Build the app with the `prod` profile, start a temporary local web server, launch headless
Chrome/Chromium, run tests, and stop the web server:

```sh
lein ci
```

Please be patient; it may take over 15 seconds to see any output, and over 25 seconds to complete.

Or, for auto-reload:
```sh
lein watch
```

Then in another terminal:
```sh
karma start
```

### Compiling CSS with `lein-less`

Use [Less](http://lesscss.org/features/) to edit styles in `.less` files located in the
[`less/`](less/) directory. CSS files are compiled automatically on [`dev`](#running-the-app)
or [`prod`](#production) build.

Manually compile CSS files:
```sh
lein less once
```

The `resources/public/css/` directory is created, containing the compiled CSS files.

#### Compiling CSS with `lein-less` on change

Enable automatic compiling of CSS files when source `.less` files are changed:
```sh
lein less auto
```

### Running `shadow-cljs` Actions

See a list of [`shadow-cljs CLI`](https://shadow-cljs.github.io/docs/UsersGuide.html#_command_line)
actions:
```sh
lein run -m shadow.cljs.devtools.cli --help
```

Please be patient; it may take over 10 seconds to see any output. Also note that some actions shown
may not actually be supported, outputting "Unknown action." when run.

Run a shadow-cljs action on this project's build id (without the colon, just `app`):
```sh
lein run -m shadow.cljs.devtools.cli <action> app
```
### Debug Logging

The `debug?` variable in [`config.cljs`](src/cljs/tom/config.cljs) defaults to `true` in
[`dev`](#running-the-app) builds, and `false` in [`prod`](#production) builds.

Use `debug?` for logging or other tasks that should run only on `dev` builds:

```clj
(ns tom.example
  (:require [tom.config :as config])

(when config/debug?
  (println "This message will appear in the browser console only on dev builds."))
```

## Production

Build the app with the `prod` profile:

```sh
lein release
```

## Export for Desktop

For Windows
```sh
npm run package-win
```

For Mac
```sh
npm run package-mac
```
