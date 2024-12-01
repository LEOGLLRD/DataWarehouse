package com.stockchain.service;

import com.google.gson.Gson;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.stockchain.dto.requests.CreateFolderRequest;
import com.stockchain.dto.requests.DepositFileRequest;
import com.stockchain.dto.requests.GetFileRequest;
import com.stockchain.dto.responses.GetFileResponse;
import com.stockchain.entity.File;
import com.stockchain.entity.Home;
import com.stockchain.repository.HomeRepo;
import com.stockchain.repository.UserRepo;
import com.stockchain.dto.responses.Response;
import com.stockchain.util.Folder;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
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
    @Autowired
    private Gson gson;


    public Response addFile(DepositFileRequest fileRequest) throws IOException {
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

    public GetFileResponse downloadFile(GetFileRequest fileRequest) throws IOException {

        GetFileResponse response = new GetFileResponse();
        try {
            //Checking all the parameters are set
            if (fileRequest.getIdUser() == null || fileRequest.getIdUser().isEmpty()
                    || fileRequest.getFileName() == null || fileRequest.getFileName().isEmpty()
                    || fileRequest.getPath() == null || fileRequest.getPath().isEmpty()) {
                response.setMessage("One or several of the parameters are empty");
                response.setStatusCode(400);
                return response;
            }
            //Getting the user Home
            String idUser = fileRequest.getIdUser();

            Optional<Home> h = homeRepo.findByUserId(idUser);
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }

            System.out.println("File name requested : " + fileRequest.getFileName());
            System.out.println("Path requested : " + fileRequest.getPath());
            //Going through the tree and finding the file
            Home hm = h.get();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(hm.getHome());
            System.out.println("JSON  : " + gson.toJson(home));
            com.stockchain.util.File file = home.getFolder().getFileAt(fileRequest.getFileName(), fileRequest.getPath());
            //Checking that the file was found
            if (file == null) {
                response.setStatusCode(400);
                response.setMessage("File not found");
                return response;
            }

            System.out.println("file Id : " + file.getId());
            ObjectId objectId = new ObjectId(file.getId());
            System.out.println("objectId : " + objectId);
            GridFSFile gridFSFile = template.findOne(new Query(new Criteria().andOperator(
                    Criteria.where("_id").is(objectId)
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

    public Response createFolder(CreateFolderRequest createFolderRequest) {
        //First checking the parameters are set
        Response response = new Response();
        try {

            if (createFolderRequest.getFolderName() == null || createFolderRequest.getFolderName().isEmpty()
                    || createFolderRequest.getPath() == null || createFolderRequest.getPath().isEmpty()
                    || createFolderRequest.getIdUser() == null || createFolderRequest.getIdUser().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }

            //Getting the home of the user
            Optional<Home> h = homeRepo.findByUserId(createFolderRequest.getIdUser());
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }

            //Creating the structure of the new home based on the old one
            Gson gson = new Gson();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(h.get().getHome());
            home.getFolder().addFolderAt(new Folder(createFolderRequest.getFolderName()), createFolderRequest.getPath());
            Home finalHome = new Home();
            finalHome.setUserId(createFolderRequest.getIdUser());
            finalHome.setHome(gson.toJson(home));
            System.out.println(finalHome.getHome());
            homeRepo.updateHome(createFolderRequest.getIdUser(), finalHome);
            response.setMessage("File added successfully");
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }


    }


}
