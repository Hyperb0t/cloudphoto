package ru.itis.cloudphoto;

import com.beust.jcommander.JCommander;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] inputArgs) {
        Args args = new Args();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(inputArgs);
        PhotoService photoService = new S3Service();
        if(args.getCommand() == null) {
            log.error("no command provided");
            return;
        }
        switch (args.getCommand()) {
            case "download":
                photoService.downloadPhotos(args);
                break;
            case "upload":
                photoService.uploadPhotos(args);
                break;
            case "list":
                photoService.list(args);
                break;
            default:
                log.error("Unknown command");

        }
    }
}
