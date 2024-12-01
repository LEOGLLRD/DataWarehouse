package com.stockchain.service;

import com.google.gson.Gson;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.stockchain.dto.requests.DepositFileRequest;
import com.stockchain.dto.requests.GetFileRequest;
import com.stockchain.dto.responses.GetFileResponse;
import com.stockchain.entity.File;
import com.stockchain.entity.Home;
import com.stockchain.repository.HomeRepo;
import com.stockchain.repository.UserRepo;
import com.stockchain.dto.responses.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {

    public static String tempPath = "src/temp/";


    @Autowired
    private GridFsTemplate template;

    @Autowired
    private GridFsOperations operations;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private HomeRepo homeRepo;

    @Autowired
    private MongoTemplate mongoTemplate;


    public Response addFile(DepositFileRequest fileRequest, HttpServletRequest request) throws IOException {
        Response response = new Response();
        try {

            //Getting the mail (id) of the user, from the token
            String idUser = fileRequest.getUserName();
            if (idUser == null) {
                response.setMessage("No IdUser");
                response.setStatusCode(400);
                return response;
            }

            //Checking if the parameters are all set
            if (fileRequest.getUserName().isEmpty() || fileRequest.getName().isEmpty() || fileRequest.getPath().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }

            //Getting the home of the user
            Optional<Home> h = homeRepo.findByUserId(idUser);

            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }


            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", fileRequest.getFile().getSize());
            metadata.put("userId", idUser);
            Object fileID = template.store(fileRequest.getFile().getInputStream(), fileRequest.getFile().getOriginalFilename(), fileRequest.getFile().getContentType(), metadata);

            System.out.println("Adding the file : " + fileRequest.getName() + " at : " + fileRequest.getPath());
            //Creating the structure of the new home based on the old one
            Gson gson = new Gson();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(h.get().getHome());
            System.out.println("Old home : " + gson.toJson(home));
            home.getFolder().addFileAt(new com.stockchain.util.File(fileRequest.getName(), fileID.toString()), fileRequest.getPath());
            System.out.println("New home : " + gson.toJson(home));
            Home finalHome = new Home();
            finalHome.setUserId(idUser);
            finalHome.setHome(gson.toJson(home));
            System.out.println(finalHome.getHome());
            homeRepo.updateHome(idUser, finalHome);
            System.out.println(fileID);
            response.setMessage("File added successfully");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }

    }

    public GetFileResponse downloadFile(GetFileRequest fileRequest, HttpServletRequest request) throws IOException {

        GetFileResponse response = new GetFileResponse();
        try {
            //Getting the mail (id) of the user, from the token
            String idUser = fileRequest.getUserName();
            if (idUser == null) {
                response.setMessage("No name specified");
                response.setStatusCode(400);
                return response;
            }

            GridFSFile gridFSFile = template.findOne(new Query(new Criteria().andOperator(
                    Criteria.where("metadata.userId").is(idUser),
                    Criteria.where("filename").is(fileRequest.getFileName())
            )));

            File loadFile = new File();

            if (gridFSFile != null && gridFSFile.getMetadata() != null) {
                loadFile.setFilename(gridFSFile.getFilename());

                loadFile.setFileType(gridFSFile.getMetadata().get("_contentType").toString());

                loadFile.setFileSize(gridFSFile.getMetadata().get("fileSize").toString());

                loadFile.setFile(IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()));

                response.setMessage("File downloaded successfully");
                response.setStatusCode(200);
                response.setFile(loadFile);
                return response;
            } else {
                response.setMessage("Bad Request");
                response.setStatusCode(400);
                return response;

            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }


    }


}
