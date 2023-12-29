package com.example.twitterdownloader.service.impl;

import com.example.twitterdownloader.exceptions.ConversionFailedException;
import com.example.twitterdownloader.globals.GlobalVars;
import com.example.twitterdownloader.service.ImageFormatConversionService;
import com.example.twitterdownloader.utils.FileResolver;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_RGB2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.haveImageReader;

@Service
public class ImageFormatConversionServiceImpl implements ImageFormatConversionService {
    private String[] supportedFormat = {"dib", "jpeg", "jpg", "png", "webp", "pbm", "pgm", "ppm", "pnm",
            "pfm", "tiff", "tif"};
    private String targetDir = GlobalVars.FILEPATH_CONVERTED;
    @Override
    public String imageConvert(String filename, String destFormat) {
        System.out.println("fffff" + filename);
        if(!isExtensionInSupportedFormat(destFormat)){
            System.out.println("Format is not supported!");
            return null;
        }

        String[] splitFileName = filename.split("\\.");
        if(splitFileName.length != 2){
            System.out.println("Failed to get the extension of file: " + filename);
            System.out.println("Please check the naming of the file, example: image.jpg");
            return null;
        }

        String convertedFilename = null;
        if(destFormat.equals("pbm")){
            convertedFilename = pbmImageConvert(filename, targetDir);
        }else if(destFormat.equals("pgm")){
            convertedFilename = pgmImageConvert(filename, targetDir);
        }else{
            convertedFilename = baseImageConvert(filename, targetDir, destFormat);
        }
        return FileResolver.extractFilename(convertedFilename);
    }

    private String baseImageConvert(String filename, String targetDir, String destFormat){
        String extension = filename.split("\\.")[1];
        String name = filename.split("\\.")[0];
        System.out.println(name);
        System.out.println(extension);
        System.out.println("Image extension is: ." + extension);
        System.out.println("Image target extension is: ." + destFormat);
        System.out.println("Start to convert");
        Mat img = imread(filename);

        String fileToSave = FileResolver.changeDir(name, targetDir) + "." + destFormat;
        System.out.println("Saving converted file to " + fileToSave);
        boolean isCreated = imwrite(fileToSave, img);

        if(isCreated) System.out.println("Converting complete");
        return fileToSave;
    }

    private String pgmImageConvert(String filename, String targetDir) {
        Mat img = imread(filename, IMREAD_GRAYSCALE);
        String pgmName = filename.split("\\.")[0];
        String pgmFileToSave = FileResolver.changeDir(pgmName, targetDir) + ".pgm";
        System.out.println("Converting to pgm");
        boolean isCreated = imwrite(pgmFileToSave, img);
        if(isCreated) System.out.println("Converting complete");
        return pgmFileToSave;
    }

    private String pbmImageConvert(String filename, String targetDir) {
        Mat img = imread(filename);
        Mat img_cvt = new Mat(img.rows(), img.cols(), CV_8UC1);
        cvtColor(img, img_cvt, COLOR_RGB2GRAY);
        img_cvt.convertTo(img_cvt, -1, 1.2, 20);
        for(int i=0; i<img_cvt.rows(); i++){
            BytePointer mp = img_cvt.ptr(i);
            for(long j=0; j<img_cvt.cols(); j++){
                // If a pixel is white, then leave it white, if not, turn it black
                if(mp.getUnsigned(j) > 255 / 1.5){
                    mp.putUnsigned(j, 255);
                }else{
                    mp.putUnsigned(j, 0);
                }
            }
        }

        String pbmName = filename.split("\\.")[0];
        String pbmFileToSave = FileResolver.changeDir(pbmName, targetDir) + ".pbm";
        System.out.println("Converting to pbm");
        boolean isCreated = imwrite(pbmFileToSave, img_cvt);
//        boolean isCreated = imwrite(pbmFileToSave, img_cvt);

        if(isCreated) System.out.println("Converting complete");
        return pbmFileToSave;
    }

    private boolean isExtensionInSupportedFormat(String extension){
        for(int i = 0; i<supportedFormat.length; i++){
            if(extension.equals(supportedFormat[i])) return true;
        }
        return false;
    }
}
