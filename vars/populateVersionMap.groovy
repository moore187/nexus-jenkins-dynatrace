#!/usr/bin/env groovy
@Grab('groovy.util.XmlSlurper')
import groovy.util.XmlSlurper
def call(ArrayList<File> pomList){
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