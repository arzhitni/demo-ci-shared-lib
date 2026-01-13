def call(Map args = [:]) {
  def config = args.get('config', 'Release')
  def resultsDir = args.get('resultsDir', 'TestResults')

  sh """
    rm -rf ${resultsDir} || true
    dotnet test -c ${config} --no-build \
      --logger "trx;LogFileName=test_results.trx" \
      --results-directory ${resultsDir}
  """

  // Publishes trx results to Jenkins UI (MSTest plugin)
  step([$class: 'MSTestPublisher',
        testResultsFile: "**/${resultsDir}/*.trx",
        failOnError: false,
        keepLongStdio: true])
}
