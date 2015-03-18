PROJECT = currentweather
REGISTRY = registry.giantswarm.io
# The default company equeals to your username
username :=  $(shell swarm user)

build:
	docker build -t $(REGISTRY)/$(username)/$(PROJECT) .

run-redis: build
	docker run --d -p 6379:6379\
	 --name redis redis

run-application: build	
	docker run --rm -p 4567:4567 --link redis:redis $(REGISTRY)/$(username)/$(PROJECT)

push: build
	docker push $(REGISTRY)/$(username)/$(PROJECT)

up: build
	swarm up --var=username=$(username)