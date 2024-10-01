package com.stockchain.controller;

import com.stockchain.dto.requests.DepositFileRequest;
import com.stockchain.dto.requests.GetFileRequest;
import com.stockchain.dto.requests.UserSignInRequest;
import com.stockchain.dto.responses.Response;
import com.stockchain.dto.responses.UserSignInResponse;
import com.stockchain.service.AuthService;
import com.stockchain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")

public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/auth/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> signUp(UserSignInRequest userSignInRequest) {
        return ResponseEntity.ok(authService.signUp(userSignInRequest));
    }

    @PostMapping(value = "/auth/signin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserSignInResponse> signIn(UserSignInRequest userSignInRequest) {
        return ResponseEntity.ok(authService.signIn(userSignInRequest));
    }


    @PostMapping(value = "/func/deposit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> depositFile(DepositFileRequest depositRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userService.depositFile(depositRequest, request));
    }

    @GetMapping(value = "/func/get", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> getFile(GetFileRequest getFileRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userService.getFile(getFileRequest, request));
    }

}


