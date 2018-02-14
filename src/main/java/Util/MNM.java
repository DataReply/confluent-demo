package Util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("MNM")
public class MNM {
	long pill_id;
	int camera_id;
	double score;
	int pill_x;
	int pill_y;
	int color;
	int damaged;
	
	public long getPill_id() {
		return pill_id;
	}
	public int getCamera_id() {
		return camera_id;
	}
	public double getScore() {
		return score;
	}
	public int getPill_x() {
		return pill_x;
	}
	public int getPill_y() {
		return pill_y;
	}
	public int getColor() {
		return color;
	}
	public int getDamaged() {
		return damaged;
	}
	
	public void setDamaged(int damaged) {
		this.damaged = damaged;
	}
	public void setPill_id(long pill_id) {
		this.pill_id = pill_id;
	}
	public void setCamera_id(int camera_id) {
		this.camera_id = camera_id;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public void setPill_x(int pill_x) {
		this.pill_x = pill_x;
	}
	public void setPill_y(int pill_y) {
		this.pill_y = pill_y;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	@JsonCreator
	public MNM(@JsonProperty("pill_id") long pill_id,
			@JsonProperty("camera_id") int camera_id,
			@JsonProperty("score") double score,
			@JsonProperty("pill_x") int pill_x,
			@JsonProperty("pill_y") int pill_y,
			@JsonProperty("color") int color,
			@JsonProperty("damaged") int damaged) {
		super();
		this.pill_id = pill_id;
		this.camera_id = camera_id;
		this.score = score;
		this.pill_x = pill_x;
		this.pill_y = pill_y;
		this.color = color;
		this.damaged = damaged;
	}
	
	@Override
	public String toString() {
		return "Message [pill_id=" + pill_id + ", camera_id=" + camera_id + ", score=" + score + ", pill_x=" + pill_x
				+ ", pill_y=" + pill_y + ", color=" + color + ", damaged=" + damaged + "]";
	}
	
	
}
