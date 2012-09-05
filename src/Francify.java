import java.io.File;
import java.util.TreeMap;

import processing.core.PApplet;
import processing.core.PFont;

public class Francify extends PApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "Francify" });
	}

	Slider s;
	boolean unpressed;
	boolean movingSlider, leftHandle, rightHandle;
	static int fontSize = 10;
	PFont myFont;
	PFont largerFont;
	int rangeMin, rangeMax;
	
	TreeMap<Integer, RaceRow> data;
	
	public void setup() {
		size(800, 600);
		frameRate(30);
		
		s = new Slider(50, 500, 700, 50);
		int[] vals = new int[38];
		for(int i = 0; i < vals.length; i++){
			vals[i] = i;
		}
//		s.setValues(new int[]{1900,1901,1902,1903,1904,1905,1906,1907,1908,1909,1910,1911,1912});
		s.setValues(vals);
		unpressed = true;
		movingSlider = false;
		leftHandle = false;
		rightHandle = false;

		myFont = createFont("BrowalliaNew", fontSize);
		largerFont = createFont("BrowalliaNew", 24);
		
		// Handle data import
		data = new TreeMap<Integer, RaceRow>();
//		String[] lines = loadStrings("data"+File.separator+"Tour_De_France_Data.csv");
//		for(int i = 1; i < lines.length; i++){
//			String[] parts = lines[i].split(",");
//			RaceRow rr = new RaceRow();
//			rr.year = Integer.parseInt(parts[0]);
//            data.put(rr.year, rr);
//		}
		//TODO remove hardcode
        RaceRow r1 = new RaceRow();
		r1.year = 1903;
		r1.distance = 2428;
	    data.put(r1.year, r1);

        RaceRow r2 = new RaceRow();
	    r2.year = 1904;
        r2.distance = 2428;
        data.put(r2.year, r2);

        RaceRow r3 = new RaceRow();
        r3.year = 1905;
        r3.distance = 2994;
        data.put(r3.year, r3);
	}

	public void draw() {
		// Handle data drawing

		background(0xcccccc);
		drawAxes();
		s.drawSlider();
		drawRange();
		handleInput();
		drawData(s.getLeftBound(), s.getRightBound());
		updateAnim();
		if (!mousePressed)
			updateCursor();
	}
	
	public void updateCursor(){
		int pos = s.whereIs(mouseX, mouseY);
		switch(pos){
		case Slider.OUTSIDE:
			cursor(ARROW);
			break;
		case Slider.INSIDE:
			cursor(MOVE);
			break;
		case Slider.LEFTSLIDER:
		case Slider.RIGHTSLIDER:
			cursor(HAND);
		}
	}
	
	public void drawRange(){
		String ranges = rangeMin + " - " + rangeMax;
		if(rangeMin == rangeMax)
			ranges = ""+rangeMin;
		int rangeWidth = (int)(textWidth(ranges) + 0.5);
		int rangeX = getWidth()/2 - rangeWidth/2;
		int rangeY = getHeight()-25 + 12;
		fill(0,0,0);
		textFont(largerFont);
		text(ranges, rangeX, rangeY);
	}
	
	public void handleInput(){
		// Handle user input
		if (mousePressed == true) {
			if (unpressed) {
				// Everything within this block occurs when first clicked
				// (pressed state toggles on)
				unpressed = false;
				int loc = s.whereIs(mouseX, mouseY);
				if (loc == Slider.INSIDE)
					movingSlider = true;
				else if (loc == Slider.LEFTSLIDER)
					leftHandle = true;
				else if (loc == Slider.RIGHTSLIDER)
					rightHandle = true;
			} else {
				// Everything in this block occurs when the mose has been
				// pressed for some period (clicking and dragging, etc.)
				if(movingSlider){
					s.dragAll(mouseX, pmouseX);
				} else if(leftHandle){
					s.dragLH(mouseX, pmouseX);
				} else if(rightHandle){
					s.dragRH(mouseX, pmouseX);
				}
				s.snapGoals();
			}
		}
	}
	
	public void updateAnim(){
		// Update animation values (simple spring animation)
	    int speed = 4;
	    s.updateAnim(speed);
	}

	public void mouseReleased() {
		unpressed = true;
		movingSlider = false;
		leftHandle = false;
		rightHandle = false;
		s.updateGoals();
	}

	public void drawAxes() {
		// Draw Axes Lines
		stroke(0);
		strokeWeight(3);
		line(50,50,50,450);
		line(50,450,750,450);

		// Draw Labels
	}

	public void drawData(float minBound, float maxBound) {
		// Set colors and draw lines. Use a thicker stroke if possible
	    int curMinBound = 1902;
	    int curMaxBound = 1906;
	    for(int i = curMinBound; i<= curMaxBound; i++){
	        RaceRow rr = data.get(i);
	        if (rr != null){
	            float x = mapToPlotX(rr.year, curMinBound, curMaxBound);
	            float y = mapToPlotY(rr.distance);
	            ellipse(x,y,5,5);
	        }
	        else{
	            System.out.println("Null data at key: " + i);    
	        }
	    }

	}
	
	public float mapToPlotY(float y){
	    int axisMin = 2400;
	    int axisMax = 3100;
	    float newY = map(y, axisMin, axisMax, 50, 450);
	    return newY;
	}
	
	public float mapToPlotX(float x, float minBound, float maxBound){
	    float newX = map(x, minBound, maxBound, 50, 750);
	    return newX;
	}

	private class Slider {
		int x, y, w, h;
		int left, right;
		int goalLeft, goalRight;
		int snappedLeft, snappedRight;

		int[] values;

		public static final int OUTSIDE = 0, INSIDE = 1, LEFTSLIDER = 2,
				RIGHTSLIDER = 3;

		public Slider(int x, int y, int w, int h) {
			this.left = this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.right = w + x;
			goalLeft = left;
			goalRight = right;
			snappedLeft = goalLeft;
			snappedRight = goalRight;
		}

		public void setValues(int[] values) {
			this.values = values;
			rangeMin = values[0];
			rangeMax = values[values.length-1];
		}

		public void drawSlider() {
			strokeWeight(1);
			
			// Draw underlying data
			fill(0,0,0);
			textFont(myFont);
			for(int i = 0; i < values.length; i++){
				int xpos = x + (i) * w / (values.length) + w / (2*values.length);
				text(values[i],xpos - (int)(textWidth(""+values[i])+0.5)/2, y+h/2 + fontSize/2);
			}

			// Draw main bar
			fill(0, 0, 0, 0);
			for (int i = 0; i < h; i++) {
				stroke(100, 100, 255, i * 127 / h);
				line(left, y + i, right, y + i);
			}
			rect(left, y, right - left, h);

			// Draw left handle
			stroke(0, 0, 0, 0);
			fill(100, 100, 255, 127);
			arc(left, y + 10, 20, 20, PI, 3 * PI / 2);
			arc(left, y + h - 10, 20, 20, PI / 2, PI);
			rect(left - 10, y + 10, 10, h - 20);

			fill(100, 100, 255);
			ellipse(left - 5, y + (h / 2) - 5, 4, 4);
			ellipse(left - 5, y + (h / 2), 4, 4);
			ellipse(left - 5, y + (h / 2) + 5, 4, 4);

			// Draw right handle
			stroke(0, 0, 0, 0);
			fill(100, 100, 255, 127);
			arc(right, y + 10, 20, 20, 3 * PI / 2, 2 * PI);
			arc(right, y + h - 10, 20, 20, 0, PI / 2);
			rect(right, y + 10, 10, h - 20);

			fill(100, 100, 255);
			ellipse(right + 5, y + (h / 2) - 5, 4, 4);
			ellipse(right + 5, y + (h / 2), 4, 4);
			ellipse(right + 5, y + (h / 2) + 5, 4, 4);
		}

		public int whereIs(int x, int y) {
			int ret = OUTSIDE;
			if (x >= left && x <= right && y > this.y && y < this.y + h) {
				ret = INSIDE;
			} else if (x > left - 10 && x < left && y > this.y
					&& y < this.y + h) {
				ret = LEFTSLIDER;
			} else if (x > right && x < right + 10 && y > this.y
					&& y < this.y + h) {
				ret = RIGHTSLIDER;
			}
			return ret;
		}
		
		public void dragAll(int nx, int px){
			goalLeft += nx-px;
			goalRight += nx-px;
			if(goalLeft < x){
				goalRight += x-goalLeft;
				goalLeft += x-goalLeft;
			}
			if(goalRight > x + w){
				goalLeft -= (goalRight-(x+w));
				goalRight -= (goalRight-(x+w));
			}
		}
		
		public void dragLH(int nx, int px){
			goalLeft += nx-px;
			if(goalLeft < x){
				goalLeft += x-goalLeft;
			} else if(goalLeft > goalRight - w/values.length){
				goalLeft = goalRight - w/values.length;
			}
		}
		
		public void dragRH(int nx, int px){
			goalRight += nx-px;
			if(goalRight > x+w){
				goalRight -= (goalRight-(x+w));
			} else if(goalLeft > goalRight - w/values.length){
				goalRight = goalLeft + w/values.length;
			}
		}
		
		public void snapGoals(){
			int leftX = goalLeft - x;
			float ratioL = leftX / (float)w;
			int index = (int)(ratioL * values.length + 0.5);
			snappedLeft = x + w * index / values.length;
			rangeMin = values[index];
			
			int rightX = goalRight - x;
			float ratioR = rightX / (float)w;
			index = (int)(ratioR * values.length + 0.5);
			snappedRight = x + w * index / values.length;
			rangeMax = values[index-1];
		}
		
		public float getLeftBound(){
			return left;
		}
		
		public float getRightBound(){
			return right;
		}
		
		public void updateGoals(){
			goalLeft = snappedLeft;
			goalRight = snappedRight;
		}
		
		public void updateAnim(int slowness){
			if(abs(snappedLeft-left) > 0){
				left += (snappedLeft - left) / slowness;
				if(abs(snappedLeft - left) == 1){
					left = snappedLeft;
				}
			}
			if(abs(snappedRight-right) > 0){
				right += (snappedRight - right) / 4;
				if(abs(snappedRight - right) == 1){
					right = snappedRight;
				}
			}
		}
	}
}
