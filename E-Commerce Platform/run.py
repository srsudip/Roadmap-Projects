#!/usr/bin/env python3
"""
E-Commerce Platform — build, run, and clean up.

Start:  cleans any leftover session, builds jars on the host (mvn clean
        package), builds Docker images, starts everything fresh.
Stop:   Ctrl+C removes all session containers, images, volumes, and
        runs mvn clean.
"""

import subprocess
import signal
import sys
import os
import shutil
import time

COMPOSE_DIR = os.path.dirname(os.path.abspath(__file__))
BACKEND_DIR = os.path.join(COMPOSE_DIR, "backend")
FRONTEND_DIR = os.path.join(COMPOSE_DIR, "frontend", "frontend-service")
HEALTH_TIMEOUT = 300  # seconds
# GCS mirror of Maven Central — repo.maven.apache.org rate-limits this IP
MVN = ["mvn", "-B", "-s", os.path.join(COMPOSE_DIR, "maven-settings.xml")]


def run(cmd, cwd=COMPOSE_DIR, **kwargs):
    """Run a command and stream output."""
    return subprocess.run(cmd, cwd=cwd, **kwargs)


def docker_cleanup():
    """Remove all session containers, images, volumes, and networks."""
    print("\n[CLEANUP] Removing e-commerce containers, images, and volumes...")
    run(["docker", "compose", "down", "--rmi", "all", "--volumes", "--remove-orphans"])
    print("[CLEANUP] Docker done.")


def maven_clean():
    """Remove build artifacts from backend and frontend."""
    print("[CLEANUP] Running mvn clean...")
    run(MVN + ["clean", "-q"], cwd=BACKEND_DIR)
    run(MVN + ["clean", "-q"], cwd=FRONTEND_DIR)
    print("[CLEANUP] Maven done.")


def full_cleanup():
    docker_cleanup()
    maven_clean()


def build_jars():
    """Build all jars on the host (in-container Maven hits rate limits)."""
    print("[BUILD] Building backend jars (mvn clean package)...")
    ret = run(MVN + ["clean", "package", "-DskipTests"], cwd=BACKEND_DIR)
    if ret.returncode != 0:
        print("[ERROR] Backend Maven build failed.")
        sys.exit(1)
    print("[BUILD] Building frontend jar...")
    ret = run(MVN + ["clean", "package", "-DskipTests"], cwd=FRONTEND_DIR)
    if ret.returncode != 0:
        print("[ERROR] Frontend Maven build failed.")
        sys.exit(1)


def wait_for_health():
    """Poll compose until no container is starting/unhealthy, or timeout."""
    deadline = time.time() + HEALTH_TIMEOUT
    while time.time() < deadline:
        out = subprocess.run(
            ["docker", "compose", "ps", "--format", "{{.Name}} {{.Status}}"],
            cwd=COMPOSE_DIR, capture_output=True, text=True,
        ).stdout
        pending = [l for l in out.splitlines()
                   if "starting" in l or "unhealthy" in l]
        if not pending:
            return True
        print(f"[WAIT] {len(pending)} container(s) not healthy yet...")
        time.sleep(10)
    print("[WARN] Timed out waiting for health. Current state:")
    return False


def handle_signal(sig, frame):
    """Handle Ctrl+C."""
    full_cleanup()
    sys.exit(0)


def main():
    signal.signal(signal.SIGINT, handle_signal)
    signal.signal(signal.SIGTERM, handle_signal)

    for tool in ("docker", "mvn"):
        if shutil.which(tool) is None:
            print(f"[ERROR] '{tool}' not found on PATH. Install it first.")
            sys.exit(1)

    print("[FRESH] Cleaning up any previous session...")
    full_cleanup()

    build_jars()

    print("[START] Building images and starting all containers...")
    ret = run(["docker", "compose", "up", "-d", "--build"])
    if ret.returncode != 0:
        print("[ERROR] docker compose up failed.")
        docker_cleanup()
        sys.exit(1)

    print("[START] Waiting for services to become healthy...")
    wait_for_health()
    run(["docker", "compose", "ps"])
    print()
    print("=" * 50)
    print("  E-Commerce Platform is running")
    print("  Frontend:     http://localhost:3000")
    print("  API Gateway:  http://localhost:8080")
    print("  Eureka:       http://localhost:8761")
    print("  RabbitMQ:     http://localhost:15672")
    print("=" * 50)
    print()
    print("Press Ctrl+C to stop and clean up.\n")

    # Keep running — tail logs so the user sees activity
    proc = subprocess.Popen(
        ["docker", "compose", "logs", "-f"],
        cwd=COMPOSE_DIR,
    )

    try:
        proc.wait()
    except KeyboardInterrupt:
        pass
    finally:
        proc.terminate()
        full_cleanup()


if __name__ == "__main__":
    main()
