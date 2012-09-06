import java.io.File;
import java.util.Set;
import java.util.TreeMap;
import controlP5.*;

import processing.core.PApplet;
import processing.core.PFont;

public class Francify extends PApplet {

	private static final long serialVersionUID = 1L;
	public static final int PART_ONE = 0, PART_TWO = 1;
	public static final boolean DRAW_DISTANCE = true;
	public static final boolean DRAW_SPEED= false;

	public ControlP5 cp5;
	public CheckBox checkbox;
	public boolean enableDistance = true;
	public boolean enableSpeed = true;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "Francify" });
	}

	//Skinning Color Variables
	int backgroundColor = 0xFFCCCCCC;
	int darkColor = 0xFF002E3E;
	int dataColor0 = 0xFF4499bb;
	int dataColor1 = 0xFF88c23c;
	int detailsOnDemandColor = 0xAAE7E7E7;
	
	Slider s;
	boolean unpressed;
	boolean movingSlider, leftHandle, rightHandle;
	static int fontSize = 10;
	PFont myFont;
	PFont largerFont;
	int rangeMin, rangeMax, minSYear, maxSYear;
	int graphX, graphY, graphW, graphH;
	int currentDisplayed;
	static float detailMaxDistance = 25.0f;
	float detailsDistance;
	String sliderLabel, title;
	
	float minSpeed, maxSpeed, minDistance, maxDistance;
	
	TreeMap<Integer, RaceRow> data;
	TreeMap<String, Integer> numMedals;
	
	public void setup() {
	    smooth();
	    size(1000, 600);
		graphX = 100;
		graphY = 50;
		graphW = width - 200;
		graphH = height - 200;
		
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
				if(rr.avgSpeed < minSpeed)
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
		
		int[] vals = new int[maxSYear - minSYear];
		for(int i = minSYear; i < maxSYear; i++){
			vals[i-minSYear] = i;
		}
		s = new Slider(graphX, graphY + graphH + 50, graphW, 50, vals);
		s.setDrawInterval(10);
		sliderLabel = "Years";
		title = "Tour de France, 1903 - 2009";

		//checkboxes
		cp5 = new ControlP5(this);
		checkbox = cp5.addCheckBox("checkBox")
		        .setPosition(graphW, graphY/2)
		        .setColorForeground(dataColor0)
		        .setColorBackground(backgroundColor)
		        .setColorActive(dataColor0)
		        .setColorLabel(darkColor)
		        .setSize(20, 20)
		        .setItemsPerRow(2)
		        .setSpacingColumn(45)
		        .setSpacingRow(20)
		        .addItem("Distance", 1)
		        .addItem("Average Speed", 1)
		        ;
		   checkbox.toggle("Distance");
		   checkbox.toggle("Average Speed");
	}

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isFrom(checkbox)) {
            int distanceChecked = (int) checkbox.getArrayValue()[0];
            if (distanceChecked == 1) {
                enableDistance = true;
            }
            else{
                enableDistance = false;
            }
            
            int speedChecked = (int) checkbox.getArrayValue()[1];
            if (speedChecked == 1) {
                enableSpeed= true;
            }
            else{
                enableSpeed = false;
            }
        }
    }
	
	public void draw() {
		// Handle data drawing

		background(backgroundColor);
		drawAxes();
		s.drawSlider();
		handleInput();
        detailsDistance = Float.MAX_VALUE;
        RaceRow detailRow = null;
        if (enableDistance) {
            detailRow = drawData(DRAW_DISTANCE, s.getLeftBound(),
                    s.getRightBound());
        }
        if (enableSpeed) {
            RaceRow speedRow = drawData(DRAW_SPEED, s.getLeftBound(),s.getRightBound());
            if (speedRow != null) {
                detailRow = speedRow;
            }
        }
		if (detailRow != null) {
			detailsOnDemand(detailRow);
		}
		updateAnim();
		if (!mousePressed)
			updateCursor();
	}
	
	public void detailsOnDemand(RaceRow row){
		if(mouseX < graphX + graphW/2){ // Mouse on left half of graph
			detailsOnDemand(row, graphX + graphW/2 + 10, graphY + 10, graphW/2-20, graphH-20, 20);
		} else {
			detailsOnDemand(row, graphX + 10, graphY + 10, graphW/2-20, graphH-20, 20);
		}
	}
	
	public void detailsOnDemand(RaceRow row, int x, int y, int w, int h, int r){
		int textSize = 18;
		fill(detailsOnDemandColor);
		noStroke();
		//Draw rounded rectangle
		rect(x+r,y+r,w-2*r,h-2*r);
		rect(x,y+r,r,h-2*r);
		rect(x+r,y,w-2*r,r);
		rect(x+w-r,y+r,r,h-2*r);
		rect(x+r,y+h-r,w-2*r,r);
		
		int d = 2*r;
		stroke(darkColor);
		arc(x+r,y+r,d,d,PI,3*PI/2);
		arc(x+w-r,y+r,d,d,3*PI/2, 2*PI);
		arc(x+w-r,y+h-r,d,d,0,PI/2);
		arc(x+r,y+h-r,d,d,PI/2,PI);
		line(x,y+r,x,y+h-r);
		line(x+r,y,x+w-r,y);
		line(x+r,y+h,x+w-r,y+h);
		line(x+w,y+r,x+w,y+h-r);
		
		fill(darkColor);
		textFont(largerFont);
		textSize(textSize);
		textAlign(LEFT);
		int ty = y + r/2 + textSize;
		int tx = x + r;
		text("Year: " + row.year, tx, ty);
		ty += textSize;
		text("Average Speed: " + row.avgSpeed + " mph", tx, ty);
		ty += textSize;
		text("Distance: " + row.distance + " km", tx, ty);
		ty += textSize;
		text("# Stages: " + row.numStages, tx, ty);
		ty += textSize*2;
		
		text("Winner: ", tx, ty);
		tx += r;
		ty += textSize;
		text("Rider: " + row.firstPlaceRider, tx, ty);
		ty += textSize;
		text("Country: " + row.firstPlaceCountry, tx, ty);
		ty += textSize;
		text("Team: " + row.firstPlaceTeam, tx, ty);
		ty += textSize*2;
		tx -= r;
		
		text("Second Place: ", tx, ty);
		tx += r;
		ty += textSize;
		text("Rider: " + row.secondPlaceRider, tx, ty);
		ty += textSize;
		text("Country: " + row.secondPlaceCountry, tx, ty);
		ty += textSize;
		text("Team: " + row.secondPlaceTeam, tx, ty);
		ty += textSize*2;
		tx -= r;

		text("Third Place: ", tx, ty);
		tx += r;
		ty += textSize;
		text("Rider: " + row.thirdPlaceRider, tx, ty);
		ty += textSize;
		text("Country: " + row.thirdPlaceCountry, tx, ty);
		ty += textSize;
		text("Team: " + row.thirdPlaceTeam, tx, ty);
		ty += textSize*2;
		tx -= r;
		
		// Hilight the selected data points
		noFill();
		stroke(darkColor);
		strokeWeight(3);
		float year = mapToPlotX(row.year, s.getLeftBound(), s.getRightBound(), graphX, graphW);     
        float distY = mapToPlotY(row.distance, minDistance, maxDistance,
                    graphY, graphH);
        float speedY = mapToPlotY(row.avgSpeed, minSpeed, maxSpeed,
                    graphY, graphH);
        ellipse(year,distY,8,8);
        ellipse(year,speedY,8,8);
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
		fill(darkColor);
		textFont(largerFont);
		if(rangeMin == rangeMax){
			String range = ""+rangeMin;
			int rangeY = graphY + graphH + 25;
			textAlign(CENTER);
			text(range, getWidth()/2, rangeY);
		} else {
			String range = ""+rangeMin;
			int rangeY = graphY + graphH + 25;
			textAlign(LEFT);
			text(range, graphX, rangeY);
			
			range = ""+rangeMax;
			rangeY = graphY + graphH + 25;
			textAlign(RIGHT);
			text(range, graphX + graphW, rangeY);
		}
		textAlign(CENTER);
		text(sliderLabel, getWidth()/2, 590);
		text(title, getWidth()/2, 25);
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
			s = new Slider(50,700,500,150, null);
			Set<Integer> keys = data.keySet();
			int min = Integer.MAX_VALUE, max = 0;
			for(int i : keys){
				if(i < min)
					min = i;
				if(i > max)
					max = i;
			}
		} else {
			s = new Slider(50,700,500,150, null);
		}
		currentDisplayed = (currentDisplayed + 1) % 2;
	}

	public void drawAxes() {
		// Draw Axes Lines
		stroke(darkColor);
		strokeWeight(3);
		strokeJoin(BEVEL);
		strokeCap(SQUARE);
		noFill();
		beginShape();
		vertex(graphX, graphY);
		vertex(graphX, graphY + graphH);
		vertex(graphX + graphW, graphY + graphH);
		endShape();
		
		// Draw Labels
		drawRange();
		
		textFont(largerFont);
		textAlign(CENTER);
		fill(rgba(dataColor0, 0.75f));
		pushMatrix();
		translate(40,graphY+graphH/2);
		rotate(-PI/2);
		text("Distance (km)", 0, 0);
		fill(rgba(dataColor1, 0.75f));
		text("Average Speed (mph)", 0, 40);
		popMatrix();
		
		textAlign(RIGHT);
		textFont(myFont);
		textSize(18);
		pushMatrix();
		translate(graphX - 5,graphY + 18);
		fill(rgba(dataColor0, 0.75f));
		text(""+maxDistance, 0, 0);
		fill(rgba(dataColor1, 0.75f));
		text(""+maxSpeed, 0, 18);
		popMatrix();
		
		pushMatrix();
		translate(graphX - 5,graphY + graphH - 20);
		fill(rgba(dataColor0, 0.75f));
		text(""+maxDistance, 0, 0);
		fill(rgba(dataColor1, 0.75f));
		text(""+maxSpeed, 0, 18);
		popMatrix();
		textAlign(CENTER);
	}

    public RaceRow drawData(boolean distanceOrSpeed, int minBound, int maxBound,
            int strokeWidth, float graphX, float graphY, float graphWidth,
            float graphHeight) {
        // Set colors and draw lines.
        noFill();
        beginShape();
        strokeWeight(strokeWidth);

        RaceRow toReturn = null;
        
        //Get and draw data
        float y = 0, lastX = 0; 
        // Weather it is actively drawing or not. Prevents drawing all
        // points in gaps of data
        boolean activeDraw = true;
        for(int i = minBound; i <= maxBound; i++){
            RaceRow rr = data.get(i);
            if ((rr != null) && (rr.distance > 0)){
                float year = mapToPlotX(rr.year, minBound, maxBound, graphX, graphWidth);
                if (distanceOrSpeed == DRAW_DISTANCE){
                    y = mapToPlotY(rr.distance, minDistance, maxDistance,
                            graphY, graphHeight);
                    stroke(rgba(dataColor0, 0x88));
                }
                else { //(distanceOrSpeed == DRAW_SPEED)
                    y = mapToPlotY(rr.avgSpeed, minSpeed, maxSpeed,
                            graphY, graphHeight);
                    stroke(rgba(dataColor1, 0x88));
                }
                if(!activeDraw){
                    activeDraw = true;
                    curveVertex(year, y);
                }

                if(i == minBound || i == maxBound){
                    curveVertex(year, y);
                }
                curveVertex(year, y);
                
                float dist = dist(mouseX, mouseY, year, y);
                if(dist < detailsDistance && dist < detailMaxDistance){
                	toReturn = rr;
                	detailsDistance = dist;
                }
                
                lastX = year;
            }
            else{
                if(activeDraw){
                    activeDraw = false;
                    curveVertex(lastX, y);
                }
                endShape();
                beginShape();
            }
        }
        endShape();
        
        return toReturn;
    }

    public RaceRow drawData(boolean distanceOrSpeed, int minBound, int maxBound) {
        return drawData(distanceOrSpeed, minBound, maxBound, 3, graphX, graphY,
                graphW, graphH);
    }
	
    public float mapToPlotY(float y, float min, float max, float graphY,
            float graphHeight) {
        // Maps actual values to locations we want to draw
        //Uses 10% buffer to make data more readable
        int buffer = (int) ((max - min) * 0.1);
        float newY = map(
                y,
                min - buffer,
                max + buffer,
                graphY + graphHeight,
                graphY
        );
        return newY;
    }
	
    public float mapToPlotX(float x, float minBound, float maxBound,
            float graphX, float graphWidth) {
        float newX = map(x, minBound, maxBound, graphX, graphX + graphWidth);
    return newX;
	}

	public int rgba(int rgb, int a){
		return rgb & ((a << 24) | 0xFFFFFF);
	}
	
	public int rgba(int rgb, float a){
		if(a < 0)
			a = 0;
		if(a > 255)
			a = 255;
		return rgba(rgb, (int)(a * 255));
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

		public Slider(int x, int y, int w, int h, int[] values) {
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
			this.values = values;
			rangeMin = values[0];
			rangeMax = values[values.length-1];
		}
		
		public void setDrawInterval(int drawInterval){
			this.drawInterval = drawInterval;
		}

		public void drawSlider() {
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

			drawData(DRAW_DISTANCE, values[0], values[values.length -1], 1, x, y, w, h);
			drawData(DRAW_SPEED, values[0], values[values.length -1], 1, x, y, w, h);
			
			// Draw underlying data
			fill(0,0,0);
			strokeWeight(1);
			stroke(0);
			textFont(myFont);
			line(x, y+h, x+w, y+h);
			for (int i = 0; i < values.length; i++) {
				int xpos = x + (i) * w / (values.length) + w
						/ (2 * values.length);
				if (values[i] % drawInterval == 0 || i == 0 || i == values.length-1) {
					textAlign(CENTER);
					text(values[i], xpos, y + h + fontSize);
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
			
			//Draw mini graph

			// Draw main bar
			fill(0, 0, 0, 0);
			for (int i = 0; i < h; i++) {
				stroke(rgba(darkColor, i * 127 / h));
				line(left, y + i, right, y + i);
			}
			rect(left, y, right - left, h);

			// Draw left handle
			stroke(0, 0, 0, 0);
			fill(rgba(darkColor, 127));
			arc(left, y + 10, 20, 20, PI, 3 * PI / 2);
			arc(left, y + h - 10, 20, 20, PI / 2, PI);
			rect(left + 0.5f - 10, y + 10, 10, h - 20);

			fill(darkColor);
			ellipse(left - 5, y + (h / 2) - 5, 4, 4);
			ellipse(left - 5, y + (h / 2), 4, 4);
			ellipse(left - 5, y + (h / 2) + 5, 4, 4);

			// Draw right handle
			stroke(0, 0, 0, 0);
			fill(rgba(darkColor, 127));
			arc(right, y + 10, 20, 20, 3 * PI / 2, 2 * PI);
			arc(right, y + h - 10, 20, 20, 0, PI / 2);
			rect(right + 0.5f, y + 10, 10, h - 20);

			fill(darkColor);
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
				right += (snappedRight - right) / slowness;
				if(abs(snappedRight - right) == 1){
					right = snappedRight;
				}
			}
		}
	}
}
