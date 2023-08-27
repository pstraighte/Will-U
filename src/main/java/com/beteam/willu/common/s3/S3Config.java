package com.beteam.willu.common.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

	private String awsAccessKey = System.getenv("AWS_ACCESS_KEY_ID");  // 시스템 환경 변수에서 액세스 키 값 가져오기
	private String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");  // 시스템 환경 변수에서 시크릿 키 값 가져오기
	private String awsRegion = System.getenv("AWS_REGION");  // 시스템 환경 변수에서 리전 값 가져오기

	//    @Value("${AWS_ACCESS_KEY_ID\n}")  // 액세스 키 값 주입 받기
	//    private String awsAccessKey;
	//
	//    @Value("${AWS_SECRET_ACCESS_KEY}")  // 시크릿 키 값 주입 받기
	//    private String awsSecretKey;
	//
	//    @Value("${AWS_REGION}")      // 리전 값 주입 받기
	//    private String awsRegion;

	@Bean
	public AmazonS3 amazonS3Client() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

		return AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))  // 액세스 키 및 시크릿 키로 자격 증명 생성
			.withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration("s3.amazonaws.com", awsRegion))  // 리전 설정
			.build();  // AmazonS3 클라이언트 생성 및 반환
	}
}
