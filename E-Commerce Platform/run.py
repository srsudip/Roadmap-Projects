#!/usr/bin/env python3
"""
E-Commerce Platform — Start, run, and clean up.
Ctrl+C to stop and clean everything.
"""

import subprocess
import signal
import sys
import os
import time

COMPOSE_DIR = os.path.dirname(os.path.abspath(__file__))
HEALTH_TIMEOUT = 300  # seconds


def run(cmd, **kwargs):
    """Run a command and stream output."""
    return subprocess.run(cmd, cwd=COMPOSE_DIR, **kwargs)


def cleanup():
    """Stop only e-commerce containers and their resources."""
    print("\n[CLEANUP] Stopping e-commerce containers...")
    run(["docker", "compose", "down", "--rmi", "all", "--volumes", "--remove-orphans"])
    print("[CLEANUP] Done.")


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
    cleanup()
    sys.exit(0)


def main():
    signal.signal(signal.SIGINT, handle_signal)
    signal.signal(signal.SIGTERM, handle_signal)

    print("[START] Building and starting all containers...")
    ret = run(["docker", "compose", "up", "-d", "--build"])
    if ret.returncode != 0:
        print("[ERROR] docker compose up failed.")
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
        cleanup()


if __name__ == "__main__":
    main()
