package com.example.grpc.client.grpcclient;

import org.springframework.stereotype.Controller;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RESTController
public class PingPongEndpoint {    

	GRPCClientService grpcClientService;    
	@Autowired
    	public PingPongEndpoint(GRPCClientService grpcClientService) {
        	this.grpcClientService = grpcClientService;
    	}
	@GetMapping("/")
	public String home() {
		return "uploadForm";
	}   
	@GetMapping("/ping")
    	public String ping() {
        	return grpcClientService.ping();
    	}
    @GetMapping("/add")
	public String add() {
		return grpcClientService.add();
	}
	@PostMapping("/")
	public String fileUpload(@RequestParam("file") MultipartFile file,RedirectAttributes redirectAttributes) throws IllegalStateException, IOException {
		System.out.println("Upload Test");
		return grpcClientService.handleFileUpload(file, redirectAttributes);
	}
}
