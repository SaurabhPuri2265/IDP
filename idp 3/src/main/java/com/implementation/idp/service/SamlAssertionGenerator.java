package com.implementation.idp.service;

import com.implementation.idp.entity.User;
import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.stereotype.Service;

@Service
public class SamlAssertionGenerator {

    public String generateSAMLAssertion(User user) {
        try {
            DefaultBootstrap.bootstrap();
            // Get the XMLObject builder factory
            XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

            // Create Assertion
            SAMLObjectBuilder<Assertion> assertionBuilder = (SAMLObjectBuilder<Assertion>) builderFactory.getBuilder(Assertion.DEFAULT_ELEMENT_NAME);

            Assertion assertion = assertionBuilder.buildObject();

            // Create Issuer
            SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
            Issuer issuer = issuerBuilder.buildObject();
            issuer.setValue("http://localhost:8081/idp"); // IdP entity ID
            assertion.setIssuer(issuer);

            // Create Subject
            SAMLObjectBuilder<Subject> subjectBuilder = (SAMLObjectBuilder<Subject>) builderFactory.getBuilder(Subject.DEFAULT_ELEMENT_NAME);
            Subject subject = subjectBuilder.buildObject();
            assertion.setSubject(subject);

            // Create NameID
            SAMLObjectBuilder<NameID> nameIDBuilder = (SAMLObjectBuilder<NameID>) builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
            NameID nameID = nameIDBuilder.buildObject();
            nameID.setValue(user.getEmail());
            subject.setNameID(nameID);


            // Create AttributeStatement
            SAMLObjectBuilder<AttributeStatement> attributeStatementBuilder = (SAMLObjectBuilder<AttributeStatement>) builderFactory.getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
            AttributeStatement attributeStatement = attributeStatementBuilder.buildObject();
            assertion.getAttributeStatements().add(attributeStatement);

            // Create Attribute for Name
            SAMLObjectBuilder<Attribute> nameAttributeBuilder = (SAMLObjectBuilder<Attribute>) builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
            Attribute nameAttribute = nameAttributeBuilder.buildObject();
            nameAttribute.setName("Name");
            XSString nameAttributeValue = (XSString) builderFactory.getBuilder(XSString.TYPE_NAME).buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
            nameAttributeValue.setValue(user.getName());
            nameAttribute.getAttributeValues().add(nameAttributeValue);
            attributeStatement.getAttributes().add(nameAttribute);

            // Create Attribute for Phone Number
            SAMLObjectBuilder<Attribute> emailAttributeBuilder = (SAMLObjectBuilder<Attribute>) builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
            Attribute emailAttribute = emailAttributeBuilder.buildObject();
            emailAttribute.setName("Email");
            XSString emailAttributeBuilderValue = (XSString) builderFactory.getBuilder(XSString.TYPE_NAME).buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
            emailAttributeBuilderValue.setValue(user.getEmail());
            emailAttribute.getAttributeValues().add(emailAttributeBuilderValue);
            attributeStatement.getAttributes().add(emailAttribute);

            // Create Conditions
            SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>) builderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
            Conditions conditions = conditionsBuilder.buildObject();
            conditions.setNotBefore(DateTime.now());
            conditions.setNotOnOrAfter(DateTime.now().plusMinutes(30)); // Adjust validity period as needed
            assertion.setConditions(conditions);

            return samlObjectToString(assertion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String samlObjectToString(Assertion assertion) throws MarshallingException {
        MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
        Marshaller marshaller = marshallerFactory.getMarshaller(assertion);
        return XMLHelper.nodeToString(marshaller.marshall(assertion));
    }
}
