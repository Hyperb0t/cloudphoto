package ru.itis.cloudphoto;

public interface PhotoService {
    void uploadPhotos(Args args);

    void downloadPhotos(Args args);

    void list(Args args);
}
