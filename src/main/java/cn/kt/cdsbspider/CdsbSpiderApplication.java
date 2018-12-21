package cn.kt.cdsbspider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("cn.kt.cdsbspider.dao")
@EnableScheduling
public class CdsbSpiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdsbSpiderApplication.class, args);
	}

}

