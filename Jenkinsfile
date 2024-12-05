pipeline {
    agent any

    environment {
        REPO = 'ktb-cpplab/cpplab-be'
        ECR_REPO = '891612581533.dkr.ecr.ap-northeast-2.amazonaws.com/cpplab/be'
        ECR_CREDENTIALS_ID = 'ecr:ap-northeast-2:AWS_CREDENTIALS'
        GITHUB_CREDENTIALS_ID = 'github_token'
        REMOTE_USER = 'ubuntu'
        ECS_CLUSTER_NAME = 'cpplab-ecs-cluster'
        ECS_SERVICE_NAME = 'my-be-service'
        AWS_REGION = 'ap-northeast-2'
        BRANCH_NAME = "${env.GIT_BRANCH}"
        DOCKER_TAG = "${env.BUILD_NUMBER}"  // Jenkins build number
        AWS_CREDENTIALS_ID = 'AWS_CREDENTIALS'
        APPLICATION_YML = 'application.yml'
        PROMETHEUS_YML = 'prometheus.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    currentBuild.description = 'Checkout'
                    git branch: "${BRANCH_NAME}", url: "https://github.com/${REPO}.git", credentialsId: "${GITHUB_CREDENTIALS_ID}"
                }
            }
        }
        stage('Check Changes') {
            steps {
                script {
                    currentBuild.description = 'Check Changes'
                    def changes = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                    echo "Changed files:\n${changes}"

                    if (changes ==~ /^README(\.md)?$/) {
                        echo "Only README file has changed. Skipping build and deployment."
                        currentBuild.result = 'SUCCESS'
                        error("Skipping pipeline as only README file has changed.")
                    }
                }
            }
        }

        stage('Prepare Application Properties') {
            steps {
                script {
                    // src/main/resources 디렉토리 생성
                    sh 'mkdir -p src/main/resources/yaml'
                }

                withCredentials([file(credentialsId: 'application-yml', variable: 'APPLICATION_YML')]) {
                    sh '''
                        cp $APPLICATION_YML src/main/resources/application.yml
                    '''
                }
                withCredentials([file(credentialsId: 'application-dev-yml', variable: 'APPLICATION_DEV_YML')]) {
                    sh '''
                        cp $APPLICATION_DEV_YML src/main/resources/yaml/application-dev.yml
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    currentBuild.description = 'Build Docker Image'
                    dockerImage = docker.build("${ECR_REPO}:${DOCKER_TAG}")
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    currentBuild.description = 'Push to ECR'
                    docker.withRegistry("https://${ECR_REPO}", "${ECR_CREDENTIALS_ID}") {
                        dockerImage.push("${DOCKER_TAG}")
                        dockerImage.push("latest")
                    }
                }
            }
        }

        stage('Deploy to ECS') {
            steps {
                script {
                    currentBuild.description = 'Update ECS Service'
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_CREDENTIALS_ID}"]]) {
                        sh """
                        aws ecs update-service --cluster ${ECS_CLUSTER_NAME} --service ${ECS_SERVICE_NAME} --force-new-deployment --region ${AWS_REGION}
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            withCredentials([string(credentialsId: 'Discord-Backend-Webhook', variable: 'DISCORD')]) {
                discordSend description: """
                제목: ${currentBuild.displayName}
                결과: 성공
                실행 시간: ${currentBuild.duration / 1000}s
                """,
                link: env.BUILD_URL,
                title: "✅ ${env.JOB_NAME} : ${currentBuild.displayName} 성공",
                webhookURL: "$DISCORD"
            }
        }
        failure {
            withCredentials([string(credentialsId: 'Discord-Backend-Webhook', variable: 'DISCORD')]) {
                def failedStageName = currentBuild.description ?: 'Unknown'
                discordSend description: """
                제목: ${currentBuild.displayName}
                결과: 실패
                실패한 단계: ${failedStageName}
                실행 시간: ${currentBuild.duration / 1000}s
                """,
                link: env.BUILD_URL,
                title: "❌ ${env.JOB_NAME} : ${currentBuild.displayName} 실패",
                webhookURL: "$DISCORD"
            }
        }
        always {
            sh "rm -f src/main/resources/application.yml src/main/resources/yaml/application-dev.yml"
        }
    }
}
