#!groovy

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

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

def getRunAsBranch(){
    def runAsBranch=env.BRANCH_NAME
    if(params.RUN_AS_BRANCH != null && params.RUN_AS_BRANCH.trim().length() != 0){
        runAsBranch=params.RUN_AS_BRANCH
    }
    return runAsBranch
}

def environmentVars(){
    return mapToString (
            applicationName: env.JOB_BASE_NAME,
            sourceBranch   : env.GIT_BRANCH,
            buildNumber    : env.BUILD_NUMBER,
            buildUrl       : env.BUILD_URL)
}

def paramsMethod(params){
    def slurper = new JsonSlurper()
    def jsonStr = mapToString(JsonOutput.toJson(params));
    def paramsObj = slurper.parseText(jsonStr)
    echo paramsObj
    echo paramsObj.TIMEOUT
    return paramsObj
}
