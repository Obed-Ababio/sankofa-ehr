# Sankofa EHR developer entry points (WP-002).
# Until WP-010 lands our own distro assembly, `make dev` runs the STOCK
# OpenMRS 3 reference application at the tag pinned in docs/VERSIONS.md.

REFAPP_TAG := 3.7.0# keep in sync with docs/VERSIONS.md §2
CACHE_DIR  := .cache/refapp-$(REFAPP_TAG)

.PHONY: dev dev-logs dev-down dev-destroy test build seed help

help:
	@echo "make dev         - start local stack (stock O3 refapp $(REFAPP_TAG) until WP-010)"
	@echo "make dev-logs    - follow backend logs"
	@echo "make dev-down    - stop the stack (data kept)"
	@echo "make dev-destroy - stop and DELETE volumes (fresh DB next start)"
	@echo "make test        - run test suites (placeholder until WP-020/WP-033)"
	@echo "make build       - build OMODs/ESMs (placeholder until WP-010/WP-020)"
	@echo "make seed        - load demo data (placeholder until WP-031)"

$(CACHE_DIR):
	git clone --depth 1 --branch $(REFAPP_TAG) \
	  https://github.com/openmrs/openmrs-distro-referenceapplication.git $(CACHE_DIR)

dev: $(CACHE_DIR)
	cd $(CACHE_DIR) && TAG=$(REFAPP_TAG) docker compose up -d
	@echo "First boot takes 5–15 min (DB setup)."
	@echo "UI:  http://localhost/openmrs/spa/login   (admin / Admin123)"

dev-logs: $(CACHE_DIR)
	cd $(CACHE_DIR) && docker compose logs -f backend

dev-down: $(CACHE_DIR)
	cd $(CACHE_DIR) && docker compose down

dev-destroy: $(CACHE_DIR)
	cd $(CACHE_DIR) && docker compose down -v

test:
	@echo "No test suites yet: unit tests land with WP-020 (ghanaemr-core)," ; \
	 echo "isolation suite with WP-021, e2e with WP-033." ; exit 1

build:
	@echo "Nothing to build yet: distro assembly is WP-010, OMOD is WP-020." ; exit 1

seed:
	@echo "Seed data generator lands with WP-031." ; exit 1
