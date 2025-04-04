package dev.imyisus.knote_java;

import jakarta.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Controller
@EnableConfigurationProperties(KnoteProperties.class)
public class KNoteController {

    @Autowired
    private KNoteService notesService;

    @PostConstruct
    public void init() throws InterruptedException {
        notesService.initMinio();
    }

    @GetMapping("/")
    public String index(Model model) {
        notesService.getAllNotes(model);
        return "index";
    }

    @PostMapping("/note")
    public String saveNotes(@RequestParam("image") MultipartFile file,
                            @RequestParam String description,
                            @RequestParam(required = false) String publish,
                            @RequestParam(required = false) String upload,
                            Model model) throws Exception {

        if (publish != null && publish.equals("Publish")) {
            notesService.saveNote(description, model);
            notesService.getAllNotes(model);
            return "redirect:/";
        }

        if (upload != null && upload.equals("Upload")) {
            if (file != null && file.getOriginalFilename() != null
                    && !file.getOriginalFilename().isEmpty()) {
                notesService.uploadImage(file, description, model);
            }
            notesService.getAllNotes(model);
            return "index";
        }

        // After save fetch all notes again
        return "index";
    }

    @GetMapping(value = "/img/{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getImageByName(@PathVariable String name) throws Exception {
        return notesService.getImageByName(name);
    }
}
