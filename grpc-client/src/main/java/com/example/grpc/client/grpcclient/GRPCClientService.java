package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GRPCClientService {
	private String fileName;
        private String uploadFilePath;
        private String contentType;
        private File dest;
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
	public FileUploadResponse fileUpload(@RequestParam("file") MultipartFile fileUpload){

		// String fileName = fileUpload.getOriginalFilename();
		// String matrixConv = convertMatrixtoString(fileUpload);
		// int[][] matrixNew = convertToMatrix(matrixConv);

	}
	
	// string matrix converted to 2D int array
	public static int[][] convertToMatrix(String m) {

		// split matrices row and col number from actual matrix data
		String[] data = m.split(";"); // get matrix data
		String row_col[] = data[0].split(","); // get matrix row and cl
		// Get row and col number into int var.
		int row = Integer.parseInt(row_col[0].replaceAll("[\\n\\t ]", ""));
		int col = Integer.parseInt(row_col[1].replaceAll("[\\n\\t ]", ""));

		String[] matrixData_temp = data[1].split(" "); // get the matrix data into string array

		int[][] matrix = new int[row][col];
		int temp_matrix_index = 0;

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				matrix[i][j] = Integer.parseInt(matrixData_temp[temp_matrix_index].replaceAll("[\\n\\t ]", ""));
				temp_matrix_index++;
			}
		}
		return matrix;
	}
	
	// Get matrix string from the file
	public static String convertMatrixtoString(MultipartFile fileUpload) 
	{
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileUpload));
			String s = null;
			while ((s = br.readLine()) != null) {
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
