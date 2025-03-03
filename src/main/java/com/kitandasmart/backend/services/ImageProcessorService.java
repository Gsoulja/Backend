package com.kitandasmart.backend.services;

import lombok.Setter;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.photo.Photo;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class ImageProcessorService {


    // Setter for imagePath to be used when image is uploaded


    @Value("${tesseract.datapath}")
    private String tesseractDataPath;

    @Value("${output.directory:processed_images}")
    private String outputDirectory;


    private static final Set<String> EXCLUDED_TERMS = new HashSet<>(Arrays.asList(
            "öffnungszeiten", "tkea.ch", "buchung", "total", "chf", "steuer",
            "mwst", "datum", "uhrzeit", "bediener", "betrag", "garantie",
            "tel.", "fax", "service"
    ));

    public String processImage(String filePath) {
        try {
            //validateInputs();
            createOutputDirectory();

            String processedImagePath = preprocessImage(filePath);
            String ocrText = performOcr(processedImagePath);
            String polishedText = polishOcrText(ocrText);

            return outputResults(ocrText, polishedText);

        } catch (Exception e) {
            System.out.println("Error processing image: "+ e);
            System.exit(1);
        }
        return null;
    }



    private void createOutputDirectory() throws Exception {
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
    }

    private String preprocessImage(String imagePath) {
        System.out.println("Preprocessing image: {}"+ imagePath);

        Mat image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
        if (image.empty()) {
            throw new IllegalStateException("Failed to load image: " + imagePath);
        }

        // Preprocessing steps...
        Mat denoised = new Mat();
        Photo.fastNlMeansDenoising(image, denoised);

        // Enhance contrast, apply threshold, etc.
        Mat contrast = new Mat();
        Core.normalize(denoised, contrast, 0, 255, Core.NORM_MINMAX);

        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(contrast, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        Mat cleaned = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Imgproc.morphologyEx(binary, cleaned, Imgproc.MORPH_CLOSE, kernel);

        String outputPath = Paths.get(outputDirectory, "processed_image.png").toString();
        Imgcodecs.imwrite(outputPath, cleaned);

        return outputPath;
    }

    private String performOcr(String processedImagePath) throws TesseractException {
        System.out.println("Performing OCR on processed image");

        Tesseract tesseract = configureTesseract();
        return tesseract.doOCR(new File(processedImagePath));
    }

    private Tesseract configureTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractDataPath);
        tesseract.setLanguage("deu");
        tesseract.setTessVariable("user_defined_dpi", "300");
        tesseract.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,- ");
        tesseract.setPageSegMode(6);
        return tesseract;
    }

    private String polishOcrText(String ocrText) {
        System.out.println("Polishing OCR text");

        return Arrays.stream(ocrText.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .filter(this::isRelevantLine)
                .map(this::cleanLine)
                .filter(line -> !line.isEmpty())
                .reduce(new StringBuilder(),
                        (sb, line) -> sb.append(line).append("\n"),
                        StringBuilder::append)
                .toString();
    }

    private boolean isRelevantLine(String line) {
        return EXCLUDED_TERMS.stream()
                .noneMatch(term -> line.toLowerCase().contains(term));
    }

    private String cleanLine(String line) {
        return line.replaceAll("[^a-zA-Z0-9.,-- ]", "").trim();
    }

    private String outputResults(String rawText, String polishedText) {
        System.out.println("Writing results to output files");

        try {
            Path rawOutputPath = Paths.get(outputDirectory, "raw_ocr_output.txt");
            Path polishedOutputPath = Paths.get(outputDirectory, "polished_ocr_output.txt");

            Files.writeString(rawOutputPath, rawText);
            Files.writeString(polishedOutputPath, polishedText);

            System.out.println("Raw OCR output written to: {}"+ rawOutputPath);
            System.out.println("Polished OCR output written to: {}"+ polishedOutputPath);

            // Also print to console
            System.out.println("\nRaw OCR Text:\n");
            System.out.println("----------------------------------------");
            System.out.println(rawText);
            System.out.println("----------------------------------------");
            System.out.println("\nPolished OCR Text:\n");
            System.out.println("----------------------------------------");
            System.out.println(polishedText);
            System.out.println("----------------------------------------");
            return polishedText;
        } catch (Exception e) {
            System.out.println("Error writing output files: "+ e);
        }
        return polishedText;
    }
}
