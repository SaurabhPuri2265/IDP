package com.implementation.idp.service;

import lombok.extern.slf4j.Slf4j;
import org.opensaml.Configuration;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Serializable;
import java.io.StringReader;
import java.security.Principal;

/**
 * @author saurabhpuri on 06/10/23
 */

@Service
@Slf4j
public class PrincipleGenerator {

    public Principal convertSamlAssertionToPrinciple(String samlAssertion) {
        Principal principal = convertSamlAssertionToPrincipal(samlAssertion);
        if (principal != null) {
            // Access user information from the Principal
            String username = principal.getName();

            // Print user information
            System.out.println("Username: " + username);
        } else {
            System.err.println("Failed to parse SAML assertion.");
        }
        return principal;
    }

    private static Principal convertSamlAssertionToPrincipal(String samlAssertionXML) {
        try {
            // Initialize OpenSAML
            org.opensaml.DefaultBootstrap.bootstrap();

            // Parse the SAML assertion from the XML string
            //Element assertionElement = XMLObjectProviderRegistrySupport.getParserPool().parse(new StringReader(samlAssertionXML)).getDocumentElement();
            Element assertionElement = convertStringToElement(samlAssertionXML);

            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(assertionElement);
            Assertion samlAssertion = (Assertion) unmarshaller.unmarshall(assertionElement);

            // Extract user information from the SAML assertion
            String username = samlAssertion.getSubject().getNameID().getValue();

            // Create a Principal with the username
            return new CustomPrincipal(username);
        } catch (UnmarshallingException | ConfigurationException e) {

            return null;
        }
    }

    private static Element convertStringToElement(String xmlString) {
        try {
            // Create a DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Disable validation (optional)
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            // Create a DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML string into a Document
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            // Get the root element (SAML assertion in this case)
            Element rootElement = document.getDocumentElement();

            return rootElement;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Custom Principal class
class CustomPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 8151089040192540273L;
    private final String name;

    public CustomPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}