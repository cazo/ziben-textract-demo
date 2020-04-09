package com.ziben.recognise;

import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.ImageQuality;
import com.amazonaws.services.rekognition.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CompareFacesID {
	public static void main(String[] args) throws Exception {

		// The S3 bucket and document
		String source = "rekognition/cazo-tst-recko.jpeg";
		String target = "rekognition/cnh-claudio.png";
		//String target = "rekognition/rg-claudio.jpg";
		Float similarityThreshold = 70F;
		String bucket = "storage-textract";
		
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		CompareFacesRequest request = new CompareFacesRequest()
				.withSourceImage(new Image().withS3Object(new S3Object().withName(source).withBucket(bucket)))
				.withTargetImage(new Image().withS3Object(new S3Object().withName(target).withBucket(bucket)))
				.withSimilarityThreshold(similarityThreshold);

		// Call operation
		CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);

		// Display results
		List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
		for (CompareFacesMatch match : faceDetails) {
			ComparedFace face = match.getFace();
			BoundingBox position = face.getBoundingBox();
			ImageQuality quality = face.getQuality();
			System.out.println("Face at " + position.getLeft().toString() + " " + position.getTop() + " matches with "
					+ face.getConfidence().toString() + "% confidence," 
					+ " with image Quality (Brightness/Sharpness): " + quality.getBrightness() + " / " + quality.getSharpness());
			
			ObjectMapper objectMapper = new ObjectMapper();
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(match));

		}
		List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

		System.out.println("There was " + uncompared.size() + " face(s) that did not match");
		// TODO: explorar fotos com rotação dentro da imagem
		System.out.println("Source image rotation: " + compareFacesResult.getSourceImageOrientationCorrection());
		System.out.println("target image rotation: " + compareFacesResult.getTargetImageOrientationCorrection());
		

	}

}
