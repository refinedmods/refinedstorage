node {
  stage('Preparation') {
    checkout scm
  }
  cache(maxCacheSize: 250, caches: [
    [$class: 'ArbitraryFileCache', excludes: 'modules-2/modules-2.lock,*/plugin-resolution/**', includes: '**/*', path: '${HOME}/.gradle/caches'],
    [$class: 'ArbitraryFileCache', excludes: '', includes: '**/*', path: '${HOME}/.gradle/wrapper']
  ]) {
    stage('Cleanup') {
      sh "./gradlew clean"
    }
    stage('Build') {
      sh "./gradlew setupCIWorkspace"
      sh "./gradlew build"
    }
  }
  stage('Archive artifacts') {
    archiveArtifacts 'build/libs/*.jar'
  }
  stage('Publish artifacts') {
    sh "./gradlew publish"
  }
}
