#!/usr/bin/env groovy
@Grab('groovy.json.JsonSlurper')
import groovy.json.JsonSlurper
def call() {
    def nexusApiUrlRequest = new URL("http://nexus:8081/service/rest/v1/assets?repository=maven-releases").openConnection()
    def nexusApiRC = nexusApiUrlRequest.getResponseCode()
    def responseOutput = nexusApiUrlRequest.getInputStream().getText()
    if (nexusApiRC.equals(200)) {
        println "http request successful. Repo Names will be populated"
    } else {
        println "http request failed. Please check the Nexus API"
        return 1
    }
    def json = new JsonSlurper().parseText(responseOutput)
    List<String> RepoNames = json.items
    nexusApiUrlRequest.disconnect()

    RepoNames.each {

        if (it.path.contains(".jar") && !it.path.contains(".sha1")) {

            String path = new JsonSlurper().parseText(it).path
            String version = path.split('/')[2]
            def lib = path.split("/")[3].substring(0, path.split("/")[3].indexOf("."))

        }

        if (version.contains(lib)) {

            Set set = version.indexOf(lib).value
            set.add(version)
            versionMap set

        } else {


        }
        //Turn RepoNames into a map of name to version
        return RepoNames
    }
}