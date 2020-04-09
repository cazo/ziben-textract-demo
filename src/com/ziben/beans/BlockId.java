package com.ziben.beans;

import java.util.List;

import com.amazonaws.services.textract.model.BoundingBox;
import com.amazonaws.services.textract.model.Point;

/**
 * Classe para conter os blocos detectados pelo Textract
 * @author claudiocardozo
 *
 */
public class BlockId {
	private String id;
	private String detectedText;
	private String type;
	private Float confidence;
	private String entityType;
	private BoundingBox bound;
	private List<Point> polygon;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDetectedText() {
		return detectedText;
	}
	public void setDetectedText(String detectedText) {
		this.detectedText = detectedText;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Float getConfidence() {
		return confidence;
	}
	public void setConfidence(Float confidence) {
		this.confidence = confidence;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public BoundingBox getBound() {
		return bound;
	}
	public void setBound(BoundingBox bound) {
		this.bound = bound;
	}
	public List<Point> getPolygon() {
		return polygon;
	}
	public void setPolygon(List<Point> polygon) {
		this.polygon = polygon;
	}
}
