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
import java.util.Arrays;
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
	private int[][] m1;
	private int[][] m2;
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
				.setA00(m1[0][0])
				.setA01(m1[0][1])
				.setA10(m1[1][0])
				.setA11(m1[1][1])
				.setB00(m2[0][0])
				.setB01(m2[0][1])
				.setB10(m2[1][0])
				.setB11(m2[1][1])
				.build());
		String resp = A.getC00() + " " + A.getC01() + "\n" + A.getC10() + " " + A.getC11() + "\n";
		print(resp);
		return resp;
	}
	
	public String multiply() {
		print("hello");
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
		MatrixReply A = stub.multiplyBlock(MatrixRequest.newBuilder()
				.setA00(m1[0][0])
				.setA01(m1[0][1])
				.setA10(m1[1][0])
				.setA11(m1[1][1])
				.setB00(m2[0][0])
				.setB01(m2[0][1])
				.setB10(m2[1][0])
				.setB11(m2[1][1])
				.build());
		String resp = A.getC00() + A.getC01() + A.getC10() + A.getC11() + "";
		print(resp);
		return resp;
	}
	
	public String handleFileUpload(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,@RequestParam("operation") String operation,@RequestParam("deadline") String deadline,RedirectAttributes redirectAttributes) throws IOException {

		
		print("U have uploaded "+ file1.getOriginalFilename());
		print("U have uploaded " + file2.getOriginalFilename());
		try{
			String matrixContent1 = new String(file1.getBytes());
			String matrixContent2 = new String(file2.getBytes());
			if(matrixContent1.length()!=0 && matrixContent2.length()!=0){
				print(matrixContent1);
				print(matrixContent2);
				String [] rowsM1 = matrixContent1.split("\n");
				String [] rowsM2 = matrixContent2.split("\n");
				if (rowsM1.length == rowsM2.length && rowcolCheck(rowsM1) && rowcolCheck(rowsM2) && isPowerOfTwo(rowsM1.length))
				{	

					int[][] EmptyMatrix = new int[rowsM1.length][rowsM1.length];
					m1 = buildMatrix(EmptyMatrix, rowsM1);
					m2 = buildMatrix(EmptyMatrix, rowsM2);
					// printTwoDimensionalArray(m1);
					// System.out.println("");
					// printTwoDimensionalArray(m2);
					print("Both matrices are the same size and are square");
					redirectAttributes.addFlashAttribute("message", "Both matrices are the same size and are square");
					print(operation);
					if(operation.equals("multiply")){
						redirectAttributes.addFlashAttribute("message",
								multiply());
					}
					else{
						redirectAttributes.addFlashAttribute("message",
								add());
					}
					return "redirect:/";
				}
				else{
					print("Please the matrices are the right size and are square");
					redirectAttributes.addFlashAttribute("message", "Please make sure the matrices are the right size and are square");
					return "redirect:/";
				}

			}
			else{
				print("You have uploaded an empty file(s)");
				redirectAttributes.addFlashAttribute("message", "You have uploaded an empty file!");
				return "redirect:/";
			}
			
		}
		catch (Exception e){
			print("Error "+ e);
			return "";
		}
		

		
	}
	// public int[][] buildMatrix(String[] rows){
	// 	int size = rows.length;
	// 	int[][] matrixEmpty = new int[size][size];
	// 	return matrixEmpty;
	// }
	public static void printTwoDimensionalArray(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				System.out.printf("%d ", a[i][j]);
			}
			System.out.println();
		}
	}
	private int[][] buildMatrix(int[][] m, String[] matrixRows) {
		int r = 0;
		int c = 0;
		for (String row : matrixRows) {
			for (String num : row.trim().split(",")) {
				m[r][c] = Integer.parseInt(num);
				c += 1;
			}
			c = 0;
			r += 1;
		}
		return m;
	}
	
	public boolean rowcolCheck(String[] rows){
		// check each row is equal to number of columns
		// loop through each row, check , split array to number of rows
		for(int i = 0; i<rows.length;i++)
		{
			String [] rowSplit = rows[i].split(",");
			if(rows.length != rowSplit.length)
			{
				return false;
			}
		}
		return true;
	}
	public void print(String content){
		System.out.println(content);
	}
	// https://www.geeksforgeeks.org/program-to-find-whether-a-given-number-is-power-of-2/
	public static boolean isPowerOfTwo(int n) {
		if (n == 0)
			return false;

		return (int) (Math.ceil((Math.log(n) / Math.log(2)))) == (int) (Math.floor(((Math.log(n) / Math.log(2)))));
	}
	
}
