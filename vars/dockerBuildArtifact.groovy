def call(Map args = [:]) {
  def config = args.get('config', 'Release')
  sh "dotnet restore"
  sh "dotnet build -c ${config} --no-restore"
}
