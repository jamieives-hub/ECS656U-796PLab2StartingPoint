package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;


import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class GRPCClientService {
	public String ping() {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		PingPongServiceGrpc.PingPongServiceBlockingStub stub = PingPongServiceGrpc.newBlockingStub(channel);
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
				.setPing("")
				.build());
		channel.shutdown();
		return helloResponse.getPong();
	}

	public String add() {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
		MatrixReply A = stub.addBlock(MatrixRequest.newBuilder()
				.setA00(1)
				.setA01(2)
				.setA10(5)
				.setA11(6)
				.setB00(1)
				.setB01(2)
				.setB10(5)
				.setB11(6)
				.build());
		String resp = A.getC00() + " " + A.getC01() + "<br>" + A.getC10() + " " + A.getC11() + "\n";
		return resp;
	}
	
	public void handleFileUpload(@RequestParam("file1") MultipartFile file1,@RequestParam("file2") MultipartFile file2,
			@RequestParam("operation") String operation,@RequestParam("deadline") String deadline,RedirectAttributes redirectAttributes) throws IOException {

		
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file1.getOriginalFilename() + "!");
		System.out.println("U have uploaded "+ file1.getOriginalFilename());
		System.out.println("U have uploaded " + file2.getOriginalFilename());

		String matrixContent1 = new String(file1.getBytes());
		String matrixContent2 = new String(file2.getBytes());
		// String [] matrixContentSplit = matrixContent.split("@");
		// String matrix1 = matrixContentSplit[0];
		// String matrix2 = matrixContentSplit[1];
		// print("M1: "+ matrix1);
		// print("M2: " + matrix2);
		//if array length matrix 1 is equal to array length of matrix 2
		//if array matrix 1 (split by commas) is equal to array matrix 2(split by commas)
		// String [] matrix1CommaSep = matrix1.split(",");
		// String[] matrix2CommaSep = matrix2.split(",");
		// if(matrix1CommaSep.length == matrix2CommaSep.length){
			
		// }
		String [] rows = matrixContent1.split("\n");

		
	}
	public void print(String content){
		System.out.println(content);
	};
	
}
