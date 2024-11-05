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

        stage('Prepare Application Properties') {
            steps {
                withCredentials([file(credentialsId: 'application-properties', variable: 'PROPERTIES')]) {
                    sh "cp \$PROPERTIES application.properties"
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
        always {
            sh "rm -f application.properties" // 보안을 위해 빌드 완료 후 삭제
        }
    }
}