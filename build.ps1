podman run `
 -v ${PWD}:/home/gradle/project `
 -v gradle-cache:/home/gradle/.gradle `
 -w /home/gradle/project `
 gradle:8.13-jdk21 gradle executableJar --no-daemon --parallel --build-cache