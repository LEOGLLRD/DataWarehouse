package com.stockchain.controller;

import com.stockchain.dto.requests.*;
import com.stockchain.dto.responses.GetFileResponse;
import com.stockchain.dto.responses.GetHomeResponse;
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
    public ResponseEntity<Response> depositFile(DepositFileRequest depositRequest) throws IOException {
        return ResponseEntity.ok(userService.addFile(depositRequest));
    }

    @PostMapping(value = "/func/createFolder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> createFolder(CreateFolderRequest createFolderRequest) throws IOException {
        return ResponseEntity.ok(userService.createFolder(createFolderRequest));
    }


    @GetMapping(value = "/func/get", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getFile(GetFileRequest getFileRequest, HttpServletRequest request) throws IOException {
        GetFileResponse getFileResponse = userService.downloadFile(getFileRequest);
        if (getFileResponse.getStatusCode() != 200) {
            return ResponseEntity.badRequest().body(getFileResponse);

        } else
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(getFileResponse.getFile().getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getFileResponse.getFile().getFilename() + "\"")
                    .body(new ByteArrayResource(getFileResponse.getFile().getFile()));
    }

    @GetMapping(value = "/func/getHome", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Response> getHome(GetHomeRequest getHomeRequest) throws IOException {
        return ResponseEntity.ok(userService.getHome(getHomeRequest));
    }

}


