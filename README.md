# Airsonic Web Client

This is just meant for exploration. If you want to see something more serious, take a look at [airsonic-ui](https://github.com/airsonic/airsonic-ui).

## Development

The project is written in [ClojureScript](https://clojurescript.org/) and uses [re-frame](https://github.com/Day8/re-frame) for structure and peace of mind. The build tool is [shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html), which offers nice editor integration and interoparibility with the whole JavaScript ecosystem.
If you haven't worked with re-frame: I highly recommend it. Good resources are the project's [docs](https://github.com/Day8/re-frame/tree/master/docs) and a [post about its building blocks](https://purelyfunctional.tv/guide/re-frame-building-blocks/).

To build the project make sure you have Node.js (v6.0.0), npm and Java 8 installed in your system.

```
# after cloning the project, first install all dependencies
$ npm install
# start a continuous build with hot-code-reloading; first build takes a while
$ npm run dev
# build and optimize the code once for production
$ npm run build
```

**Note:** In dev mode this project comes with re-frame-10x. You can hit `Ctrl + h` to display the overlay and have a time traveling debugger.

## Build artifacts

Everything you need to serve the app can be found inside the `public` folder.
