package com.stockchain.controller;

import com.stockchain.dto.requests.DepositFileRequest;
import com.stockchain.dto.requests.GetFileRequest;
import com.stockchain.dto.requests.UserSignInRequest;
import com.stockchain.dto.requests.UserSignUpRequest;
import com.stockchain.dto.responses.GetFileResponse;
import com.stockchain.dto.responses.Response;
import com.stockchain.service.AuthService;
import com.stockchain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")

public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/auth/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> signUp(UserSignUpRequest userSignUpRequest) {
        return ResponseEntity.ok(authService.signUp(userSignUpRequest));
    }

    @PostMapping(value = "/auth/signin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> signIn(UserSignInRequest userSignInRequest) {
        return ResponseEntity.ok(authService.signIn(userSignInRequest));
    }

    @PostMapping(value = "/func/deposit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> depositFile(DepositFileRequest depositRequest, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok(userService.addFile(depositRequest, request));
    }

    @GetMapping(value = "/func/get", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ByteArrayResource> getFile(GetFileRequest getFileRequest, HttpServletRequest request) throws IOException {
        GetFileResponse getFileResponse = userService.downloadFile(getFileRequest, request);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(getFileResponse.getFile().getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getFileResponse.getFile().getFilename() + "\"")
                .body(new ByteArrayResource(getFileResponse.getFile().getFile()));
    }

}


