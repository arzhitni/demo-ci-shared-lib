def call(Map args = [:]) {
  def name = args.get('name')
  if (!name) {
    return
  }

  withEnv(["DOCKER_HOST=unix:///var/run/docker.sock"]) {
    sh "docker rm -f ${name} >/dev/null 2>&1 || true"
  }
}
