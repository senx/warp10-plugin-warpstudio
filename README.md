# WarpStudio Plugin

![logo](https://github.com/senx/warp10-plugin-warpstudio/raw/master/warpStudio.png)

WarpStudio, the WarpScript editor

[Demo](http://studio.senx.io/)

## Installation

    wf g io.warp10 warp10-plugin-warpstudio --confDir=/path/to/warp10/conf.d --macroDir=/path/to/warp10/macros/ --libDir=/path/to/warp10/libs

## Features

- WarpScript Authoring
    - select your Warp 10 instance.
    - code colorization
    - code completion
    - stack result visualization
    - raw JSON result visualization
    - dataviz
        - Line/step/annotation charts
        - maps
        - images preview
        - tabular view
    - keep your code in the editor and add it to the Scratchpad
    - snapshots are stored on SenX servers, do not use this feature if this is a concern to you. It generates a permalink to share your code or an execution result.
    - save WarpScript under a path in your browser, find it in the "File Explorer"
    - export WarpScripts as a file
    - export result as a JSON file
- Geo Time Series explorer
- WarpScript Wizard
- Fetch, Update and Delete forms
- Manage multiple Warp 10 instances

## Configuration
```properties
# HTTP part
warpstudio.host=0.0.0.0
warpstudio.port=8081
# SSL PART
warpstudio.ssl.port=
warpstudio.ssl.host=
warpstudio.ssl.tcp.backlog=
warpstudio.ssl.acceptors=
warpstudio.ssl.selectors=
warpstudio.ssl.keystore.path=
warpstudio.ssl.keystore.password=
warpstudio.ssl.cert.alias=
warpstudio.ssl.keymanager.password=
warpstudio.ssl.idle.timeout=
warpstudio.defaults=
# Plugin activation
warp10.plugin.warpstudio=io.warp10.plugins.warpstudio.WarpStudioPlugin
```

## Add default GIT remotes and/or backends

    $ java -jar warp10-warpstudio-server-v1.0.31.jar myProps.properties


`myProps.properties`:

    warpstudio.defaults=./defBackend-tests.json


`defBackend-tests.json`:

```json
{
  "backends": [
    {
      "id": "localhost",
      "url": "http://localhost:8080/api/v0",
      "execEndpoint": "/exec",
      "findEndpoint": "/find",
      "fetchEndpoint": "/fetch",
      "updateEndpoint": "/update",
      "deleteEndpoint": "/delete",
      "timeUnit": "us",
      "name": "Localhost"
    }, ...
  ],
  "remotes": [
    {
      "name": "My cool project",
      "repo": "project-mars-rover",
      "url": "localhost:8080/api/v0",
      "token": "xxxxxxxxxxxxxxxxx",
      "id": "toto"
    }
  ]
}
```
