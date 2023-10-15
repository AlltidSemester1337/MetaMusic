package com.demo.metamusic;

import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
class MetaMusicServiceApplicationTests {

	@MockBean
	private ArtistInformationRepository artistInformationRepository;

	@Test
	void contextLoads() {
	}

}
