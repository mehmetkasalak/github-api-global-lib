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
        applicationName : env.JOB_BASE_NAME,
        sourceBranch    : env.GIT_BRANCH,
        buildNumber     : env.BUILD_NUMBER,
        buildUrl        : env.BUILD_URL,
        sessionId       : env.SESSION_ID,
        config          : env.DOTNET_BUILD_CONFIGURATION
    )
}

def paramsMethod(params){
    def paramsObj = new JsonSlurper().parseText(params.toString())
    println paramsObj.TIMEOUT
    return paramsObj
}

def getAssetsStages(){
    def assetsStages = [
        build: [
            'Build Assets': {
                stage('Build Assets'){
                    parallel ([
                        'Build B2C Page-Layout Assets': {
                            stage('Build B2C Page-Layout Assets') {
                                echo 'Build B2C Page-Layout Assets'
                            }
                        },
                        'Build B2C Email Assets': {
                            stage('Build B2C Email Assets') {
                                echo 'Build B2C Email Assets'
                            }
                        }
                    ])
                }
            }
        ],
        publish: [
            'Publish Assets': {
                stage('Publish Assets'){
                    stage('Publish B2C Page-Layout Assets'){
                         echo 'Publish B2C Page-Layout Assets'
                    }
                    stage('Publish Email Assets'){
                        echo 'Publish Email Assets'
                    }
                }
            }
        ]
    ]
    return assetsStages
}
