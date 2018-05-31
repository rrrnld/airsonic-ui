# Airsonic Web Client [![Build Status](https://travis-ci.org/heyarne/airsonic-ui.svg?branch=master)](https://travis-ci.org/heyarne/airsonic-ui)

This is just meant for exploration. If you want to see something more serious, take a look at [airsonic-ui](https://github.com/airsonic/airsonic-ui).

## Implemented so far

* Login
* Welcome screen (most recently played)
* Artist detail
* Album detail
* Play Track w/ next and previous
* Currently playing notification

## Development

The project is written in [ClojureScript](https://clojurescript.org/) and uses [re-frame](https://github.com/Day8/re-frame) for structure and peace of mind. The build tool is [shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html), which offers nice editor integration and interoparibility with the whole JavaScript ecosystem.
If you haven't worked with re-frame: I highly recommend it. Good resources are the project's [docs](https://github.com/Day8/re-frame/tree/master/docs) and a [post about its building blocks](https://purelyfunctional.tv/guide/re-frame-building-blocks/).

To build the project make sure you have Node.js (v6.0.0), npm and Java 8 installed in your system.

```
# after cloning the project, first install all dependencies
$ npm install

# start a continuous build with hot-code-reloading and continuous testing
# first build takes a while. open http://localhost:8080
$ npm run dev
```

**Note:** In dev mode this project comes with re-frame-10x. You can hit `Ctrl + h` to display the overlay and have a time traveling debugger.

## Tests

This project uses [karma](https://karma-runner.github.io/) for tests. Make sure to have Google Chrome installed, otherwise the watcher will time out. If you want to run tests continuously in the background, you may want to have Growl installed to show notifications ([see setup instructions](https://www.npmjs.com/package/karma-growl-reporter#installation)).

```
# run tests once
$ npm test
```

**Note:** If you want nice console output in your tests, make sure to `(enable-console-print!)`. You can call `println` afterwards like you're used to.

## Deployment

```
# build and optimize the code once for production
$ npm run build

# runs npm run build and publishes everything via gh-pages
$ npm run deploy
```

All build artifacts will be output in `/public`. Don't change anything in there as changes will be overwritten.
