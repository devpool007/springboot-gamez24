package com.gamez24.backend.springboot_gamez24

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringbootGamez24Application

fun main(args: Array<String>) {
	runApplication<SpringbootGamez24Application>(*args)
//    SpringApplication.run(SpringbootGamez24Application::class.java, *args)

}
