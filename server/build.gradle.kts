/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

plugins {
    id("java-library")
}

repositories {
}

dependencies {// https://mvnrepository.com/artifact/io.netty/netty-all
    implementation(libs.javalin)
    implementation(project(":commons"))
    implementation(libs.logback.classic)
    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    implementation(libs.mariadb.java.client)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}