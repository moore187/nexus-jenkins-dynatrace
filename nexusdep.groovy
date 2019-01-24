import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

static def populatePomList() {
    def pomList = []
    def dir = new File(".")
    dir.eachFileRecurse (FileType.FILES) { file ->
    if (file.getName() == "pom.xml") {
      pomList << file
    }
    }
    println("PomList: ${pomList.toString()}")
    return pomList
}


static def populateVersionMap(ArrayList<File> pomList) {

    def versionMapNew = [:]
    pomList.each {
    def currentPom = new File(it.toString())
    def xml = new XmlParser().parse(currentPom)
    def dependencyListGPath = xml.dependencies.dependency
    for (dependency in dependencyListGPath) {
      dependency.children().each {
        def childString = it.name().toString()
        if (childString.indexOf("version") >= 0) {
          versionMapNew << [(dependency.artifactId.text()):it.text()]
        }
      }
    }
    }
    println("VersionMap: ${versionMapNew.toString()}")
    return versionMapNew
}

static def findVersionsOnNexus (Map versionMap) {
    
    Map<String, Set<String>> nexusSet = new HashMap<String, Set<String>>()

    List<String> RepoNames = []
    
    versionMap.each {

        def nexusApiUrlRequest = new URL("http://localhost:8081/service/rest/v1/search?name=${it.key}").openConnection()
        def nexusApiRC = nexusApiUrlRequest.getResponseCode()
        def responseOutput = nexusApiUrlRequest.getInputStream().getText()
        if (nexusApiRC.equals(200)) {
            println "Search returned values"
        } else {
            println "Error: ${nexusApiUrlRequest.getResponseCode()}"
            return 1
        }
        def json = new JsonSlurper().parseText(responseOutput)
        RepoNames.addAll(json.items)
        nexusApiUrlRequest.disconnect()

    }

    //Removed loop within loop
    RepoNames.each {

        String version = it.version
        String lib = it.name

        println("${lib}:${version}")

        if (versionMap.containsKey(lib)){

            //Used treeset to guarantee insertion order, order comes from API return

            Set set = new TreeSet<String>()
            set.addAll(versionMap.get(lib))
            set.add(version)
            versionMap.replace(lib, set)

        } else {

            def set = new HashSet<String>()
            set.add(version)
            versionMap.put(lib, version)

        }



        println("VersionMap: ${versionMap.toString()}")

    }

    return versionMap
    
}

//Paramiterise for NPM repos
//static def listRepositorys(){
//
//    def nexusApiUrlRequest = new URL("http://localhost:8081/service/rest/v1/assets?repository=maven-releases").openConnection()
//    def nexusApiRC = nexusApiUrlRequest.getResponseCode()
//    def responseOutput = nexusApiUrlRequest.getInputStream().getText()
//    if (nexusApiRC.equals(200)){
//    println "http request successful. Repo Names will be populated"
//    }
//    else {
//    println "http request failed. Please check the Nexus API"
//    return 1
//    }
//    def json = new JsonSlurper().parseText(responseOutput)
//    List<String> RepoNames = json.items
//    nexusApiUrlRequest.disconnect()
//    Map<String, Set> versionMap = new HashMap<String, Set>()
//
//    println("RepoNames: ${RepoNames.toString()}")
//
//    RepoNames.each {
//
//      String lib = ""
//      String version = ""
//      println("Current RepoNames Value: ${it.toString()}")
//        if (it.path.contains(".jar") && !it.path.contains(".sha1")) {
//
//          String path = it["downloadUrl"]
//            println(path.split("/"))
//          version = path.split('/')[7]
//          lib = path.split("/")[8].substring(0, path.split("/")[8].indexOf("-"))
//
//            if (versionMap.containsKey(lib)){
//
//                //Make sure version are sorted
//
//                Set set = new HashSet<String>()
//                set.addAll(versionMap.get(lib))
//                set.add(version)
//                versionMap.replace(lib, set)
//
//            } else {
//
//                def set = new HashSet<String>()
//                set.add(version)
//                versionMap.put(lib, version)
//
//            }
//
//        }
//
//      println("VersionMap: ${versionMap.toString()}")
//
//    }
//
//    println("Edited VersionMap: ${versionMap.toString()}")
//
//  return versionMap
//
//}
//
//static def compareToNexus(Map<String,Object> buildVersionMap, Map<String, Set> repoNames) {
//
//    Set<String> nexusKeySet = new HashSet<String>(repoNames.keySet())
//    Set<String> pomKeySet = new HashSet<String>(buildVersionMap.keySet())
//
//    println("Nexus Key Set: ${nexusKeySet.toString()}")
//    println("POM Key Set: ${pomKeySet.toString()}")
//
//    nexusKeySet.retainAll(pomKeySet)
//    Map<String, String> returnMap = new HashMap<>()
//
//    nexusKeySet.each {
//
//        if (repoNames.get(it)) returnMap[it] = (String) "Available Versions: ${repoNames.get(it).toString()}"
//    }
//
//    println("returnMap: ${returnMap.toString()}")
//    return returnMap
//
//}

static def urbancodeFileWriter(Map<String,String> buildVersionMap, Map<String, Set> repoNames) {

    def jsonText = new File('./file').getText()
    def slurper = new JsonSlurper().parseText(jsonText)
    def json = new JsonBuilder(slurper)

    // Change format to lib: Current=x, Latest=y

    buildVersionMap.each {println(it)}

    buildVersionMap.each {

        json.content.customProperties.putAt("Current ${it.key} Version: ${it.value}", "Available ${it.key} Versions: ${repoNames[it.key]}")

    }

    println("JSON: ${json.toPrettyString()}")
    return json

}

static void main(String[] args) {

    PomList = populatePomList()
    Map<String,String> pomVersions = populateVersionMap(PomList)
    Map<String, Set> ComparedDependencies = findVersionsOnNexus(pomVersions)
    Map<String,String> pomVersionsNew = populateVersionMap(PomList)
    jsonText = urbancodeFileWriter(pomVersionsNew, ComparedDependencies)

    //println(jsonText.toPrettyString())

    File file = new File("./file")
    file.bytes = []
    file << jsonText
}