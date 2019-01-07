#!/usr/bin/env groovy
@Grab('groovy.json.JsonSlurper')
import groovy.json.JsonSlurper
def call(){
    def nexusApiUrlRequest = new URL("http://nexus:8081/service/rest/v1/repositories").openConnection()
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
    def RepoNames = json.name
    nexusApiUrlRequest.disconnect()
    return RepoNames
}