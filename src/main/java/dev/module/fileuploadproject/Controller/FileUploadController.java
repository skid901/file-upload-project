package dev.module.fileuploadproject.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/rest")
public class FileUploadController {
    //
    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    /*
     *  test.
     */
    @GetMapping("/hello")
    public String hello(Model model) {
        //
        return "hello.html";
    }

    /*
     *  shows uploaded list of files.
     */
    @GetMapping("/files")
    public String getFiles(HttpServletRequest request, Model model) {
        //
        return "get files";
    }

    /*
     *  uploads files origin version.
     */
    @PostMapping("/file/origin")
    public String postFilesOrigin(HttpServletRequest request,
                            @ModelAttribute("file") MultipartFile file) {
        // declare a result variable.
        String result = "";

        // get upload path and directory.
        String uploadPath = request.getServletContext().getRealPath("upload");
        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // duplicate file to server.
        String uploadFileURI = uploadDir.getAbsolutePath()
                                + File.separator
                                + file.getOriginalFilename();
        File uploadFile = new File(uploadFileURI);
        try(BufferedOutputStream stream = new BufferedOutputStream(
                                           new FileOutputStream(uploadFile))) {
           stream.write(file.getBytes());
           logger.info("upload file: " + uploadFileURI);

           // upload success.
           result = "1";
        } catch(IOException exception) {

           // upload fail.
           logger.error("file uploading failed");
        }

        return result;
    }

    /*
     *  uploads files.
     */
    @PostMapping("/file")
    public String postFiles(HttpServletRequest request,
                            @ModelAttribute("file") MultipartFile file) {
        
        // check heap memory size infomation.
        long totalHeapSize = Runtime.getRuntime().totalMemory();
        long freeHeapSize = Runtime.getRuntime().freeMemory();
        logger.info(String.format("Totla Heap Size: %.2f GB",
                                    (double)totalHeapSize/Math.pow(1024, 3)));
        logger.info(String.format("Free Heap Size:  %.2f GB",
                                    (double)freeHeapSize/Math.pow(1024, 3)));

        // declare a result variable.
        String result = "";

        // get upload path and directory.
        String uploadPath = request.getServletContext().getRealPath("upload");
        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // duplicate file to server.
        String uploadFileURI = uploadDir.getAbsolutePath()
                                + File.separator
                                + file.getOriginalFilename();
        File uploadFile = new File(uploadFileURI);

        // upload file.
        try(FileInputStream fis = (FileInputStream)file.getInputStream();
                FileOutputStream fos = new FileOutputStream(uploadFile);
                FileChannel fcIn = fis.getChannel();
                FileChannel fcOut = fos.getChannel()) {
            
            long fileSize = fcIn.size(); // get total size of file.
            long position = 0; // get uploading start position.
            long count = fcIn.size(); // get left size of file.
            while(position < fileSize) {
                
                // get left size after uploading.
                long backlog = fcIn.transferTo(position, count, fcOut);
                if(backlog > 0) {
                    
                    position += backlog;
                    count -= backlog;
                    logger.info(String.format("uploading %s: %.2f/%.2f GB",
                                                uploadFile.getName(),
                                                (double)position/Math.pow(1024, 3),
                                                (double)fileSize/Math.pow(1024, 3)));
                }
            }
            logger.info("file uploaded: " + uploadFile.getAbsolutePath());

            // upload success.
            result = "1";

        } catch (IOException e) {

           // upload fail.
           logger.error("file uploading failed: " + uploadFile.getName());
        }

        return result;
    }
}
