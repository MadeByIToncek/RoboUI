plugins {
    id("java-library")
}

repositories {
}

dependencies{// https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.javalin:javalin:6.3.0")
    implementation(project(":commons"))
    implementation("ch.qos.logback:logback-classic:1.5.11")
    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}