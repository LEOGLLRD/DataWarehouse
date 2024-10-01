package com.stockchain.service;

import com.stockchain.dto.requests.DepositFileRequest;
import com.stockchain.dto.requests.GetFileRequest;
import com.stockchain.dto.responses.GetFileResponse;
import com.stockchain.exception.ArgumentNotFilledException;
import com.stockchain.exception.NoFileException;
import com.stockchain.network.Client;
import com.stockchain.repository.FileRepo;
import com.stockchain.repository.UserRepo;
import com.stockchain.dto.responses.Response;
import com.stockchain.util.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    public static String tempPath = "src/temp/";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileRepo fileRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username).orElseThrow();
    }


    public Response depositFile(DepositFileRequest depositRequest, HttpServletRequest request) {
        Response response = new Response();

        try {
            if (depositRequest.getFile() == null) {
                throw new NoFileException();
            }


        } catch (NoFileException e) {
            e.printStackTrace();
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return response;
        }

        response.setStatusCode(200);
        response.setMessage("File successfully uploaded ! Files list : " + depositRequest.getFile().toString());

        return response;
    }

    public Response getFile(GetFileRequest getFileRequest, HttpServletRequest request) {
        GetFileResponse response = new GetFileResponse();

        try {
            if (getFileRequest.getFileNames() == null || getFileRequest.getFileNames().isEmpty()) {
                throw new ArgumentNotFilledException();
            }

            //Getting the user id based on the token
            Integer userId = getUserIdFromToken(request);


            //Setting the files to return
            response.setFiles(null);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved the file(s) !");


        } catch (ArgumentNotFilledException e) {
            //e.printStackTrace();
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return response;
        }


        return response;

    }

    public Integer getUserIdFromToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken = authHeader.substring(7);
        String username = jwtUtils.extractUsername(jwtToken);
        System.out.println(username);
        return userRepo.findByEmail(username).orElseThrow().getId();
    }

}
