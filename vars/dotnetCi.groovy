def call(Map args = [:]) {
  def image      = args.get('image', 'jenkins-dotnet-agent:8.0')
  def config     = args.get('config', 'Release')
  def project    = args.get('project') // required
  def publishDir = args.get('publishDir', 'publish')
  def resultsDir = args.get('resultsDir', 'TestResults')
  def stashName  = args.get('stashName', 'publish-dir')

  if (!project) {
    error "dotnetCi: 'project' is required, e.g. project: 'src/DemoApi/DemoApi.csproj'"
  }

  // Runs all dotnet commands inside a docker container (agent image)
  // and keeps workspace consistent via reuseNode-like behavior.
  docker.image(image).inside('-u root:root') {
    sh 'dotnet --info'

    sh 'dotnet restore'
    sh "dotnet build -c ${config} --no-restore"

    sh """
      rm -rf ${resultsDir} || true
      dotnet test -c ${config} --no-build \
        --logger "trx;LogFileName=test_results.trx" \
        --results-directory ${resultsDir}
    """

    // Publish test results to Jenkins UI
    step([$class: 'MSTestPublisher',
          testResultsFile: "**/${resultsDir}/*.trx",
          failOnError: false,
          keepLongStdio: true])

    sh "rm -rf ${publishDir} || true"
    sh "dotnet publish ${project} -c ${config} -o ${publishDir}"

    // Make publish/ available for later stages (e.g., docker build on controller)
    stash name: stashName, includes: "${publishDir}/**"
  }
}