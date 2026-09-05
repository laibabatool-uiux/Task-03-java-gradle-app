pipeline {
    agent any

    environment {
        APP_NAME    = 'task-03-app'
        APP_VERSION = '1.0.0'
        JAVA_HOME   = '/usr/lib/jvm/java-17-openjdk-amd64'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 20, unit: 'MINUTES')
    }

    stages {

        stage('1. Checkout') {
            steps {
                echo '=== Task-03: Fetching source code ==='
                checkout scm
                sh 'git log -1 --oneline'
            }
        }

        stage('2. Environment Check') {
            steps {
                sh '''
                    echo "=== Task-03: Toolchain ==="
                    java -version
                    chmod +x gradlew
                    ./gradlew --version
                    ./gradlew task03Info
                '''
            }
        }

        stage('3. Compile') {
            steps {
                echo '=== Task-03: Compiling ==='
                sh './gradlew clean compileJava --no-daemon'
            }
        }

        stage('4. Dependency Report') {
            steps {
                sh './gradlew :app:dependencies --configuration runtimeClasspath --no-daemon > dependency-report.txt || true'
                archiveArtifacts artifacts: 'dependency-report.txt', allowEmptyArchive: true
            }
        }

        stage('5. Unit Tests') {
            steps {
                echo '=== Task-03: Running tests ==='
                sh './gradlew test --no-daemon'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'app/build/test-results/test/*.xml'
                }
            }
        }

        stage('6. Code Coverage') {
            steps {
                sh './gradlew jacocoTestReport --no-daemon'
            }
        }

        stage('7. Package') {
            steps {
                echo '=== Task-03: Building distribution ==='
                sh './gradlew build distTar --no-daemon -x test'
                sh 'ls -lh app/build/distributions/'
                archiveArtifacts artifacts: 'app/build/libs/*.jar, app/build/distributions/*.tar',
                                 fingerprint: true
            }
        }

        stage('8. Deploy') {
            steps {
                echo '=== Task-03: Deploying to EC2 ==='
                sh """
                    sudo /usr/local/bin/task-03-deploy.sh \
                      \$(pwd)/app/build/distributions/${APP_NAME}-${APP_VERSION}.tar
                """
            }
        }

        stage('9. Smoke Test') {
            steps {
                sh '''
                    echo "=== Task-03: Verifying deployment ==="
                    for i in $(seq 1 10); do
                        if curl -fsS http://localhost:8080/health; then
                            echo ""; echo "Health check passed."; exit 0
                        fi
                        echo "Waiting for app... ($i/10)"; sleep 3
                    done
                    echo "Health check FAILED"; exit 1
                '''
            }
        }
    }

    post {
        success { echo "✅ Task-03 pipeline SUCCESS — build #${env.BUILD_NUMBER} is live on port 8080" }
        failure { echo "❌ Task-03 pipeline FAILED — check the stage logs above" }
        always  { echo "Task-03 pipeline finished with status: ${currentBuild.currentResult}" }
    }
}
