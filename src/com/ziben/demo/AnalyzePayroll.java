package com.ziben.demo;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.AnalyzeDocumentRequest;
import com.amazonaws.services.textract.model.AnalyzeDocumentResult;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BoundingBox;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.services.textract.model.Point;
import com.amazonaws.services.textract.model.Relationship;
import com.amazonaws.services.textract.model.S3Object;
import com.ziben.beans.BlockId;

public class AnalyzePayroll {

    BufferedImage image;

    AnalyzeDocumentResult result;

    public AnalyzePayroll(AnalyzeDocumentResult documentResult, BufferedImage bufImage) throws Exception {
        super();

        result = documentResult; // Results of text detection.
        image = bufImage; // The image containing the document.

    }

    // Draws the image and text bounding box.
    public String displayInfo() {
    	Map<String, Object> retInfo = new HashMap<String, Object>();

        // Iterate through blocks and display bounding boxes around everything.

        List<Block> blocks = result.getBlocks();
        for (Block block : blocks) {
            DisplayBlockInfo(block);
        }
        return "";
    }

 
    
    // Displays information from a block returned by text detection and text analysis
    private BlockId DisplayBlockInfo(Block block) {
    	//Map<String, Object> retInfo = new HashMap<String, Object>();
    	System.out.println("Block Id : " + block.getId());
    	// coloca o blckId na hash
    	BlockId blockid = new BlockId();
    	blockid.setId(block.getId());
    	
        if (block.getText() != null) {
            System.out.println("    Detected text: " + block.getText());
            blockid.setDetectedText(block.getText());
        }
        System.out.println("    Type: " + block.getBlockType());
        blockid.setType(block.getBlockType());
        
        if (block.getBlockType().equals("PAGE") != true) {
            System.out.println("    Confidence: " + block.getConfidence().toString());
            blockid.setConfidence(block.getConfidence());
        }
        if(block.getBlockType().equals("CELL"))
        {
            System.out.println("    Cell information:");
            System.out.println("        Column: " + block.getColumnIndex());
            System.out.println("        Row: " + block.getRowIndex());
            System.out.println("        Column span: " + block.getColumnSpan());
            System.out.println("        Row span: " + block.getRowSpan());

        }
        
        System.out.println("    Relationships");
        List<Relationship> relationships = block.getRelationships();
        if(relationships != null) {
            for (Relationship relationship : relationships) {
                System.out.println("        Type: " + relationship.getType());
                System.out.println("        IDs: " + relationship.getIds().toString());
            }
        } else {
            System.out.println("        No related Blocks");
        }

        System.out.println("    Geometry");
        BoundingBox bound = new BoundingBox();
        System.out.println("        Bounding Box: " + block.getGeometry().getBoundingBox().toString());
        bound = block.getGeometry().getBoundingBox();
        blockid.setBound(bound);
        System.out.println("        Polygon: " + block.getGeometry().getPolygon().toString());
        List<Point> polygon = block.getGeometry().getPolygon();
        blockid.setPolygon(polygon);
        
        List<String> entityTypes = block.getEntityTypes();
        System.out.println("    Entity Types");
        if(entityTypes != null) {
            for (String entityType : entityTypes) {
                System.out.println("        Entity Type: " + entityType);
                blockid.setEntityType(entityType);
            }
        } else {
            System.out.println("        No entity type");
        }
        
        if(block.getBlockType().equals("SELECTION_ELEMENT")) {
            System.out.print("    Selection element detected: ");
            if (block.getSelectionStatus().equals("SELECTED")){
                System.out.println("Selected");
            }else {
                System.out.println(" Not selected");
            }
        }
       
        if(block.getPage() != null)
            System.out.println("    Page: " + block.getPage());            
        System.out.println();
        
        return blockid;
    }

    public static void main(String arg[]) throws Exception {

        // The S3 bucket and document
        String document = "textract/contracheque.jpg";
        String bucket = "storage-textract";
		
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new EndpointConfiguration("https://s3.amazonaws.com","us-east-1"))
                //.withRegion(clientRegion)
                //.withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
                      
        // Get the document from S3
        com.amazonaws.services.s3.model.S3Object s3object = s3client.getObject(bucket, document);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        BufferedImage image = ImageIO.read(inputStream);

        // Call AnalyzeDocument 
        EndpointConfiguration endpoint = new EndpointConfiguration(
                "https://textract.us-east-1.amazonaws.com", "us-east-1");
        AmazonTextract client = AmazonTextractClientBuilder.standard()
                .withEndpointConfiguration(endpoint).build();
                
        AnalyzeDocumentRequest request = new AnalyzeDocumentRequest()
                .withFeatureTypes("TABLES","FORMS")
                .withDocument(new Document().withS3Object(new S3Object().withName(document).withBucket(bucket)));

        AnalyzeDocumentResult result = client.analyzeDocument(request);

        // Create frame and panel.
        AnalyzePayroll retAnalyze = new AnalyzePayroll(result, image);
        retAnalyze.displayInfo();

    }
}