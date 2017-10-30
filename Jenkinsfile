@Library('Pipeline Libraries@fortify-test') _

UPSTREAM_TRIGGERS = [
    "common-dependencies",
    "common-client-parent",
    "common-messaging-parent",
    "compute-capabilities-api",
    "engineering-standards-service-parent",
    "hdp-capability-registry-client-parent",
    "system-integration-sdk",
    "virtualization-capabilities-api"
]
properties(getBuildProperties(upstreamRepos: UPSTREAM_TRIGGERS))

pipeline { 
    parameters {
        string(name: 'dockerImagesDel', defaultValue: 'true')
        string(name: 'dockerRegistry',  defaultValue: 'docker-dev-local.art.local')
        string(name: 'dockerImageTag',  defaultValue: '${BRANCH_NAME}.${BUILD_NUMBER}')
    }
    agent {
        node {
            label 'maven-builder'
            customWorkspace "workspace/${env.JOB_NAME}"
        }
    }
    environment {
        GITHUB_TOKEN = credentials('github-02')
    }
    options { 
        skipDefaultCheckout()
        timestamps()
    }
    tools {
        maven 'linux-maven-3.3.9'
        jdk 'linux-jdk1.8.0_102'
    }
    stages {
        stage('Checkout') {
            steps {
                doCheckout()
            }
        }
        stage('Fortify Scan') {
            steps {
                runFortifyScan()
            }
        }
        stage("Build") {
            steps {
                script {
                    if (env.BRANCH_NAME ==~ /master|stable\/.*/) {
                        sh "mvn clean deploy -Dmaven.repo.local=.repo -DskipDocker=false -PbuildDockerImageOnJenkins -Ddocker.registry=${params.dockerRegistry} -DdockerImage.tag=${params.dockerImageTag} -DdeleteDockerImages=${params.dockerImagesDel}"
                    } else {
                        sh "mvn clean install -Dmaven.repo.local=.repo -DskipDocker=false -PbuildDockerImageOnJenkins -Ddocker.registry=${params.dockerRegistry} -DdockerImage.tag=${params.dockerImageTag} -DdeleteDockerImages=${params.dockerImagesDel}"
                    }
                }
            }
        }
        stage('Record Test Results') {
            steps {
                junit '**/target/*-reports/*.xml'
            }
        }
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/*.rpm', fingerprint: true 
            }
        }
        stage('Upload to Repo') {
            steps {
                uploadArtifactsToArtifactory()
            }
        }
        stage('SonarQube Analysis') {
            steps {
                doSonarAnalysis()    
            }
        }
        stage('Third Party Audit') {
            steps {
                doThirdPartyAudit()
            }
        }
        stage('PasswordScan') {
            steps {
                doPwScan()
            }
        }
        stage('Github Release') {
            steps {
                githubRelease()
            }
        }
        stage('NexB Scan') {
            steps {
                doNexbScanning()
            }
        }
        stage('Run pytest Scanner') {
            steps {
                runPyTestScanner()
            }
    	}
    }
    post {
        always {
            cleanWorkspace()   
        }
        success {
            successEmail(send_to: 'jeffery.cheng@dell.com', only: true)
        }
        failure {
            failureEmail(send_to: 'jeffery.cheng@dell.com', only: true)
        }
    }
}
