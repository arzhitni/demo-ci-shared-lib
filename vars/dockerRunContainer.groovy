def call(Map args = [:]) {
  def image         = args.get('image')
  def name          = args.get('name', "app-${env.BUILD_NUMBER}")
  def hostPort      = args.get('hostPort', 18080)
  def containerPort = args.get('containerPort', 8080)

  if (!image) {
    error "dockerRunContainer: 'image' is required"
  }

  withEnv(["DOCKER_HOST=unix:///var/run/docker.sock"]) {
    sh """
      set -e
      docker rm -f ${name} >/dev/null 2>&1 || true
      docker run -d --name ${name} -p ${hostPort}:${containerPort} ${image}
      docker ps --filter "name=${name}"
    """
  }

  return name
}
