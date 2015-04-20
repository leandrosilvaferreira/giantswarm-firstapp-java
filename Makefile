PROJECT = currentweather
REGISTRY = registry.giantswarm.io
# The default company equeals to your username
username :=  $(shell swarm user)

docker-build:
	docker build -t $(REGISTRY)/$(username)/$(PROJECT) .

docker-run-redis:
	docker run -d -p 6379:6379\
	 --name redis redis

docker-run-application: build	
	docker run --rm -p 4567:4567 --link redis:redis $(REGISTRY)/$(username)/$(PROJECT)

docker-push: build
	docker push $(REGISTRY)/$(username)/$(PROJECT)

swarm-up: push
	swarm up --var=username=$(username)