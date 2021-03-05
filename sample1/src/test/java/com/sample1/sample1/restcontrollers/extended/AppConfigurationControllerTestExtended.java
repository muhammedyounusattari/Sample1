package com.sample1.sample1.restcontrollers.extended;

import com.sample1.sample1.restcontrollers.core.AppConfigurationControllerTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
public class AppConfigurationControllerTestExtended extends AppConfigurationControllerTest {
    //Add your custom code here
}