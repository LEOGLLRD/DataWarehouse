package com.stockchain.service;

import com.google.gson.Gson;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.stockchain.dto.requests.*;
import com.stockchain.dto.responses.*;
import com.stockchain.entity.File;
import com.stockchain.entity.Home;
import com.stockchain.repository.HomeRepo;
import com.stockchain.repository.UserRepo;
import com.stockchain.util.Folder;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private GridFsTemplate template;

    @Autowired
    private GridFsOperations operations;

    @Autowired
    private HomeRepo homeRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Gson gson;


    public Response addFile(DepositFileRequest fileRequest) {
        Response response = new Response();
        System.out.println(fileRequest.toString());
        try {
            //Checking if the parameters are all set
            if (fileRequest.getName() == null || fileRequest.getName().isEmpty() || fileRequest.getIdUser() == null || fileRequest.getIdUser().isEmpty() || fileRequest.getFile() == null || fileRequest.getPath() == null || fileRequest.getPath().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }
            String idUser = fileRequest.getIdUser();
            //Getting the home of the user
            Optional<Home> h = homeRepo.findByUserId(idUser);

            //Checking the home exists
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }


            //Preparing for inserting a new file into the fsfiles and fschunks collections
            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", fileRequest.getFile().getSize());
            metadata.put("userId", idUser);
            Object fileID = template.store(fileRequest.getFile().getInputStream(), fileRequest.getFile().getOriginalFilename(), fileRequest.getFile().getContentType(), metadata);

            //Creating the structure of the new home based on the old one
            Gson gson = new Gson();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(h.get().getHome());
            home.getFolder().addFileAt(new com.stockchain.util.File(fileRequest.getName(), fileID.toString(), fileRequest.getFile().getSize()), fileRequest.getPath());
            Home finalHome = new Home();
            finalHome.setUserId(idUser);
            finalHome.setHome(gson.toJson(home));

            //Updating the home tree
            homeRepo.updateHome(idUser, finalHome);

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
            if (fileRequest.getIdUser() == null || fileRequest.getIdUser().isEmpty() || fileRequest.getFileName() == null || fileRequest.getFileName().isEmpty() || fileRequest.getPath() == null || fileRequest.getPath().isEmpty()) {
                response.setMessage("One or several of the parameters are empty");
                response.setStatusCode(400);
                return response;
            }
            //Getting the user Home
            String idUser = fileRequest.getIdUser();

            //Checking the home exists
            Optional<Home> h = homeRepo.findByUserId(idUser);
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }

            //Going through the tree and finding the file
            Home hm = h.get();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(hm.getHome());
            com.stockchain.util.File file = home.getFolder().getFileAt(fileRequest.getFileName(), fileRequest.getPath());
            //Checking that the file was found
            if (file == null) {
                response.setStatusCode(400);
                response.setMessage("File not found");
                return response;
            }

            //Creating the ObjectID before the query
            ObjectId objectId = new ObjectId(file.getId());
            GridFSFile gridFSFile = template.findOne(new Query(new Criteria().andOperator(Criteria.where("_id").is(objectId))));

            File loadFile = new File();

            //Checking the file has been uploaded
            if (gridFSFile != null && gridFSFile.getMetadata() != null) {

                //Setting metadata informations of the file
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
        Response response = new Response();
        try {
            //First checking the parameters are set
            if (createFolderRequest.getFolderName() == null || createFolderRequest.getFolderName().isEmpty() || createFolderRequest.getPath() == null || createFolderRequest.getPath().isEmpty() || createFolderRequest.getIdUser() == null || createFolderRequest.getIdUser().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }

            //Getting the home of the user
            Optional<Home> h = homeRepo.findByUserId(createFolderRequest.getIdUser());
            //And checking if it exists
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

    public GetHomeResponse getHome(GetHomeRequest getHomeRequest) {
        GetHomeResponse response = new GetHomeResponse();
        try {
            //Checking if the mail is given
            if (getHomeRequest.getIdUser() == null || getHomeRequest.getIdUser().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }
            //Getting the user's home
            Optional<Home> h = homeRepo.findByUserId(getHomeRequest.getIdUser());

            //And checking if it exists
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }
            //Then sending the home tree as string
            Home hm = h.get();
            response.setMessage("Home successfully recovered");
            response.setHome(hm.getHome());
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }


    }

    public Response deleteFolder(DeleteFolderRequest deleteFolderRequest) {
        Response response = new Response();
        try {
            //Checking all the parameters are set
            if (deleteFolderRequest.getIdUser() == null || deleteFolderRequest.getIdUser().isEmpty() || deleteFolderRequest.getPath() == null || deleteFolderRequest.getPath().isEmpty() || deleteFolderRequest.getFolderName() == null || deleteFolderRequest.getFolderName().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }
            //Getting the user's home
            Optional<Home> h = homeRepo.findByUserId(deleteFolderRequest.getIdUser());
            //And checking if it exists
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }
            Home hm = h.get();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(hm.getHome());
            //First getting all the Files that are contained in this Folder
            List<String> contained = home.getFolder().getAllUnderContained();
            //Deleting the files
            if (!deleteFiles(contained)) {
                response.setStatusCode(400);
                
            }
            //Deleting the folder at path
            home.getFolder().removeFolderAt(deleteFolderRequest.getFolderName(), deleteFolderRequest.getPath());
            Home finalHome = new Home();
            finalHome.setHome(gson.toJson(home));
            finalHome.setUserId(deleteFolderRequest.getIdUser());
            homeRepo.updateHome(deleteFolderRequest.getIdUser(), finalHome);
            response.setMessage("Folder removed successfully");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            response.setMessage("Error while trying to remove the folder");
            return response;
        }
    }

    public Response deleteFile(DeleteFileRequest deleteFileRequest) {
        Response response = new Response();

        try {
            //Checking all the parameters are set
            if (deleteFileRequest.getIdUser() == null || deleteFileRequest.getIdUser().isEmpty() || deleteFileRequest.getPath() == null || deleteFileRequest.getPath().isEmpty() || deleteFileRequest.getFileName() == null || deleteFileRequest.getFileName().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }
            //Getting the user's home
            Optional<Home> h = homeRepo.findByUserId(deleteFileRequest.getIdUser());
            //And checking if it exists
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }
            Home hm = h.get();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(hm.getHome());
            //Deleting the folder at path
            com.stockchain.util.File file = home.getFolder().getFileAt(deleteFileRequest.getFileName(), deleteFileRequest.getPath());
            if (file == null) {
                response.setStatusCode(400);
                response.setMessage("File not found");
                return response;
            }
            //Creating the ObjectID of the file before the query
            ObjectId objectId = new ObjectId(file.getId());

            //Removing the file from the tree home
            home.getFolder().removeFileAt(deleteFileRequest.getFileName(), deleteFileRequest.getPath());
            //And setting the new tree home
            Home finalHome = new Home();
            finalHome.setHome(gson.toJson(home));
            finalHome.setUserId(deleteFileRequest.getIdUser());
            //Removing the file
            Query query = new Query(Criteria.where("_id").is(objectId));
            template.delete(query);
            // And updating the home
            homeRepo.updateHome(deleteFileRequest.getIdUser(), finalHome);
            response.setMessage("File removed successfully");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            response.setMessage("Error while trying to delete file");
            return response;
        }
    }

    public boolean deleteFiles(List<String> ids) {
        try {
            for (String id : ids) {
                //Creating the ObjectID of the file before the query
                ObjectId objectId = new ObjectId(id);
                //Removing the file
                Query query = new Query(Criteria.where("_id").is(objectId));
                template.delete(query);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Response getFilesNb(GetFilesNumberRequest getFilesNumberRequest) {
        GetFilesNumberResponse response = new GetFilesNumberResponse();
        try {
            //First checking all the parameters are set
            if (getFilesNumberRequest.getIdUser() == null || getFilesNumberRequest.getIdUser().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }
            Optional<Home> h = homeRepo.findByUserId(getFilesNumberRequest.getIdUser());
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }
            Home hm = h.get();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(hm.getHome());
            response.setMessage("Number of files recovered");
            response.setFilesNumber(countFileNb(home));
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            response.setMessage("Error while trying to get files number");
            response.setFilesNumber(0);
            return response;
        }
    }


    private int countFileNb(com.stockchain.util.Home home) {
        Folder root = home.getFolder();
        return root.getFilesContainedNumber();
    }


    public Response getFilesSize(GetFilesSizeRequest getFilesSizeRequest) {
        GetFilesSizeResponse response = new GetFilesSizeResponse();
        try {
            //Checking all the parameters are set
            if (getFilesSizeRequest.getIdUser() == null || getFilesSizeRequest.getIdUser().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("One or several of the parameters are empty");
                return response;
            }
            //Checking the home has been found
            Optional<Home> h = homeRepo.findByUserId(getFilesSizeRequest.getIdUser());
            if (h.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("You have No Home");
                return response;
            }
            //Getting the home and getting the size of the home.
            Home hm = h.get();
            com.stockchain.util.Home home = com.stockchain.util.Home.jsonToHome(hm.getHome());
            response.setFilesSize(home.getFolder().getFilesContainedSize());
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            response.setMessage("Error while trying to get files size");
            return response;
        }
    }


}
