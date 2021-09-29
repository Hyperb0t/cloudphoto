package ru.itis.cloudphoto;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class S3Service implements PhotoService {

    public S3Service() {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new ProfileCredentialsProvider("default")
                                .getCredentials()))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
        bucketName = "cloudphoto16";
    }

    public S3Service(String bucketName) {
        this.bucketName = bucketName;
    }

    private final String bucketName;
    private AmazonS3 amazonS3;

    @Override
    public void uploadPhotos(Args args) {
        if(args.getPath() == null) {
            log.error("specify path with -p");
            return;
        }
        if(args.getAlbum() == null) {
            log.error("specify album with -a");
            return;
        }
        try {
            Files.walk(Path.of(args.getPath()), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(p -> p.toFile().isFile())
                    .filter(p -> {return p.toString().toLowerCase().endsWith(".jpg") ||
                                 p.toString().toLowerCase().endsWith(".jpeg");})
                    .forEach(p -> {
                        log.info("uploading " + p);
                        amazonS3.putObject(bucketName, args.getAlbum() + "/" + p.getFileName(), p.toFile());
                        log.info("successfully uploaded " + p);
                    });
        }catch (IOException e) {
            log.error("can't access files in directory");
        }
    }

    @Override
    public void downloadPhotos(Args args) {
        if(args.getPath() == null) {
            log.error("specify path with -p");
            return;
        }
        if(args.getAlbum() == null) {
            log.error("specify album with -a");
            return;
        }
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        objectListing.setDelimiter("/");
        objectListing.setPrefix(args.getAlbum());
        objectListing.getObjectSummaries()
                .forEach(o -> downloadPhoto(o, args.getPath()));
    }

    private void downloadPhoto(S3ObjectSummary summary, String dir) {

        log.info("downloading photo " + summary.getKey());
        S3Object object = amazonS3.getObject(bucketName, summary.getKey());
        String filename = summary.getKey().substring(summary.getKey().lastIndexOf('/'));
        try(FileOutputStream fileOutputStream = new FileOutputStream(dir + filename)) {
            S3ObjectInputStream inputStream = object.getObjectContent();
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = inputStream.read(read_buf)) > 0) {
                fileOutputStream.write(read_buf, 0, read_len);
            }
        }catch (Exception e) {
            log.error("can't download file: connection error or nonexistent download path");
        }finally {
            log.info("successfully downloaded " + summary.getKey() + " and saved to " + dir + filename);
        }
    }

    @Override
    public void list(Args args) {
        if(args.getAlbum() != null) {
            listPhotosInAlbum(args.getAlbum());
        }
        else {
            listAlbums();
        }
    }

    private void listAlbums() {
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        Set<String> albums = new HashSet<>();
        objectListing.getObjectSummaries().stream()
                .forEach(s -> albums.add(s.getKey().substring(0, s.getKey().indexOf('/'))));
        albums.stream().forEach(System.out::println);
    }

    private void listPhotosInAlbum(String album) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        objectListing.getObjectSummaries().stream()
                .filter(s -> s.getKey().startsWith(album))
                .forEach(s -> System.out.println(s.getKey()));
    }
}
