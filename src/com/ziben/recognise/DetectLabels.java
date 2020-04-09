package com.ziben.recognise;

import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.TextDetection;

public class DetectLabels {
	public static void main(String[] args) throws Exception {

		// The S3 bucket and document
		String photo = "rekognition/vit-junina.jpeg";
		String bucket = "storage-textract";

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		DetectLabelsRequest requestLabel = new DetectLabelsRequest()
				.withImage(new Image().withS3Object(new S3Object().withName(photo).withBucket(bucket)))
				.withMaxLabels(10)
				// .withRequestCredentialsProvider(credentials)
				.withMinConfidence(75F);
		try {
			DetectLabelsResult result = rekognitionClient.detectLabels(requestLabel);
			List<Label> labels = result.getLabels();
			System.out.println("Textos Detectados para " + photo);
			for (Label label : labels) {
				System.out.println(label.getName() + ": " + label.getConfidence().toString());
			}
		} catch (AmazonRekognitionException e) {
			e.printStackTrace();
		}

	     DetectTextRequest requestText = new DetectTextRequest()
	              .withImage(new Image()
	              .withS3Object(new S3Object()
	              .withName(photo)
	              .withBucket(bucket)));
	    

	      try {
	         DetectTextResult result = rekognitionClient.detectText(requestText);
	         List<TextDetection> textDetections = result.getTextDetections();

	         System.out.println("Detected lines and words for " + photo);
	         for (TextDetection text: textDetections) {
	      
	                 System.out.println("Detected: " + text.getDetectedText());
	                 System.out.println("Confidence: " + text.getConfidence().toString());
	                 System.out.println("Id : " + text.getId());
	                 System.out.println("Parent Id: " + text.getParentId());
	                 System.out.println("Type: " + text.getType());
	                 System.out.println();
	         }
	      } catch(AmazonRekognitionException e) {
	         e.printStackTrace();
	      }

	}
}
