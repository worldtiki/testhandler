package test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCompression() throws Exception {
        byte[] bytes = "test bla bla".getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Encoding", "gzip");
        HttpEntity<byte[]> request = new HttpEntity<>(compress(bytes), headers);

        ResponseEntity<String> responseFromVictim = this.restTemplate.postForEntity("/test", request, String.class);
        assertEquals("Expected a 200", HttpStatus.OK, responseFromVictim.getStatusCode());
    }

    public static byte[] compress(byte[] body) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                zipStream.write(body);
            }
            return byteStream.toByteArray();
        }
    }
}
