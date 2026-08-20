# Developer setup

Target: new laptop → running system in under 30 minutes (excluding slow networks — first run downloads several GB of images).

## Prerequisites (macOS)

```sh
brew install colima docker docker-compose maven
colima start --cpu 4 --memory 8 --disk 60
```

Add the compose plugin dir to `~/.docker/config.json` (brew prints this caveat):

```json
{ "cliPluginsExtraDirs": ["/opt/homebrew/lib/docker/cli-plugins"] }
```

Maven needs the Ozone repository in `~/.m2/settings.xml` — copy the profile from [`infra/maven-settings-example.xml`](../infra/maven-settings-example.xml) and activate it.

## Prerequisites (Windows desktop — WSL2)

Do **everything inside the WSL2 distro** (Ubuntu). Never clone to a Windows path (`/mnt/c/...`) — the repo uses a symlink (`configuration/`) and LF line endings, and Docker bind-mount performance on `/mnt/c` is terrible.

```sh
# inside WSL2 Ubuntu
sudo apt update && sudo apt install -y docker.io docker-compose-v2 maven openjdk-17-jdk make git curl
sudo usermod -aG docker $USER   # then close and reopen the terminal
git clone <repo-url> ~/src/sankofa-ehr
```

(Alternatively Docker Desktop for Windows with the WSL2 backend works too — skip `docker.io` if you use it.)

Then add the Ozone Maven profile exactly as below.

Linux: install Docker Engine + compose plugin + Maven from your distro; skip Colima.

## Run it

```sh
make dev      # build the distro (first time) and start the stack
make logs     # tail container logs
make stop     # stop containers, keep data
make destroy  # stop and DELETE all data/volumes (DB is disposable pre-pilot)
```

`make dev` prints the URL and credentials when the stack is up.

## Troubleshooting

- **`docker: command not found` inside make** — ensure `colima status` says Running and `docker context ls` shows `colima` selected.
- **amd64-only image warnings on Apple Silicon** — noted per-image in `distro/`; if a container crash-loops with exec format errors, check its `platform:` override.
- **Port already in use** — the stack binds port 80/8080 on localhost; stop whatever holds it or edit the proxy port in `distro/`.
- **Wiping a broken environment** — `make destroy && make dev` rebuilds from scratch; all metadata comes from `configuration/`, so nothing of value lives in the local DB.
