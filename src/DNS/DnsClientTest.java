package DNS;

import org.junit.Test;
import static org.junit.Assert.*;

public class DnsClientTest {

	@Test
    public void testInput() {
        String[] args = {"-t", "10", "-r", "2", "-mx", "@8.8.8.8", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
        
            assertEquals(10000, DnsClient.getTimeout());
            assertEquals(2, DnsClient.getmaxRetries());
            assertEquals(53, DnsClient.getPort());
            assertEquals(QueryType.MX, DnsClient.getQueryType());
            assertEquals("8.8.8.8", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

}
