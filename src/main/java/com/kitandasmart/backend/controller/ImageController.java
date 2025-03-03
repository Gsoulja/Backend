package com.kitandasmart.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kitandasmart.backend.dto.StoreDTO;
import com.kitandasmart.backend.services.ClaudeApiService;
import com.kitandasmart.backend.services.ImageProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@RestController
@RequestMapping("/api")
public class ImageController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    // Directory to save the uploaded images
    private static final String UPLOAD_DIR = "uploads/";
    public static final String prompt = "Extract the following information from the receipt and return it in JSON format:\n" +
            "\n" +
            "    Store name\n" +
            "    Store location\n" +
            "    For each product:\n" +
            "        Product name\n" +
            "        Price\n" +
            "        Category (Assign a relevant category to each product, such as \"Electronics,\" \"Groceries,\" \"Furniture,\" \"Clothing,\" etc.)\n" +
            "\n" +
            "Ensure the response is a valid JSON object with properly formatted fields.";

    @Autowired
    private  ImageProcessorService imageProcessor;
    @Autowired
    private  ClaudeApiService claudeApiService;



    @PostMapping("/upload")
    public ResponseEntity<String> handleImageUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Save image to temporary directory
            Path tempDir = Files.createTempDirectory("uploaded_images");
            Path filePath = tempDir.resolve(file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            // Process the image
            String extractedText = imageProcessor.processImage(filePath.toString());

           String result = claudeApiService.generateCompletion(prompt +" "+extractedText,"claude-3-7-sonnet-20250219");

            System.out.println(result.toString());

            return ResponseEntity.ok("Image processed successfully. Extracted text: " + result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing image: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploaded_images", filename); // Save in a folder named "uploaded_images"
        Files.createDirectories(path.getParent()); // Ensure the folder exists
        Files.write(path, file.getBytes()); // Save file content to the folder
        return path.toString(); // Return the path of the saved file
    }
}
