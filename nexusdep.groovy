import groovy.io.FileType
import groovy.json.JsonSlurper

static def populatePomList() {
    def pomList = []
    def dir = new File(".")
    dir.eachFileRecurse (FileType.FILES) { file ->
    if (file.getName() == "pom.xml") {
      pomList << file
    }
    }
    return pomList
}


static def populateVersionMap(ArrayList<File> pomList) {
    def versionMap = [:]
    pomList.each {
    def currentPom = new File(it.toString())
    def xml = new XmlParser().parse(currentPom)
    def dependencyListGPath = xml.dependencies.dependency
    for (dependency in dependencyListGPath) {
      dependency.children().each {
        def childString = it.name().toString()
        if (childString.indexOf("version") >= 0) {
          versionMap << [(dependency.artifactId.text()):it.text()]
        }
      }
    }
    }
    return versionMap
}

static def listRepositorys(){
    def nexusApiUrlRequest = new URL("http://nexus:8081/service/rest/v1/assets?repository=maven-releases").openConnection()
    def nexusApiRC = nexusApiUrlRequest.getResponseCode()
    def responseOutput = nexusApiUrlRequest.getInputStream().getText()
    if (nexusApiRC.equals(200)){
    println "http request successful. Repo Names will be populated"
    }
    else {
    println "http request failed. Please check the Nexus API"
    return 1
    }
    def json = new JsonSlurper().parseText(responseOutput)
    List<String> RepoNames = json.items
    nexusApiUrlRequest.disconnect()
    Map<String, Set> versionMap = new HashMap<String, Set>()

  RepoNames.each {

    if (it.path.contains(".jar") && !it.path.contains(".sha1")) {

      String path = new JsonSlurper().parseText(it).path
      String version = path.split('/')[2]
      String lib = path.split("/")[3].substring(0, path.split("/")[3].indexOf("."))

    }

  if (versionMap.contains(lib)){

    HashSet set = versionMap.get(key)
    set.add(version)
    versionMap.replace(lib, set)

  } else {

    def set = new HashSet<String>()
    set.add(version)
    versionMap.add(lib, set)
    
  }
  //Turn RepoNames into a map of name to version
  return RepoNames
  
}

static def compareToNexus(Map<String,Object> buildVersionMap, List<String> repoNames) {
    buildVersionMap.each{ k,v ->
    println "${k}:${v}"
    }
    repoNames.each {
    println it
    }

}

static void main(String[] args) {
    PomList = populatePomList()
    BuildVersionMap = populateVersionMap(PomList)
    RepoNames = listRepositorys()
    ComparedDependencies = compareToNexus(BuildVersionMap, RepoNames)
}