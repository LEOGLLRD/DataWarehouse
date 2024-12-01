package com.stockchain.service;

import com.stockchain.dto.requests.UserSignInRequest;
import com.stockchain.dto.requests.UserSignUpRequest;
import com.stockchain.dto.responses.Response;
import com.stockchain.entity.Home;
import com.stockchain.repository.HomeRepo;
import com.stockchain.repository.UserRepo;
import com.stockchain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private HomeRepo homeRepo;

    public Response signUp(UserSignUpRequest registrationRequest) {
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
            user.setPassword((registrationRequest.getPassword()));
            user.setRole("USER");
            User ourUserResult = userRepo.save(user);
            //Checking the creation of the user is a success
            if (ourUserResult.get_id() != null) {
                //We can now create the home folder for the user
                Home home = new Home();
                home.setUserId(ourUserResult.get_id());
                home.setHome("{root : {}}");
                System.out.println("Trying to create home");
                homeRepo.save(home);
                response.setMessage("User Saved and home created Successfully");
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

    public Response signIn(UserSignInRequest signinRequestUser) {
        Response response = new Response();

        try {
            //First checking if the user is already registered
            Optional<User> u = userRepo.findBy_id(signinRequestUser.getEmail());
            if (u.isEmpty()) {
                response.setMessage("You are not registered");
                response.setStatusCode(400);
                return response;
            }

            response.setStatusCode(200);
            response.setMessage("Successfully Signed In");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }


}
