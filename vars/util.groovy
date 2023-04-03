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

def shouldCleanWorkspace(){
    println env.CLEAN_WORKSPACE
    println env.DEPLOYTO
    return env.CLEAN_WORKSPACE
}

def parallelStages(){
    return [
       create : [
        'Create Licensing.Data.Grpc nuget package': {
            stage('Create Licensing.Data.Grpc nuget package'){
                script{
                    echo 'Create Licensing.Data.Grpc nuget package'
                }
            }
        },
        'Create Licensing.Products.API.Grpc nuget package': {
            stage('Create Licensing.Products.API.Grpc nuget package'){
                script{
                    echo 'Create Licensing.Products.API.Grpc nuget package'
                }
            }
         }
       ]
    ]
}


def parallelBuildStages(){
    return [
       build : [
        'Build Licensing.Data.Grpc nuget package': {
            stage('Create Licensing.Data.Grpc nuget package'){
				stage('Build Licensing.Data.API docker image'){
					when{
						beforeAgent true
					}
					steps{
						script{
							echo 'Build Licensing.Data.API docker image'
						}
					}
				}
				stage('Build Licensing.B2C.API docker image'){
					when{
						beforeAgent true
					}
					steps{
						script{
							echo 'Build Licensing.B2C.API docker image'
						}
					}
				}
            }
        },
        'Create Licensing.Products.API.Grpc nuget package': {
            stage('Create Licensing.Products.API.Grpc nuget package'){
                script{
                    echo 'Create Licensing.Products.API.Grpc nuget package'
                }
            }
         }
       ]
    ]
}
