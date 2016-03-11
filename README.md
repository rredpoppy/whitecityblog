# whitecity

Blog engine for WhiteCity Code

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server
    
Application requires a `settings.json` file with the following structure:

    {"md-folder":"whitecity/blog","smtp-host":"smtp.something.com","smtp-user":"test@test.com","smtp-pass":"passhere","smtp-port":587,"mail-from":"mine@mine.com","mail-to":"mine@mine.com","mail-subject":"New contact at My Blog"}

## License

MIT
