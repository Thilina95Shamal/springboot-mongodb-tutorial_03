package com.example.proj.service.photo;

import com.example.proj.model.Photo;
import com.example.proj.repository.PhotoRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PhotoServiceImpl implements PhotoService{

    private final PhotoRepository photoRepository;

    public PhotoServiceImpl(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Override
    public Photo createPhoto(String originalFilename, MultipartFile image) throws IOException {
        Photo photo = new Photo();
        photo.setTitle(originalFilename);
        photo.setPhoto(new Binary(BsonBinarySubType.BINARY,image.getBytes()));
        return photoRepository.save(photo);
    }

    @Override
    public Photo getPhotoById(String id) {
        Optional<Photo> photoById = photoRepository.findById(id);
        if(photoById.isPresent()){
            return photoById.get();
        }else{
            throw new RuntimeException("No Photo By the Id Found");
        }
    }
}
