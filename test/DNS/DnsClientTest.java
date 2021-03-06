package DNS;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.After;


public class DnsClientTest {
	
	@After
	public void resetQueryType() {
		DnsClient.setQueryType(QueryType.A);
	} 
	
	@Test
    public void googleDNSinputTest() {
        String[] args = {"@8.8.8.8", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
        
            assertEquals(53, DnsClient.getPort());
            assertEquals("8.8.8.8", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }
	
	@Test
	public void mcgillDNSintputTest() {
		String[] args = {"@132.206.85.18", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
        
            assertEquals(53, DnsClient.getPort());
            assertEquals(QueryType.A, DnsClient.getQueryType());
            assertEquals("132.206.85.18", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
	}
	
	/*
	 * Query type tests
	 */
	@Test
	public void Atest() {
		String[] args = {"@8.8.8.8", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
        
            assertEquals(53, DnsClient.getPort());
            assertEquals(QueryType.A, DnsClient.getQueryType());
            assertEquals("8.8.8.8", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void MXtest() {
		String[] args = {"-mx", "@8.8.8.8", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
        
            assertEquals(53, DnsClient.getPort());
            assertEquals(QueryType.MX, DnsClient.getQueryType());
            assertEquals("8.8.8.8", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void NStest() {
		String[] args = {"-ns", "@8.8.8.8", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
        
            assertEquals(53, DnsClient.getPort());
            assertEquals(QueryType.NS, DnsClient.getQueryType());
            assertEquals("8.8.8.8", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
	}
	
	/*
	 * Feature tests
	 */
	
	@Test
	public void timeoutTest() {
		String[] args = {"-t","10", "@8.8.8.8", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
            
            assertEquals(10000, DnsClient.getTimeout());
            assertEquals(53, DnsClient.getPort());
            assertEquals(QueryType.A, DnsClient.getQueryType());
            assertEquals("8.8.8.8", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void retryTest() {
		String[] args = {"-r","3", "@8.8.8.8", "mcgill.ca"};
        try {
            DnsClient.parseInputArguments(args);
            
            assertEquals(3, DnsClient.getmaxRetries());
            assertEquals(53, DnsClient.getPort());
            assertEquals(QueryType.A, DnsClient.getQueryType());
            assertEquals("8.8.8.8", DnsClient.getIP());
            assertEquals("mcgill.ca", DnsClient.getName());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
	}
	
	/*
	 * Formatting error tests
	 */
	@Test
	public void argsTest(){
        try {
        	String[] args = {"mcgill.ca"};
        	
        	 DnsClient.parseInputArguments(args);
        	 fail("No exception.");
			
		} catch (Exception e) {
			String error = "Incorrect input syntax: \"Wrong number of arguments.\"";
			assertEquals(error, e.getMessage());
		}
      
	}
	
	@Test
	public void nullNameTest() {
		try {
        	String[] args = {"@8.8.8.8",null};
        	
        	 DnsClient.parseInputArguments(args);
        	 fail("No exception.");
			
		} catch (Exception e) {
			String error = "Incorrect input syntax: \"Argument was NULL.\"";
			assertEquals(error, e.getMessage());
		}
	}
	
	@Test
	public void nullServerTest() {
		try {
        	String[] args = {null,"mcgill.ca"};
        	
        	 DnsClient.parseInputArguments(args);
        	 fail("No exception.");
			
		} catch (Exception e) {
			String error = "Incorrect input syntax: \"Argument was NULL.\"";
			assertEquals(error, e.getMessage());
		}
		
	}
	
	@Test
	public void wrongIPtest1() {
		try {
        	String[] args = {"@8888","mcgill.ca"};
        	
        	 DnsClient.parseInputArguments(args);
        	 fail("No exception.");
			
		} catch (Exception e) {
			String error = "Incorrect input syntax: \"Wrong IP Address format (@a.b.c.d).\"";
			assertEquals(error, e.getMessage());
		}
	}
	
	@Test
	public void longIPtest() {
		try {
        	String[] args = {"@257.6.6.6","mcgill.ca"};
        	
        	 DnsClient.parseInputArguments(args);
        	 fail("No exception.");
			
		} catch (Exception e) {
			String error = "Incorrect input syntax: \"IP Address numbers must be between " + DnsClient.getMinIP() + " and " +  DnsClient.getMaxIP() + ".\"";
			assertEquals(error, e.getMessage());
		}
		
	}

}
