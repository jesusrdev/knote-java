package dev.imyisus.knote_java;

import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class KNoteService {
    @Autowired
    private NotesRepository notesRepository;
    @Autowired
    private KnoteProperties properties;

    private Parser parser = Parser.builder().build();
    private HtmlRenderer renderer = HtmlRenderer.builder().build();

    private MinioClient minioClient;

    public void getAllNotes(Model model) {
        List<Note> notes = notesRepository.findAll();
        Collections.reverse(notes);
        model.addAttribute("notes", notes);
    }

    public void saveNote(String description, Model model) {
        if (description != null && !description.trim().isEmpty()) {
            // Translate markdown to HTML
            Node document = parser.parse(description.trim());
            String html = renderer.render(document);
            notesRepository.save(new Note(null, html));

            // After publish you need to clean up the textarea
            model.addAttribute("description", "");
        }
    }

    public void uploadImage(MultipartFile file, String description, Model model) throws Exception {
        String fileId = UUID.randomUUID().toString() + "." + file.getOriginalFilename().split("\\.")[1];
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(properties.getMinioBucket())
                        .object(fileId)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        model.addAttribute("description", description + " ![](/img/" + fileId + ")");
    }

    public byte[] getImageByName(String name) throws Exception {
        InputStream imageStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(properties.getMinioBucket())
                        .object(name)
                        .build()
        );
        return IOUtils.toByteArray(imageStream);
    }

    public void initMinio() throws InterruptedException {
        boolean success = false;
        while (!success) {
            try {
                // Initialize MinIO client
                minioClient = MinioClient.builder()
                        .endpoint("http://" + properties.getMinioHost() + ":9000")
                        .credentials(properties.getMinioAccessKey(), properties.getMinioSecretKey())
                        .build();

                // Check if the bucket already exists
                boolean isExist = minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(properties.getMinioBucket())
                                .build()
                );
                if (isExist) {
                    System.out.println("> Bucket already exists.");
                } else {
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(properties.getMinioBucket())
                                    .build()
                    );
                }
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("> MinIO Reconnect: " + properties.isMinioReconnectEnabled());
                if (properties.isMinioReconnectEnabled()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    success = true;
                }
            }
        }
        System.out.println("> MinIO initialized!");
    }
}