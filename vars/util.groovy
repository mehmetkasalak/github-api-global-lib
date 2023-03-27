#!groovy


import groovy.json.JsonBuilder
import groovy.json.JsonOutput

@NonCPS
def mapToString(map) {
    return new JsonBuilder(map).toString()
}

@NonCPS
def jsonFormat(String text) {
    return JsonOutput.prettyPrint(text)
}

@NonCPS
def runScriptCrossPlatform(String command) {
    println command
    if (isUnix()) {
        sh script: "pwsh -Command ${command}"
    } else {
        powershell script: command
    }
}