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

def stageObj(){
    def webAppStages = [
		build: [
			'Build Web App': {
				stage('Build Web App'){
						stage('Web App Tasks'){
							parallel ([
								failFast: true,
								'Build & Package Web App': {
									stage('Build Web App'){
										echo "Build Web App"
									}
									stage('Package Web App'){
										echo "Package Web App"
									}
								},
								'Test & Scan Web App': {
									stage('Test Web App'){
										echo "Test Web App"
									}
									stage('Scan Web App'){
										echo "Scan Web App"
									}
								}
							])
						}
				}
			}
		],
		publish: [
			'Publish Web App Docker Image to ECR': {
				stage('Publish Web App Docker Image to ECR'){
				echo "Publish Web App"
				}
			}
		]
    ]
    println new JsonBuilder(webAppStages.build).toString()
    println new JsonBuilder(webAppStages.publish).toString()
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

