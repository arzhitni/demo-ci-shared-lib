def call(Map args = [:]) {
  def image   = args.get('image', 'mcr.microsoft.com/dotnet/sdk:9.0')
  def config  = args.get('config', 'Release')
  def results = args.get('resultsDir', 'TestResults')

  docker.image(image).inside('-u root:root') {
    sh 'dotnet --info'
    sh 'dotnet restore'
    sh "dotnet build -c ${config} --no-restore"

    sh """
      rm -rf ${results} || true
      dotnet test -c ${config} --no-build \
        --logger "trx;LogFileName=test_results.trx" \
        --results-directory ${results}
    """

    step([$class: 'MSTestPublisher',
      testResultsFile: "**/${results}/*.trx",
      failOnError: false,
      keepLongStdio: true
    ])
  }
}
