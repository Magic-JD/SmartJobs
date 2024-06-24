import org.junit.jupiter.api.Test;
import org.smartjobs.SmartJobs;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SmartJobs.class})
class SmartJobsIT {

    @Test
    void testLoadsService() {
        //This test verifies that the service will start up without any errors.
    }
}