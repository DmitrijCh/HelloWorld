pipeline {
    agent any

    environment {
        TOMCAT_HOME = '/opt/apache-tomcat-9.0.85'
        WAR_FILE = 'target/HelloWorld-1.0-SNAPSHOT.war'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code repository
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '6e4cb129-2e8c-4a75-aecb-727a69f23502', url: 'https://github.com/DmitrijCh/HelloWorld']])
            }
        }

        stage('Build') {
            steps {
                // Build the war file
                sh 'mvn clean package'
            }
        }

        stage('Deploy') {


            steps {
                // Stop Tomcat
                sh "${TOMCAT_HOME}/bin/shutdown.sh"

                // Remove existing war file and deployed application
                sh "rm -rf ${TOMCAT_HOME}/webapps/HelloWorld-1.0-SNAPSHOT*"

                // Copy the new war file to Tomcat webapps directory
                sh "cp ${WAR_FILE} ${TOMCAT_HOME}/webapps/myapp.war"

                // Start Tomcat
                sh "${TOMCAT_HOME}/bin/startup.sh"
            }
        }

        stage('Verify') {
            steps {
                // Wait for Tomcat to start
                sh "sleep 10"

            }
        }
    }

    post {
        always {
            // Cleanup any leftover files after deployment
            sh "rm -rf ${WAR_FILE}"
        }
    }

}
