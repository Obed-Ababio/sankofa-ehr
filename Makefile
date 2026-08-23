DISTRO_VERSION := 1.0.0-SNAPSHOT
DISTRO_TARGET  := distro/target/sankofa-emr-$(DISTRO_VERSION)
RUN_SCRIPTS    := $(DISTRO_TARGET)/run/docker/scripts

.PHONY: dev build start stop destroy logs ps

## dev: rebuild the distro and start the full stack. Always rebuilds —
## a stale target/ silently serves old config, which costs far more than
## the few seconds a cached build takes. If the stack was already up, the
## build deletes target/ under the containers' bind mounts, so openmrs and
## frontend must be restarted or they silently serve stale/404 config.
dev:
	@was_up=$$(docker ps -q --filter name=ozone-openmrs-1); \
	$(MAKE) build start || exit 1; \
	if [ -n "$$was_up" ]; then \
		echo "Stack was already up — restarting openmrs+frontend so bind mounts re-resolve to the fresh target/"; \
		docker restart ozone-openmrs-1 ozone-frontend-1; \
		echo "Waiting for OpenMRS to come back (~2 min)..."; \
		until curl -sf -o /dev/null http://localhost/openmrs/health/started; do sleep 5; done; \
		if ! curl -sf -o /dev/null http://localhost/openmrs/spa/ozone/sankofa-frontend-config.json; then \
			echo "Frontend config 502 — proxy likely holds stale IPs after a container recreate; restarting proxy"; \
			docker restart ozone-proxy-1; \
			for i in $$(seq 1 24); do \
				curl -sf -o /dev/null http://localhost/openmrs/spa/ozone/sankofa-frontend-config.json && break; sleep 5; \
			done; \
		fi; \
		curl -sf -o /dev/null http://localhost/openmrs/spa/ozone/sankofa-frontend-config.json \
			&& echo "Healthy; Sankofa frontend config serving." \
			|| { echo "ERROR: sankofa-frontend-config.json not served after restart"; exit 1; }; \
	fi

## build: force a full rebuild of the distro.
## NOTE: `clean` deletes target/, which the running containers bind-mount —
## `make dev` handles the required restart; after a bare `make build` with the
## stack up, restart manually: docker restart ozone-openmrs-1 ozone-frontend-1
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
