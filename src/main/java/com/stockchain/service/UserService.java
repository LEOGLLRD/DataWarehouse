package com.stockchain.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.stockchain.dto.requests.DepositFileRequest;
import com.stockchain.dto.requests.GetFileRequest;
import com.stockchain.dto.responses.GetFileResponse;
import com.stockchain.entity.File;
import com.stockchain.entity.User;
import com.stockchain.exception.ArgumentNotFilledException;
import com.stockchain.exception.NoFileException;
import com.stockchain.repository.FileRepo;
import com.stockchain.repository.UserRepo;
import com.stockchain.dto.responses.Response;
import com.stockchain.util.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Meta;
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

@Service
public class UserService implements UserDetailsService {

    public static String tempPath = "src/temp/";

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private GridFsTemplate template;

    @Autowired
    private GridFsOperations operations;

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findBy_id(username).orElseThrow();
    }


    public Response addFile(MultipartFile upload, HttpServletRequest request) throws IOException {
        Response response = new Response();
        try {

            //Getting the mail (id) of the user, from the token
            String idUser = getUserEmailFromToken(request);
            if (idUser == null) {
                response.setMessage("Invalid token");
                response.setStatusCode(400);
                return response;
            }

            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", upload.getSize());
            metadata.put("userEmail", idUser);
            Object fileID = template.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType(), metadata);
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

    public GetFileResponse downloadFile(String name, HttpServletRequest request) throws IOException {

        GetFileResponse response = new GetFileResponse();
        try {
            //Getting the mail (id) of the user, from the token
            String idUser = getUserEmailFromToken(request);
            if (idUser == null) {
                response.setMessage("Invalid token");
                response.setStatusCode(400);
                return response;
            }
            System.out.println(name);
            GridFSFile gridFSFile = template.findOne(new Query(new Criteria().andOperator(
                    Criteria.where("metadata.userEmail").is(idUser),
                    Criteria.where("filename").is(name)
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

    public String getUserEmailFromToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken = authHeader.substring(7);
        String username = jwtUtils.extractUsername(jwtToken);
        System.out.println(username);
        return userRepo.findBy_id(username).orElseThrow().get_id();
    }

}
