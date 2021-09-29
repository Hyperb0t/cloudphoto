package ru.itis.cloudphoto;

import com.beust.jcommander.Parameter;
import lombok.Data;

@Data
public class Args {

    @Parameter
    private String command;

    @Parameter(names = "-p")
    private String path;

    @Parameter(names = "-a")
    private String album;

}
