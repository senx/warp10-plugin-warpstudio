#!/usr/bin/env groovy
import hudson.model.*

pipeline {
    agent any
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '3'))
    }
    environment {
        version = "${getVersion()}"
    }
    stages {

        stage('Checkout') {
            steps {
                this.notifyBuild('STARTED', version)
                git poll: false, url: 'git@gitlab.com:senx/warp10-plugin-warpstudio.git'
                // git credentialsId: 'github', poll: false, url: 'git@github.com:senx/warp10-plugin-warpstudio.git'
                echo "Building ${version}"
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Package') {
            steps {
                sh './gradlew -Duberjar shadowJar sourcesJar javadocJar'
                archiveArtifacts "build/libs/*.jar"
            }
        }

        stage('Deploy to Snapshot') {
            steps {
                nexusPublisher nexusInstanceId: 'nex', nexusRepositoryId: 'maven-snapshots', packages: [
                        [
                                $class         : 'MavenPackage',
                                mavenAssetList : [
                                        [classifier: '', extension: 'jar', filePath: 'build/libs/warp10-warpstudio-plugin-' + version + '.jar'],
                                        [classifier: 'sources', extension: 'jar', filePath: 'build/libs/warp10-warpstudio-plugin-' + version + '-sources.jar'],
                                        [classifier: 'javadoc', extension: 'jar', filePath: 'build/libs/warp10-warpstudio-plugin-' + version + '-javadoc.jar']
                                ],
                                mavenCoordinate: [artifactId: 'warp10-plugin-warpstudio', groupId: 'io.warp10', packaging: 'jar', version: version + '-SNAPSHOT']
                        ]
                ]
            }
        }

        stage('Deploy') {
            when {
                expression { return isItATagCommit() }
            }
            parallel {
                stage('Deploy to Bintray') {
                    options {
                        timeout(time: 2, unit: 'HOURS')
                    }
                    input {
                        message 'Should we deploy to Bintray?'
                    }
                    steps {
                        sh './gradlew -Duberjar -Dpublish bintrayUpload'
                        this.notifyBuild('PUBLISHED', version)
                    }
                }
            }
        }
    }
    post {
        success {
            this.notifyBuild('SUCCESSFUL', version)
        }
        failure {
            this.notifyBuild('FAILURE', version)
        }
        aborted {
            this.notifyBuild('ABORTED', version)
        }
        unstable {
            this.notifyBuild('UNSTABLE', version)
        }
    }
}

void notifyBuild(String buildStatus, String version) {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESSFUL'
    String subject = "${buildStatus}: Job ${env.JOB_NAME} [${env.BUILD_DISPLAY_NAME}] | ${version}" as String
    String summary = "${subject} (${env.BUILD_URL})" as String
    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else if (buildStatus == 'PUBLISHED') {
        color = 'BLUE'
        colorCode = '#0000FF'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
    }

    // Send notifications
    this.notifySlack(colorCode, summary, buildStatus)
}

void notifySlack(String color, String message, String buildStatus) {
    String slackURL = getParam('slackUrl')
    String payload = "{\"username\": \"${env.JOB_NAME}\",\"attachments\":[{\"title\": \"${env.JOB_NAME} ${buildStatus}\",\"color\": \"${color}\",\"text\": \"${message}\"}]}" as String
    sh "curl -X POST -H 'Content-type: application/json' --data '${payload}' ${slackURL}" as String
}

String getParam(String key) {
    return params.get(key)
}

String getVersion() {
    return sh(returnStdout: true, script: 'git describe --abbrev=0 --tags').trim()
}

boolean isItATagCommit() {
    String lastCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
    String tag = sh(returnStdout: true, script: "git show-ref --tags -d | grep ^${lastCommit} | sed -e 's,.* refs/tags/,,' -e 's/\\^{}//'").trim()
    return tag != ''
}
