package com.chatbot.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.chatbot.utility.constants.sourceRootPath;

@Slf4j
@Component
public class OpenAiUtility {

    /**
     * modify and copy file
     *
     * @param reArrangedCode  reArrangedCode
     * @param sourceFile      sourceFile
     * @param destinationPath destinationPath
     * @throws IOException java.io. i o exception
     */
    public static void modifyAndCopyFile(String reArrangedCode, Path sourceFile, String destinationPath) throws IOException {
        log.info("DirectoryFileParseProcessor :: modifyAndCopyFile ::: start()");
        String answer = reArrangedCode;
        Path destinationFile = getDestinationFilePath(sourceFile, destinationPath);
        Files.createDirectories(destinationFile.getParent());
        Files.write(destinationFile, answer.getBytes());
        log.info("DirectoryFileParseProcessor :: modifyAndCopyFile ::: end()");
    }

    /**
     * copy file
     *
     * @param sourceFile      sourceFile
     * @param destinationPath destinationPath
     * @throws IOException java.io. i o exception
     */
    public static void copyFile(Path sourceFile, String destinationPath) throws IOException {
        Path destinationFile = getDestinationFilePath(sourceFile, destinationPath);
        Files.createDirectories(destinationFile.getParent());
        Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * get destination file path
     *
     * @param sourceFile      sourceFile
     * @param destinationPath destinationPath
     * @return {@link Path}
     * @see Path
     */
    public static Path getDestinationFilePath(Path sourceFile, String destinationPath) {
        log.info("DirectoryFileParseProcessor :: getDestinationFilePath ::: start()");
        String relativePath = sourceFile.toString().substring(sourceRootPath.length()); // Remove "src" from the beginning
        File directory = new File(destinationPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                log.info("Directory created successfully");
            } else {
                log.info("Failed to create directory");
            }
        }
        log.info("DirectoryFileParseProcessor :: getDestinationFilePath ::: end()");
        return Paths.get(destinationPath + relativePath);

    }
}
