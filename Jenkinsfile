pipeline {
    agent any

    environment {
        REPO = 'ktb-cpplab/cpplab-be'
        ECR_REPO = '891612581533.dkr.ecr.ap-northeast-2.amazonaws.com/cpplab/be'
        ECR_CREDENTIALS_ID = 'ecr:ap-northeast-2:AWS_CREDENTIALS'
        GITHUB_CREDENTIALS_ID = 'github_token'
        ECS_CLUSTER_NAME = 'cpplab-ecs-cluster'
        ECS_SERVICE_NAME = 'my-be-service'
        AWS_REGION = 'ap-northeast-2'
        DOCKER_TAG = "${env.BUILD_NUMBER}"  // Jenkins build number
        AWS_CREDENTIALS_ID = 'AWS_CREDENTIALS'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    currentBuild.description = 'Checkout'
                    git branch: "${env.GIT_BRANCH}", url: "https://github.com/${REPO}.git", credentialsId: "${GITHUB_CREDENTIALS_ID}"
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

        stage('Register New Task Definition') {
            steps {
                script {
                    currentBuild.description = 'Register New Task Definition'
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_CREDENTIALS_ID}"]]) {
                        // 새로운 태스크 정의 등록
                        def taskDefinition = """
                        [
                            {
                                "name": "be-container",
                                "image": "${ECR_REPO}:${DOCKER_TAG}",
                                "memory": 512,
                                "cpu": 256,
                                "essential": true,
                                "portMappings": [{"containerPort": 8080, "hostPort": 8080}],
                                "secrets": [
                                    {"name": "DB_URL", "valueFrom": "arn:aws:ssm:ap-northeast-2:891612581533:parameter/ecs/spring/DB_URL"},
                                    {"name": "DB_USERNAME", "valueFrom": "arn:aws:ssm:ap-northeast-2:891612581533:parameter/ecs/spring/DB_USERNAME"},
                                    {"name": "DB_PASSWORD", "valueFrom": "arn:aws:ssm:ap-northeast-2:891612581533:parameter/ecs/spring/DB_PASSWORD"},
                                    {"name": "JWT_SECRET", "valueFrom": "arn:aws:ssm:ap-northeast-2:891612581533:parameter/ecs/spring/JWT_SECRET"},
                                    {"name": "KAKAO_CLIENT_SECRET", "valueFrom": "arn:aws:ssm:ap-northeast-2:891612581533:parameter/ecs/spring/KAKAO_CLIENT_SECRET"},
                                    {"name": "NAVER_CLIENT_SECRET", "valueFrom": "arn:aws:ssm:ap-northeast-2:891612581533:parameter/ecs/spring/NAVER_CLIENT_SECRET"}
                                ]
                            }
                        ]
                        """

                        def newTaskDefArn = sh(
                            script: """
                            aws ecs register-task-definition \
                                --family be-task-family \
                                --container-definitions '${taskDefinition}' \
                                --network-mode bridge \
                                --requires-compatibilities EC2 \
                                --execution-role-arn arn:aws:iam::891612581533:role/ecsTaskExecutionRole \
                                --region ${AWS_REGION} \
                                --query 'taskDefinition.taskDefinitionArn' \
                                --output text
                            """,
                            returnStdout: true
                        ).trim()

                        // 새로운 태스크 정의 ARN을 환경 변수로 저장
                        env.NEW_TASK_DEF_ARN = newTaskDefArn
                    }
                }
            }
        }

        stage('Deploy to ECS') {
            steps {
                script {
                    currentBuild.description = 'Update ECS Service with New Task Definition'
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_CREDENTIALS_ID}"]]) {
                        sh """
                        aws ecs update-service \
                            --cluster ${ECS_CLUSTER_NAME} \
                            --service ${ECS_SERVICE_NAME} \
                            --task-definition ${env.NEW_TASK_DEF_ARN} \
                            --region ${AWS_REGION}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            sh "rm -f application.properties" // 빌드 후 파일 정리
        }
    }
}
