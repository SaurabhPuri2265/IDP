package com.implementation.idp.controller;

import com.implementation.idp.entity.User;
import com.implementation.idp.service.IDPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author saurabhpuri on 05/10/23
 */

@RestController
@RequestMapping(path = "/idp")
public class Controller {

    @Autowired
    private IDPService idpService;

    @GetMapping("/test")
    public ResponseEntity<Void> testIDP(@RequestBody User user){
        String smalString = idpService.generateSAML(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/welcome")
    public String showWelcomePage(){
        return "welcome";
    }
}
