package edu.ncsu.csc326.wolfcafe;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main class for the WolfCafe application.
 */
@SpringBootApplication
public class WolfCafeApplication {

	/**
	 * Returns the ModelMapper.
	 * @return the ModelMapper for the application.
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	/**
	 * Starts the WolfCafe application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(WolfCafeApplication.class, args);
	}

}
