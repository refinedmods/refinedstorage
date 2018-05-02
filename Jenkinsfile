node {
  stage('Preparation') {
    git url: 'https://github.com/raoulvdberge/refinedstorage.git', branch: 'mc1.12'
  }
  cache(maxCacheSize: 250, caches: [
    [$class: 'ArbitraryFileCache', excludes: 'modules-2/modules-2.lock,*/plugin-resolution/**', includes: '**/*', path: '${HOME}/.gradle/caches'],
    [$class: 'ArbitraryFileCache', excludes: '', includes: '**/*', path: '${HOME}/.gradle/wrapper']
  ]) {
    stage('Build') {
       sh "./gradlew setupCIWorkspace"
       sh "./gradlew build"
    }
  }
  stage('Archive artifacts') {
    archiveArtifacts 'build/libs/*.jar'
  }
}
