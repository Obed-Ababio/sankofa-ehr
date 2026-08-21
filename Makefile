DISTRO_VERSION := 1.0.0-SNAPSHOT
DISTRO_TARGET  := distro/target/sankofa-emr-$(DISTRO_VERSION)
RUN_SCRIPTS    := $(DISTRO_TARGET)/run/docker/scripts

.PHONY: dev build start stop destroy logs ps

## dev: rebuild the distro and start the full stack. Always rebuilds —
## a stale target/ silently serves old config, which costs far more than
## the few seconds a cached build takes.
dev: build start

## build: force a full rebuild of the distro.
## NOTE: `clean` deletes target/, which the running containers bind-mount —
## after building while the stack is up, restart containers so mounts
## re-resolve: docker restart ozone-openmrs-1 ozone-frontend-1
build:
	cd distro/scripts && ./mvnw -f ../pom.xml clean package

start:
	cd $(RUN_SCRIPTS) && ./start.sh
	@echo ""
	@echo "OpenMRS 3: http://localhost  (admin / Admin123)"

stop:
	cd $(RUN_SCRIPTS) && ./stop-demo.sh

## destroy: stop containers and DELETE all data/volumes (DB is disposable pre-pilot)
destroy:
	cd $(RUN_SCRIPTS) && ./destroy-demo.sh

logs:
	docker compose -p ozone logs -f --tail=100

ps:
	docker compose -p ozone ps
