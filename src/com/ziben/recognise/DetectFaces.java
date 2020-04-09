package com.ziben.recognise;

import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DetectFaces {

	public static void main(String[] args) throws Exception {

		// The S3 bucket and document
		//String photo = "rekognition/cazo-tst-recko.jpeg";
		String photo = "rekognition/vit-junina.jpeg";
		String bucket = "storage-textract";

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		DetectFacesRequest request = new DetectFacesRequest()
				.withImage(new Image().withS3Object(new S3Object().withName(photo).withBucket(bucket)))
				.withAttributes(Attribute.ALL);
				//.withAttributes(Attribute.DEFAULT);
		// Replace Attribute.ALL with Attribute.DEFAULT to get default values.

		try {
			DetectFacesResult result = rekognitionClient.detectFaces(request);
			List<FaceDetail> faceDetails = result.getFaceDetails();

			for (FaceDetail face : faceDetails) {
				if (request.getAttributes().contains("ALL")) {
					AgeRange ageRange = face.getAgeRange();
					System.out.println("A idade estimada está entre " + ageRange.getLow().toString()
							+ " e " + ageRange.getHigh().toString() + " anos.");
					System.out.println("Conjunto completo sde Atributos:");
				} else { // non-default attributes have null values.
					System.out.println("Conjunto default de Atributos:");
				}

				ObjectMapper objectMapper = new ObjectMapper();
				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
			}

		} catch (AmazonRekognitionException e) {
			e.printStackTrace();
		}

	}

}
