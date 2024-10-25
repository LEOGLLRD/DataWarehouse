package com.stockchain.service;

import com.stockchain.dto.requests.CheckTokenRequest;
import com.stockchain.dto.requests.UserSignInRequest;
import com.stockchain.dto.responses.Response;
import com.stockchain.dto.responses.UserSignInResponse;
import com.stockchain.repository.UserRepo;
import com.stockchain.entity.User;
import com.stockchain.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public Response signUp(UserSignInRequest registrationRequest) {
        System.out.println("email : " + registrationRequest.getEmail());
        Response response = new Response();
        try {

            //First checking if the user is already registered
            Optional<User> u = userRepo.findBy_id(registrationRequest.getEmail());
            if (u.isPresent()) {
                response.setMessage("You are already registered");
                response.setStatusCode(400);
                return response;
            }

            User user = new User();
            user.set_id(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole("USER");
            User ourUserResult = userRepo.save(user);
            if (ourUserResult.get_id() != null) {
                response.setMessage("User Saved Successfully");
                response.setStatusCode(200);
                return response;
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return response;
        }
        return response;
    }

    public UserSignInResponse signIn(UserSignInRequest signinRequestUser) {
        UserSignInResponse response = new UserSignInResponse();

        try {
            //First checking if the user is already registered
            Optional<User> u = userRepo.findBy_id(signinRequestUser.getEmail());
            if (u.isEmpty()) {
                response.setMessage("You are not registered");
                response.setStatusCode(400);
                return response;
            }
            System.out.println(signinRequestUser);
            System.out.println(authenticationManager.toString());
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(signinRequestUser.getEmail(), signinRequestUser.getPassword());
            System.out.println("token: " + token.toString());
            authenticationManager.authenticate(token);
            var user = userRepo.findBy_id(signinRequestUser.getEmail()).orElseThrow();

            var jwt = jwtUtils.generateTokenForUser(user);
            var refreshToken = jwtUtils.generateRefreshTokenForUser(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public Response checkToken(CheckTokenRequest checkTokenRequest) {
        Response response = new Response();
        if (jwtUtils.isTokenExpired(checkTokenRequest.getToken())) {
            response.setStatusCode(400);
            response.setMessage("Token Expired");
            return response;
        }
        response.setStatusCode(200);
        response.setMessage("Token still valid");
        return response;
    }


}
