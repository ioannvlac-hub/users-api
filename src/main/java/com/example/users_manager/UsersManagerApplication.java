package com.example.users_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UsersManagerApplication {

	public static void main(String[] args) {
		System.out.println("Welcome to User Manager API!");
		SpringApplication.run(UsersManagerApplication.class, args);
	}

}