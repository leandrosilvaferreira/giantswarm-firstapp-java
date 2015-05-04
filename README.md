# Your first application with Java, Redis, and Docker on Giant Swarm

This is a simple example of writing Java applications and deploying them on [Giant Swarm](https://giantswarm.io/). It queries an external API and caches the data in a Redis cache.

Check out the full tutorial here:

http://docs.giantswarm.io/guides/your-first-application/java/  

## Prerequisites

* Have a Giant Swarm account and the [swarm cli](http://docs.giantswarm.io/installation/gettingstarted/#installing-the-cli) running. [Request a free invite](https://giantswarm.io/).
* Have [Docker](https://docs.docker.com/installation/) running and be familiar with the basic Docker commands and Makefiles.
* Have [Docker Compose](https://docs.docker.com/compose/) running for easier local deployment.

## Edit source

The web application is implemented using [Spark](http://sparkjava.com/). When the page is opened, it fetches current weather data for Cologne from the [openweather API](http://api.openweathermap.org/data/2.5/weather?q=Cologne,DE) and prints some details from that data. The response is cached on Redis for 60 seconds.

## Run locally

To run the two required containers locally you just have to do

```
$ make docker-build
$ make docker-run-redis
$ make docker-run-application
```

This creates a custom Docker image with the Java binary, pushes it to our private registry, and starts both the custom Docker container and a Redis container. With Docker Compose you can simply use

```
$ docker-compose up
```

To test it on a Mac run something like: `curl $(boot2docker ip):4567`, on Linux machines `curl localhost:4567` should be sufficient.

## Run on Giant Swarm

To deploy it on Giant Swarm you just have to do a `make up`. This:

* builds appropriate Docker images
* uploads them to the Giant Swarm registry
* uploads the `swarm.json` and starts the application

To test it run something like: `curl currentweather-YOURUSERNAME.gigantic.io` and replace YOURUSERNAME with your Giant Swarm username.

For all build and deploy details see the [Makefile](Makefile).

```
$ swarm login
$ docker login https://registry.giantswarm.io
$ make docker-push
$ make swarm-up
```

For further documentation and guides see the [docs](https:://docs.giantswarm.io).

## In other languages

* [NodeJS](https://github.com/giantswarm/giantswarm-firstapp-nodejs)
* [Golang](https://github.com/giantswarm/giantswarm-firstapp-go)
* [PHP](https://github.com/giantswarm/giantswarm-firstapp-php)
* [Ruby](https://github.com/giantswarm/giantswarm-firstapp-ruby)
* [Python](https://github.com/giantswarm/giantswarm-firstapp-python)
