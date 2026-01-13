def call(Map args = [:]) {
  def project = args.get('project') // required
  if (!project) {
    error "dotnetPublish: 'project' is required, e.g. project: 'src/DemoApi/DemoApi.csproj'"
  }

  def config = args.get('config', 'Release')
  def outDir = args.get('outDir', 'publish')

  sh "rm -rf ${outDir} || true"
  sh "dotnet publish ${project} -c ${config} -o ${outDir}"
}
