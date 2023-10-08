package com.implementation.idp.service;

import com.implementation.idp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;


/**
 * @author saurabhpuri on 05/10/23
 */

@Slf4j
@Service
public class IDPService {

    @Autowired
    @Qualifier("coreService")
    private RestTemplate restTemplate;

    @Autowired
    private PrincipleGenerator principleGenerator;

    @Autowired
    private SamlAssertionGenerator samlAssertionGenerator;

    public String generateSAML(User user){
        long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        String samlAssertion = samlAssertionGenerator.generateSAMLAssertion(user);
        String filePath = "saml_assertion.xml";


        // Write the SAML assertion XML to the file
        writeToFile(samlAssertion, filePath);

        Principal principal = principleGenerator.convertSamlAssertionToPrinciple(samlAssertion);
        String result = makeRestCallToOkta(principal);


        return result;

    }

    public String makeRestCallToOkta(Principal principal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        StringBuilder serverUrl = new StringBuilder("http://localhost:8080/");
        try {

            ResponseEntity<String> response = restTemplate.exchange(serverUrl.toString(),
                    HttpMethod.POST, new HttpEntity<>(principal, headers),
                    String.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.warn("Exception Occured : ", e);
        }
        return null;
    }
    private static void writeToFile(String content, String filePath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            log.info("Exception occured");
        }
    }

}
