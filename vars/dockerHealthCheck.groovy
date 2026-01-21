def call(Map args = [:]) {
  def name         = args.get('name')                 // container name
  def path         = args.get('path', '/health')
  def port         = args.get('port', 8080)           // inside container
  def retries      = args.get('retries', 30)
  def sleepSeconds = args.get('sleepSeconds', 1)

  if (!name) {
    error "dockerHealthCheck: 'name' is required"
  }

  withEnv(["DOCKER_HOST=unix:///var/run/docker.sock"]) {
    sh """
      set -e

      # wait until container is running
      for i in \$(seq 1 10); do
        state=\$(docker inspect -f '{{.State.Status}}' ${name} 2>/dev/null || true)
        [ "\$state" = "running" ] && break
        echo "Container state: \$state (waiting...)"
        sleep 1
      done

      for i in \$(seq 1 ${retries}); do
        if docker run --rm --network "container:${name}" alpine:latest sh -lc \
          "apk add --no-cache wget >/dev/null 2>&1 && wget -qO- http://localhost:${port}${path} >/dev/null 2>&1"
        then
          echo "Health check OK: http://localhost:${port}${path}"
          exit 0
        fi
        echo "Waiting for service... (\$i/${retries})"
        sleep ${sleepSeconds}
      done

      echo "Service did not become healthy"
      docker logs ${name} || true
      exit 1
    """
  }
}
