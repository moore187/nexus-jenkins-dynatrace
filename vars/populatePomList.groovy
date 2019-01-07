#!/usr/bin/env groovy
@Grab('groovy.io.FileType')
import groovy.io.FileType
def call(){
    def pomList = []
    def dir = new File(".")
    dir.eachFileRecurse (FileType.FILES) { file ->
        if (file.getName() == "pom.xml") {
        pomList << file
        }
    }
    return pomList
}