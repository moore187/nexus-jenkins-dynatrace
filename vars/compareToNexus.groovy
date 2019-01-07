#!/usr/bin/env groovy
def call(Map<String,Object> buildVersionMap, List<String> repoNames){
  buildVersionMap.each{ k,v ->
    println "${k}:${v}"
  }
  repoNames.each {
    println it
  }
  //This is where I got to. You need to go through each repo name one by one and check if it has the current dependency in. 
  //repoNames has the list of all repos in Nexus, buildVersionMap has each required dependency and there specified versions.
  //Save the highest version it can find and then compare it in UCD when it makes the dynatrace deployment
}