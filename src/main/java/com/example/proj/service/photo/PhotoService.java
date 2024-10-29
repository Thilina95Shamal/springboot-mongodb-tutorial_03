package com.example.proj.service.photo;

import com.example.proj.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService{
    Photo createPhoto(String originalFilename, MultipartFile image) throws IOException;

    Photo getPhotoById(String id);
}
