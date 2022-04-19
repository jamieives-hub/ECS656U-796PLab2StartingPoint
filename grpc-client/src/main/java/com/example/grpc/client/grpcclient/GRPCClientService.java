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
import java.util.ArrayList;
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
	private ArrayList<int[][]> m1Blocked;
	private ArrayList<int[][]> m2Blocked;

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

	public String multiply() {
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
		String resp = A.getC00() + " " + A.getC01() +"\n"+ A.getC10() + " " + A.getC11();
		print(resp);
		return resp;
	}
	
	public String add() {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		// MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
		// MatrixReply A = stub.multiplyBlock(MatrixRequest.newBuilder()
		// 		.setA00(m1[0][0])
		// 		.setA01(m1[0][1])
		// 		.setA10(m1[1][0])
		// 		.setA11(m1[1][1])
		// 		.setB00(m2[0][0])
		// 		.setB01(m2[0][1])
		// 		.setB10(m2[1][0])
		// 		.setB11(m2[1][1])
		// 		.build());
		// String resp = A.getC00() + A.getC01() + A.getC10() + A.getC11() + "";
		// print(resp);
		ArrayList<MatrixReply>rep = new ArrayList<>();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
		for(int i =0; i<m1Blocked.size();i++){
			int[][] takeBlock1 = m1Blocked.get(i);
			int[][] takeBlock2 = m2Blocked.get(i);
			MatrixReply A = stub.multiplyBlock(MatrixRequest.newBuilder()
				.setA00(takeBlock1[0][0])
				.setA01(takeBlock1[0][1])
				.setA10(takeBlock1[1][0])
				.setA11(takeBlock1[1][1])
				.setB00(takeBlock2[0][0])
				.setB01(takeBlock2[0][1])
				.setB10(takeBlock2[1][0])
				.setB11(takeBlock2[1][1])
				.build());
				rep.add(A);
				System.out.println(takeBlock1[0][0]);
				System.out.println(takeBlock1[0][1]);
				System.out.println(takeBlock1[1][0]);
				System.out.println(takeBlock1[1][1]);
				System.out.println(takeBlock2[0][0]);
				System.out.println(takeBlock2[0][1]);
				System.out.println(takeBlock2[1][0]);
				System.out.println(takeBlock2[1][1]);
		}
		
		String resp = getResponse(rep);
		return resp;
	}
	public String getResponse(ArrayList <MatrixReply> rep){
		int size = m1.length;
		int [][] matrixConverted = new int[size][size];
		int k = 0;
		for(int i = 0; i<size; i+=2){
			for(int j=0; j<size;j+=2){
				matrixConverted[i][j] = rep.get(k).getC00();
				matrixConverted[i][j+1] = rep.get(k).getC01();
				matrixConverted[i+1][j] = rep.get(k).getC10();
				matrixConverted[i+1][j+1] = rep.get(k).getC11();
				k++;
				System.out.println(rep.get(k).getC00());
				System.out.println(rep.get(k).getC01());
				System.out.println(rep.get(k).getC10());
				System.out.println(rep.get(k).getC11());
			}
		}
		String resp = "";
		for(int i = 0; i<matrixConverted.length;i++){
			for(int j=0; j<matrixConverted[i].length;j++){
				resp +=matrixConverted[i][j]+" ";
			}
			resp+="<br>";

		}
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
				if (rowsM1.length == rowsM2.length && rowColCheck(rowsM1) && rowColCheck(rowsM2) && isPowerOfTwo(rowsM1.length))
				{	

					int[][] EmptyMatrix = new int[rowsM1.length][rowsM1.length];
					m1 = buildMatrix(EmptyMatrix, rowsM1);
					m2 = buildMatrix(EmptyMatrix, rowsM2);
					print("Both matrices are the same size and are square");
					redirectAttributes.addFlashAttribute("message", "Both matrices are the same size and are square");
					print(operation);
					ArrayList<int[][]> m1Blocks = ConvertToBlocks(m1);
					ArrayList<int[][]> m2Blocks = ConvertToBlocks(m2);
					m1Blocked = m1Blocks;
					m2Blocked = m2Blocks;
					if(operation.equals("multiply")){
						print(multiply());
					}
					else{
						print(add());
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
	
	public ArrayList<int[][]> ConvertToBlocks(int[][] matrixConvert) {
		ArrayList<int[][]> converted = new ArrayList<int[][]>();
		int sizeOfBlock = 2;
		int lengthOfMatrix = matrixConvert[0].length;
		//slide by the block size
		for (int i=0; i<lengthOfMatrix; i=i+sizeOfBlock)
		{
		//loop through rows
			for (int j=0; j<lengthOfMatrix; j=j+sizeOfBlock) 
			{
				//loop through columns
				int[][] newBlock = new int[sizeOfBlock][sizeOfBlock];
				//empty block array to be filled with section of the matrixConvert array
				newBlock[0][0] = matrixConvert[i][j];
				System.out.println(newBlock[0][0]);
				newBlock[0][1] = matrixConvert[i][j+1];
				System.out.println(newBlock[0][1]);
				newBlock[1][0] = matrixConvert[i+1][j];
				System.out.println(newBlock[1][0]);
				newBlock[1][1] = matrixConvert[i+1][j+1];
				System.out.println(newBlock[1][1]);
				converted.add(newBlock);
				//add block to arraylist each iteration of the slide
			}
		}
		//returns 2D int array of blocks
		return converted;
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
	
	public boolean rowColCheck(String[] rows){
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
