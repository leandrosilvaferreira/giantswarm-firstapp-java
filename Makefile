PROJECT = currentweather
REGISTRY = registry.giantswarm.io
# The default company equeals to your username
COMPANY :=  $(shell swarm user)
username :=  $(shell swarm user)

build:
	docker build -t $(REGISTRY)/$(COMPANY)/$(PROJECT) .

run-test-redis: build
	docker run --rm -p 6379:6379\
	 --name redis redis

run-test-application: build	
	docker run --rm -p 4567:4567 --link redis:redis $(REGISTRY)/$(COMPANY)/$(PROJECT)

push: build
	docker push $(REGISTRY)/$(COMPANY)/$(PROJECT)

up: build
	swarm up --var=COMPANY=$(COMPANY) --var=username=$(username)