def call(Map args = [:]) {
  def image      = args.get('image', "demo/app:${env.BUILD_NUMBER}")
  def contextDir = args.get('contextDir', '.')
  def dockerfile = args.get('dockerfile', 'Dockerfile')

  if (!fileExists("${contextDir}/${dockerfile}")) {
    error "dockerBuildImage: Dockerfile not found at ${contextDir}/${dockerfile}"
  }

  withEnv(["DOCKER_HOST=unix:///var/run/docker.sock"]) {
    def img = docker.build(image, "--file ${dockerfile} ${contextDir}")
    echo "Built image: ${img.id} (${image})"
  }
}
