import java.io.File;
import java.util.Set;
import java.util.TreeMap;

import processing.core.PApplet;
import processing.core.PFont;

public class Francify extends PApplet {

	private static final long serialVersionUID = 1L;
	public static final int PART_ONE = 0, PART_TWO = 1;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "Francify" });
	}

	Slider s;
	boolean unpressed;
	boolean movingSlider, leftHandle, rightHandle;
	static int fontSize = 10;
	PFont myFont;
	PFont largerFont;
	int rangeMin, rangeMax, minSYear, maxSYear;
	int currentDisplayed;
	
	float minSpeed, maxSpeed, minDistance, maxDistance;
	
	TreeMap<Integer, RaceRow> data;
	TreeMap<String, Integer> numMedals;
	
	public void setup() {
		size(800, 600);
		frameRate(30);
		currentDisplayed = PART_ONE;
		
		unpressed = true;
		movingSlider = false;
		leftHandle = false;
		rightHandle = false;

		myFont = createFont("BrowalliaNew", fontSize);
		largerFont = createFont("BrowalliaNew", 24);
		
		// Handle data import
		data = new TreeMap<Integer, RaceRow>();
		numMedals = new TreeMap<String, Integer>();
		
		minSpeed = minDistance = Float.MAX_VALUE;
		maxSpeed = maxDistance = 0.0f;
		minSYear = Integer.MAX_VALUE;
		maxSYear = Integer.MIN_VALUE;
				
		String[] lines = loadStrings("data"+File.separator+"Tour_De_France_Data.csv");
		for(int i = 1; i < lines.length; i++){
			String[] parts = lines[i].split(",");
			RaceRow rr = new RaceRow();
			rr.year = Integer.parseInt(parts[0]);
			if(rr.year > maxSYear)
				maxSYear = rr.year;
			if(rr.year < minSYear)
				minSYear = rr.year;
			
			rr.firstPlaceRider = parts[1];
			rr.firstCountryID = Integer.parseInt(parts[2]);
			rr.firstPlaceCountry = parts[3];
			rr.firstPlaceTeam = parts[4];
			
			rr.secondPlaceRider = parts[5];
			rr.c2nd = Float.parseFloat(parts[6]);
			rr.secondCountryID = Integer.parseInt(parts[7]);
			rr.secondPlaceCountry = parts[8];
			rr.secondPlaceTeam = parts[9];

			rr.thirdPlaceRider = parts[10];
			rr.c3rd = Float.parseFloat(parts[11]);
			rr.thirdCountryID = Integer.parseInt(parts[12]);
			rr.thirdPlaceCountry = parts[13];
			rr.thirdPlaceTeam = parts[14];
			
			rr.numStages = Integer.parseInt(parts[15]);
			if (parts.length > 16 && !parts[16].equals("")){
				rr.distance = Float.parseFloat(parts[16]);
				if(rr.distance > maxDistance)
					maxDistance = rr.distance;
				if(rr.distance < minDistance)
					minDistance = rr.distance;
			}
			if (parts.length > 17 && !parts[17].equals("")){
				rr.avgSpeed = Float.parseFloat(parts[17]);
				if(rr.avgSpeed > maxSpeed)
					maxSpeed = rr.avgSpeed;
				if(rr.distance < minSpeed)
					minSpeed = rr.avgSpeed;
			}
			if (parts.length > 18)
				rr.bestTeam = parts[18];
			
			Integer medals = numMedals.get(rr.firstPlaceCountry);
			if(medals == null){
				numMedals.put(rr.firstPlaceCountry, 1);
			} else {
				numMedals.put(rr.firstPlaceCountry, medals + 1);
			}
			
			data.put(rr.year, rr);
		}
		
		s = new Slider(50, 500, 700, 50);
		int[] vals = new int[maxSYear - minSYear];
		for(int i = minSYear; i < maxSYear; i++){
			vals[i-minSYear] = i;
		}
		s.setValues(vals);
		s.setDrawInterval(10);
	}

	public void draw() {
		// Handle data drawing

		background(0xcccccc);
		drawAxes();
		s.drawSlider();
		drawRange();
		handleInput();
		drawDistanceData(s.getLeftBound(), s.getRightBound());
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
		case Slider.LEFTHANDLE:
		case Slider.RIGHTHANDLE:
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
				else if (loc == Slider.LEFTHANDLE)
					leftHandle = true;
				else if (loc == Slider.RIGHTHANDLE)
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
	
	public void toggleView(){
		// FIXME: Finish this method
		if(currentDisplayed == PART_ONE){
			s = new Slider(50,700,500,150);
			Set<Integer> keys = data.keySet();
			int min = Integer.MAX_VALUE, max = 0;
			for(int i : keys){
				if(i < min)
					min = i;
				if(i > max)
					max = i;
			}
		} else {
			s = new Slider(50,700,500,150);
		}
		currentDisplayed = (currentDisplayed + 1) % 2;
	}

	public void drawAxes() {
		// Draw Axes Lines
		stroke(0);
		strokeWeight(3);
		line(50,50,50,450);
		line(50,450,750,450);

		// Draw Labels
	}

	public void drawDistanceData(int minBound, int maxBound) {
		// Set colors and draw lines. Use a thicker stroke if possible
	    int pointSize = 5;
	    for(int i = minBound; i < maxBound; i++){
	        RaceRow rr0 = data.get(i);
	        RaceRow rr1 = data.get(i+1);
	        if ((rr0 != null) && (rr1 != null) && (rr0.distance > 0) && (rr1.distance > 0)){
	            float x0 = mapToPlotX(rr0.year, minBound, maxBound);
	            float y0 = mapToPlotY(rr0.distance);
	            float x1 = mapToPlotX(rr1.year, minBound, maxBound);
	            float y1 = mapToPlotY(rr1.distance);
	          //show data points
                fill(0xFF33B5E5);
                ellipse(x0,y0, pointSize, pointSize);
                ellipse(x1,y1, pointSize, pointSize);

	            //Show line
                stroke(0xFF002E3E);
                strokeWeight(1);
	            line(x0,y0,x1,y1);
	        }
	        else{
//	            System.out.println("Null data at key: " + i);    
	        }
	    }

	}
	
	public float mapToPlotY(float y){
	    int buffer = (int) ((maxDistance - minDistance) * 0.1);
	    float newY = map(y, minDistance - buffer, maxDistance + buffer, 450, 50);
	    return newY;
	}
	
	public float mapToPlotX(float x, float minBound, float maxBound){
	    float newX = map(x, minBound, maxBound, 50, 750);
	    return newX;
	}

	private class Slider {
		int x, y, w, h;
		float left, right;
		int goalLeft, goalRight;
		int snappedLeft, snappedRight;

		int drawInterval;
		
		int[] values;

		public static final int OUTSIDE = 0, INSIDE = 1, LEFTHANDLE = 2,
				RIGHTHANDLE = 3;

		public Slider(int x, int y, int w, int h) {
			this.left = this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.right = w + x;
			goalLeft = (int)(left + 0.5f);
			goalRight = (int)(right + 0.5f);
			snappedLeft = goalLeft;
			snappedRight = goalRight;
			drawInterval = 1;
		}
		
		public void setDrawInterval(int drawInterval){
			this.drawInterval = drawInterval;
		}

		public void setValues(int[] values) {
			this.values = values;
			rangeMin = values[0];
			rangeMax = values[values.length-1];
		}

		public void drawSlider() {
			//Fill background
//			fill(255,255,255);
//			noStroke();
//			rect(x,y,w,h);
			stroke(127,127,127);
			strokeWeight(2);
			noFill();
			strokeJoin(ROUND);
			beginShape();
			vertex(x, y+h);
			vertex(x, y);
			vertex(x+w, y);
			vertex(x+w, y+h);
			endShape();
			
			
			// Draw underlying data
			fill(0,0,0);
			strokeWeight(1);
			stroke(0);
			textFont(myFont);
			line(x, y+h, x+w, y+h);
			for (int i = 0; i < values.length; i++) {
				int xpos = x + (i) * w / (values.length) + w
						/ (2 * values.length);
				if (values[i] % drawInterval == 0) {
					text(values[i], xpos
							- (int) (textWidth("" + values[i]) + 0.5) / 2, y
							+ h + fontSize);
				}
				
				//Draw ruler ticks
				if(values[i] % 100 == 0){
					line(xpos, y+h, xpos, y+h - 15);
				} else if (values[i] % 10 == 0){
					line(xpos, y+h, xpos, y+h - 10);
				} else {
					line(xpos, y+h, xpos, y+h - 5);
				}
			}

			// Draw main bar
			fill(0, 0, 0, 0);
			for (int i = 0; i < h; i++) {
//				002E3E
				stroke(0x00, 0x2e, 0x3E, i * 127 / h);
				line(left, y + i, right, y + i);
			}
			rect(left, y, right - left, h);

			// Draw left handle
			stroke(0, 0, 0, 0);
			fill(0x00, 0x2e, 0x3E, 127);
			arc(left, y + 10, 20, 20, PI, 3 * PI / 2);
			arc(left, y + h - 10, 20, 20, PI / 2, PI);
			rect(left + 0.5f - 10, y + 10, 10, h - 20);

			fill(0x00, 0x2e, 0x3E);
			ellipse(left - 5, y + (h / 2) - 5, 4, 4);
			ellipse(left - 5, y + (h / 2), 4, 4);
			ellipse(left - 5, y + (h / 2) + 5, 4, 4);

			// Draw right handle
			stroke(0, 0, 0, 0);
			fill(0x00, 0x2e, 0x3E, 127);
			arc(right, y + 10, 20, 20, 3 * PI / 2, 2 * PI);
			arc(right, y + h - 10, 20, 20, 0, PI / 2);
			rect(right + 0.5f, y + 10, 10, h - 20);

			fill(0x00, 0x2e, 0x3E);
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
				ret = LEFTHANDLE;
			} else if (x > right && x < right + 10 && y > this.y
					&& y < this.y + h) {
				ret = RIGHTHANDLE;
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
			if(index == 0)
				snappedLeft = x;
			rangeMin = values[index];
			
			int rightX = goalRight - x;
			float ratioR = rightX / (float)w;
			index = (int)(ratioR * values.length + 0.5);
			if(index == values.length)
				snappedRight = x+w;
			snappedRight = x + w * index / values.length;
			rangeMax = values[index-1];
		}
		
		public int getLeftBound(){
			int leftX = (int)(left + 0.5) - x;
			float ratioL = leftX / (float)w;
			int index = (int)(ratioL * values.length + 0.5);
			return values[index];
		}
		
		public int getRightBound(){
			int rightX = (int)(right + 0.5) - x;
			float ratioR = rightX / (float)w;
			int index = (int)(ratioR * values.length + 0.5);
			return values[index-1];
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
