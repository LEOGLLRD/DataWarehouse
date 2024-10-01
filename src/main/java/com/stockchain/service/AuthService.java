package com.stockchain.service;

import com.stockchain.dto.requests.UserSignInRequest;
import com.stockchain.dto.responses.Response;
import com.stockchain.dto.responses.UserSignInResponse;
import com.stockchain.repository.UserRepo;
import com.stockchain.entity.User;
import com.stockchain.util.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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
        Response response = new Response();
        try {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole("USER");
            User ourUserResult = userRepo.save(user);
            if (ourUserResult != null && ourUserResult.getId() > 0) {
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
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequestUser.getEmail(), signinRequestUser.getPassword()));
            var user = userRepo.findByEmail(signinRequestUser.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateTokenForUser(user);
            var refreshToken = jwtUtils.generateRefreshTokenForUser(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }


}
