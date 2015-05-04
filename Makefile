PROJECT = currentweather
REGISTRY = registry.giantswarm.io
# The default company equeals to your username
username :=  $(shell swarm user)

docker-build:
	docker build -t $(REGISTRY)/$(username)/$(PROJECT) .

docker-run-redis:
	docker run -d -p 6379:6379\
	 --name redis redis

docker-run-application: docker-build
	docker run --rm -p 4567:4567 --link redis:redis $(REGISTRY)/$(username)/$(PROJECT)

docker-push: docker-build
	docker push $(REGISTRY)/$(username)/$(PROJECT)

swarm-up: docker-push
	swarm up --var=username=$(username)
