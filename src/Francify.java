import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import controlP5.*;

import processing.core.PApplet;
import processing.core.PFont;

public class Francify extends PApplet {

	private static final long serialVersionUID = 1L;
	public static final int PART_ONE = 0, PART_TWO = 1;
	public static final boolean DRAW_DISTANCE = true;
	public static final boolean DRAW_SPEED= false;

	public ControlP5 cp5;
    public CheckBox lineGraph, lineGraph2;
    public RadioButton barGraph;
    public boolean enableDistance = true;
	public boolean enableSpeed = true;
    public boolean sortAscending = true;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "Francify" });
	}

	//Skinning Color Variables
	int backgroundColor = 0xFFCCCCCC;
	int tabBackgroundColor = 0xFF777777;
	int tabUnselectColor = 0xFF999999;
	int darkColor = 0xFF002E3E;
	int dataColor0 = 0xFF4499bb;
	int dataColor1 = 0xFF88c23c;
	int detailsOnDemandColor = 0xAAE7E7E7;
	
	Slider sCurrent, sOne, sTwo;
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
	String sliderLabel;
	
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
			
			if (!rr.firstPlaceCountry.equals("(no data)")) {
				Integer medals = numMedals.get(rr.firstPlaceCountry);
				if (medals == null) {
					numMedals.put(rr.firstPlaceCountry, 1);
				} else {
					numMedals.put(rr.firstPlaceCountry, medals + 1);
				}
			}

			data.put(rr.year, rr);
		}
		
		int maxMedals = 0;
		for(String s : numMedals.keySet()){
			if(numMedals.get(s) > maxMedals){
				maxMedals = numMedals.get(s);
			}
		}
		int[] medalCounts = new int[maxMedals + 1]; 
		for(int i = 0; i <= maxMedals; i++){
			medalCounts[i] = i;
		}
		sTwo = new Slider(graphX, graphY + graphH + 50, graphW, 50, medalCounts);
		sTwo.setDrawInterval(5);
		
		int[] vals = new int[maxSYear - minSYear];
		for(int i = minSYear; i < maxSYear; i++){
			vals[i-minSYear] = i;
		}
		sOne = new Slider(graphX, graphY + graphH + 50, graphW, 50, vals);
		sOne.setDrawInterval(10);
				
		sCurrent = sOne;
		sliderLabel = "Years";

        // checkboxes for line graph
        cp5 = new ControlP5(this);
        lineGraph = cp5.addCheckBox("LineGraph")
                .setPosition(graphX + graphW + 10, graphY)
                .setColorForeground(dataColor0)
                .setColorBackground(rgba(darkColor, 0x33))
                .setColorActive(dataColor0)
                .setColorLabel(darkColor)
                .setSize(20, 20)
                .setItemsPerRow(1)
                .setSpacingColumn(45)
                .setSpacingRow(20)
                .addItem("Distance", 1);
        lineGraph.toggle("Distance");

        lineGraph2 = cp5.addCheckBox("LineGraph2")
	        .setPosition(graphX + graphW + 10, graphY + 25)
	        .setColorForeground(dataColor1)
	        .setColorBackground(rgba(darkColor, 0x33))
	        .setColorActive(dataColor1)
	        .setColorLabel(darkColor)
	        .setSize(20, 20)
	        .setItemsPerRow(1)
	        .setSpacingColumn(45)
	        .setSpacingRow(20)
	        .addItem("Avg. Speed", 1);
        lineGraph2.toggle("Avg. Speed");

        // checkboxes for bargraph
        barGraph= cp5.addRadioButton("BarGraph")
                .setPosition(graphW + graphX + 10, graphY)
                .setColorForeground(rgba(dataColor0, 0x88))
                .setColorBackground(rgba(darkColor, 0x33))
                .setColorActive(rgba(dataColor0, 0x88))
                .setColorLabel(darkColor)
                .setNoneSelectedAllowed(false)
                .setSize(20, 20)
                .setItemsPerRow(1)
                .setSpacingColumn(45)
                .setSpacingRow(20)
                .addItem("Ascending", 1)
                .addItem("Descending", 0)
                .activate(0);
        barGraph.setVisible(false);
	}

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isFrom(lineGraph)) {
            int distanceChecked = (int) lineGraph.getArrayValue()[0];
            if (distanceChecked == 1) {
                enableDistance = true;
            }
            else{
                enableDistance = false;
			}
		} else if (theEvent.isFrom(lineGraph2)) {
			int speedChecked = (int) lineGraph2.getArrayValue()[0];
            if (speedChecked == 1) {
                enableSpeed= true;
            }
            else{
                enableSpeed = false;
            }
        }
        else { //The event is from BarGraph
            if ((int)(theEvent.getGroup().getArrayValue()[0] + 0.5f) == 1){
                sortAscending = true;
            }
            else{
                sortAscending = false;
            }
        }
    }
	
	public void draw() {
		// Handle data drawing

		background(backgroundColor);
		drawAxes();
		drawTabs();
		sCurrent.drawSlider();
		handleInput();
		if(currentDisplayed == PART_ONE){
        detailsDistance = Float.MAX_VALUE;
        RaceRow detailRow = null;
        if (enableDistance) {
            detailRow = drawData(DRAW_DISTANCE, sCurrent.getLeftBound(),
                    sCurrent.getRightBound());
        }
        if (enableSpeed) {
            RaceRow speedRow = drawData(DRAW_SPEED, sCurrent.getLeftBound(),sCurrent.getRightBound());
            if (speedRow != null) {
                detailRow = speedRow;
            }
        }
		if (detailRow != null) {
			detailsOnDemand(detailRow);
		}
		} else {
			//bar graph code
			ArrayList<String> countries = filterByMedals(sCurrent.getLeftBound(),
					sCurrent.getRightBound());
			String hilighted = drawBarGraph(countries);
			if(hilighted != null){
				barDetailsOnDemand(hilighted);
			}
		}
		updateAnim();
		if (!mousePressed)
			updateCursor();
		
		updateSize();
	}
	
	public void updateSize(){
		if(graphW != width - 200 || graphH != height - 200){
			graphW = width - 200;
			graphH = height - 200;
			sOne.y = graphY + graphH + 50;
			sTwo.y = graphY + graphH + 50;
			sOne.changeWidthTo(graphW);
			sTwo.changeWidthTo(graphW);
			lineGraph.setPosition(graphX + graphW + 10, lineGraph.getPosition().y);
			lineGraph2.setPosition(graphX + graphW + 10, lineGraph2.getPosition().y);
			barGraph.setPosition(graphX + graphW + 10, barGraph.getPosition().y);
		}
	}
	
	public void barDetailsOnDemand(String countryToDraw){
		if(!sortAscending){ // Mouse on left half of graph
			barDetailsOnDemand(countryToDraw, graphX + graphW/2 + 10, graphY + 10, graphW/2-20, graphH/2, 20);
		} else {
			barDetailsOnDemand(countryToDraw, graphX + 10, graphY + 10, graphW/2-20, graphH/2, 20);
		}
	}
	
	public void barDetailsOnDemand(String countryToDraw, int x, int y, int w, int h, int r){
		ArrayList<Integer> years = getWinningYears(countryToDraw);
		int textSize = 18;
		fill(detailsOnDemandColor);
		stroke(darkColor);
		rect(x,y,w,h,r);

		fill(darkColor);
		textFont(largerFont);
		textSize(textSize);
		textAlign(LEFT);
		int ty = y + r/2 + textSize;
		int tx = x + r;
		text(countryToDraw, tx, ty);
		ty += textSize;
		text("Winning years:", tx, ty);
		tx += textSize;
		text("", ty, tx);
		for(int i = 0; i < years.size(); i++){
			if(i % 6 == 0){
				ty += textSize;
				text(years.get(i) + (i != years.size()-1 ? ", " : ""), tx, ty);
			} else {
				text(years.get(i) + (i != years.size()-1 ? ", " : ""));
			}
		}
	}
	
	public ArrayList<Integer> getWinningYears(String country){
		ArrayList<Integer> years = new ArrayList<Integer>();
		for(int i : data.keySet()){
			RaceRow rr = data.get(i);
			if(rr.firstPlaceCountry.equals(country)){
				years.add(i);
			}
		}
		return years;
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
		stroke(darkColor);
		rect(x,y,w,h,r);
		
		fill(darkColor);
		textFont(largerFont);
		textSize(textSize);
		textAlign(LEFT);
		int ty = y + r/2 + textSize;
		int tx = x + r;
		text("Year: " + row.year, tx, ty);
		ty += textSize;
		text("Average Speed: " + row.avgSpeed + " km/h", tx, ty);
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
		float year = mapToPlotX(row.year, sCurrent.getLeftBound(), sCurrent.getRightBound(), graphX, graphW);     
        float distY = mapToPlotY(row.distance, minDistance, maxDistance,
                    graphY, graphH);
        float speedY = mapToPlotY(row.avgSpeed, minSpeed, maxSpeed,
                    graphY, graphH);
		if (enableDistance)
			ellipse(year, distY, 8, 8);
		if (enableSpeed)
			ellipse(year, speedY, 8, 8);
	}
	
	public void updateCursor(){
		int pos = sCurrent.whereIs(mouseX, mouseY);
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
		
		//Handle Tab Stuff
		if(mouseY < 25){
			if(currentDisplayed == PART_ONE && mouseX > 11 * width / 20 && mouseX < 19 * width / 20){
				cursor(HAND);
			}
			if(currentDisplayed == PART_TWO && mouseX > width / 20 && mouseX < 9 * width / 20){
				cursor(HAND);
			}
		}
	}
	
	public void drawRange(){
		fill(darkColor);
		textFont(largerFont);
		if(currentDisplayed == PART_ONE){
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
			textAlign(CENTER);
			text(sliderLabel, getWidth()/2, height - 10);
		}
		} else {
			//PART TWO
			textAlign(RIGHT);
			fill(darkColor);
			text(sCurrent.values[0], graphX - 5, graphY + graphH);
			text(sCurrent.values[sCurrent.values.length-1], graphX - 5,graphY + 18);

			textAlign(CENTER);
			if( sCurrent.getLeftBound() != sCurrent.getRightBound()){
			text(sliderLabel + " : " + sCurrent.getLeftBound() + " - "
					+ sCurrent.getRightBound(), getWidth() / 2, height - 10);
			} else {
				text(sliderLabel + " : " + sCurrent.getLeftBound(),
						getWidth() / 2, height - 10);
			}
		}
	}
	
	public void handleInput(){
		// Handle user input
		if (mousePressed == true) {
			if (unpressed) {
				// Everything within this block occurs when first clicked
				// (pressed state toggles on)
				unpressed = false;
				int loc = sCurrent.whereIs(mouseX, mouseY);
				if (loc == Slider.INSIDE)
					movingSlider = true;
				else if (loc == Slider.LEFTHANDLE)
					leftHandle = true;
				else if (loc == Slider.RIGHTHANDLE)
					rightHandle = true;
				
				//Handle Tab Stuff
				if(mouseY < 25){
					if(currentDisplayed == PART_ONE && mouseX > 11 * width / 20 && mouseX < 19 * width / 20){
						toggleView(PART_TWO);
					}
					if(currentDisplayed == PART_TWO && mouseX > width / 20 && mouseX < 9 * width / 20){
						toggleView(PART_ONE);
					}
				}
			} else {
				// Everything in this block occurs when the mouse has been
				// pressed for some period (clicking and dragging, etc.)
				if(movingSlider){
					sCurrent.dragAll(mouseX, pmouseX);
				} else if(leftHandle){
					sCurrent.dragLH(mouseX, pmouseX);
				} else if(rightHandle){
					sCurrent.dragRH(mouseX, pmouseX);
				}
				sCurrent.snapGoals();
			}
		}
	}
	
	public void drawTabs(){
		fill(tabBackgroundColor);
		noStroke();
		rect(0,0,width,25);
		strokeWeight(2);
		if (currentDisplayed == PART_ONE) {
			drawLeftTab(darkColor, backgroundColor, false);
			drawRightTab(darkColor, tabUnselectColor, true);
		} else {
			drawLeftTab(darkColor, tabUnselectColor, true);
			drawRightTab(darkColor, backgroundColor, false);
		}
	}
	
	public void drawLeftTab(int strokeColor, int tabColor, boolean back){
		int radius = 10;
		noStroke();
		fill(tabColor);
		rect(width/20,25-radius,radius,radius);
		rect(9 * width/20 - radius, 25-radius, radius, radius);
		rect(width/20+radius,radius,2 * width / 5 - 2 * radius, 25-radius);
		rect(width/20+ 2 * radius, 0, 2 * width / 5 - 4 * radius, radius);
		stroke(strokeColor);
		line(0,25, width/20, 25);
		line(9 * width / 20, 25, width/2, 25);
		line(width/20 + radius, radius, width/20 + radius, 25 - radius);
		line(9 * width / 20 - radius, radius, 9 * width / 20 - radius, 25 - radius);
		line(width/20 + 2 * radius, 0, 9 * width / 20 - 2 * radius, 0);
		fill(tabBackgroundColor);
		arc(width/20, 25-radius, radius*2, radius*2, 0, PI/2);
		arc(9*width/20, 25-radius,radius*2, radius*2, PI/2, PI);
		fill(tabColor);
		arc(width/20 + radius*2, radius, radius*2, radius*2, PI, 3*PI/2);
		arc(9*width/20 - radius*2, radius, radius*2, radius*2, 3*PI/2, 2*PI);
		if(back)
			line(0,25,width/2,25);
		textFont(largerFont);
		textAlign(CENTER);
		fill(darkColor);
		text("Distance and Avg. Speed", width/4, 20);
	}
	
	public void drawRightTab(int strokeColor, int tabColor, boolean back){
		int radius = 10;
		noStroke();
		fill(tabColor);
		rect(11 * width/20,25-radius,radius,radius);
		rect(19 * width/20 - radius, 25-radius, radius, radius);
		rect(11 * width/20+radius,radius,2 * width / 5 - 2 * radius, 25-radius);
		rect(11 * width/20+ 2 * radius, 0, 2 * width / 5 - 4 * radius, radius);
		stroke(strokeColor);
		line(width/2,25, 11 * width/20, 25);
		line(19 * width / 20, 25, width, 25);
		line(11 * width/20 + radius, radius, 11 * width/20 + radius, 25 - radius);
		line(19 * width / 20 - radius, radius, 19 * width / 20 - radius, 25 - radius);
		line(11 * width/20 + 2 * radius, 0, 19 * width / 20 - 2 * radius, 0);
		fill(tabBackgroundColor);
		arc(11 * width/20, 25-radius, radius*2, radius*2, 0, PI/2);
		arc(19*width/20, 25-radius,radius*2, radius*2, PI/2, PI);
		fill(tabColor);
		arc(11 *width/20 + radius*2, radius, radius*2, radius*2, PI, 3*PI/2);
		arc(19*width/20 - radius*2, radius, radius*2, radius*2, 3*PI/2, 2*PI);
		if(back)
			line(width/2,25,width,25);
		textFont(largerFont);
		textAlign(CENTER);
		fill(darkColor);
		text("Medal Counts", 3*width/4, 20);
	}
	
	public void updateAnim(){
		// Update animation values (simple spring animation)
	    int speed = 4;
	    sCurrent.updateAnim(speed);
	}

	public void mouseReleased() {
		unpressed = true;
		movingSlider = false;
		leftHandle = false;
		rightHandle = false;
		sCurrent.updateGoals();
	}
	
	public void toggleView(int toggleTo){
		if (currentDisplayed != toggleTo) {
			currentDisplayed = (currentDisplayed + 1) % 2;
			if (currentDisplayed == PART_ONE) {
				lineGraph.setVisible(true);
				lineGraph2.setVisible(true);
				barGraph.setVisible(false);
				sCurrent = sOne;
				sliderLabel = "Years";
			} else {
				lineGraph.setVisible(false);
				lineGraph2.setVisible(false);
				barGraph.setVisible(true);
				sCurrent = sTwo;
				sliderLabel = "Number of Medals";
			}
		}
	}

	public void drawAxes() {
//	     ,  /\  .
//	    //`-||-'\\
//	   (| -=||=- |)
//	    \\,-||-.//
//	     `  ||  '
//	        ||
//	        ||
//	        ||
//	        ||
//	        ||
//	        ()
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
		
		if(currentDisplayed == PART_ONE){
		
		textFont(largerFont);
		textAlign(CENTER);
		fill(rgba(dataColor0, 0.75f));
		pushMatrix();
		translate(40,graphY+graphH/2);
		rotate(-PI/2);
		text("Distance (km)", 0, 0);
		fill(rgba(dataColor1, 0.75f));
		text("Average Speed (km/h)", 0, 40);
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
		text(""+minDistance, 0, 0);
		fill(rgba(dataColor1, 0.75f));
		text(""+minSpeed, 0, 18);
		popMatrix();
		textAlign(CENTER);
		} else {
			textFont(largerFont);
			textAlign(CENTER);
			fill(darkColor);
			pushMatrix();
			translate(65,graphY+graphH/2);
			rotate(-PI/2);
			text("# Gold Medals Won", 0, 0);
			popMatrix();
		}
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

                if(minBound == maxBound){
                	line(graphX, y, graphX+graphW, y);
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

    public ArrayList<String> filterByMedals(int min, int max){
        TreeSet<SortableCountry> toSort = new TreeSet<SortableCountry>();
        for(String s : numMedals.keySet()){
        	toSort.add(new SortableCountry(s, numMedals.get(s)));
        }
        ArrayList<String> names = new ArrayList<String>();
        for(SortableCountry sc : toSort){
			if (sc.medals >= min && sc.medals <= max)
				names.add(sc.name);
        }
        return names;
    }
    
    private class SortableCountry implements Comparable<SortableCountry>{
    	public String name;
    	public int medals;
    	public SortableCountry(String name, int medals){
    		this.name = name;
    		this.medals = medals;
    	}
		@Override
		public int compareTo(SortableCountry o) {
			int comp = medals - o.medals;
			if(comp != 0)
				return comp;
			else return name.compareTo(o.name);
		}
    }

    public String drawBarGraph(ArrayList<String> countries){
        ArrayList<String> curCountries = null;
        String details = null;
        float distanceBetween = 10;
        int numCountries = countries.size();
        //evenly divide bars across graph
        if(numCountries > 0){
            float width = graphW / numCountries;
            width -= distanceBetween;
            if (sortAscending){
                //Do nothing
                curCountries = countries;
            }
            else{
                curCountries = new ArrayList<String>();
                for(int i=numCountries-1; i >= 0; i--){
                    curCountries.add(countries.get(i));
                }
            }
            for(int i=0; i < numCountries; i++){
                if(doBarWork(curCountries, distanceBetween, width, i)){
                	details = curCountries.get(i);
                }
            }
        }
        return details;
    }

    private boolean doBarWork(ArrayList<String> countries, float distanceBetween,
            float width, int i) {
        int medalCount = numMedals.get(countries.get(i));
        float xLoc = graphX + i*distanceBetween + i*width + distanceBetween;
        return drawBar(false, medalCount, xLoc, graphY, width, graphH, dataColor0, countries.get(i));
    }

    public boolean drawBar(boolean striped, float amount, float xOffset,
            float yOffset, float width, float graphH, int drawColor, String label) {

    	float barY = map(amount, 37, 0, graphY, graphY+graphH);
        float height = graphY + graphH - barY;
        // Point of the top left corner of the bar to be drawn

        if (striped) {
            // pattern in bar graph
            int lineWeight = 3;
            strokeWeight(lineWeight);
            stroke(rgba(drawColor, 0x55));
            int patternHeight = 10;
            float lineWidth = width - lineWeight * 2;
            float startPatternY = yOffset + graphH - patternHeight - lineWeight + 1;
            float lineX = xOffset + lineWeight;

            for (float i = startPatternY; i > barY; i -= patternHeight) {
                line(lineX, i, lineX + lineWidth, i + patternHeight);
            }
            fill(rgba(drawColor, 0x44));
        }
        else {
            fill(rgba(drawColor, 0x88));
        }
        //Draw bargraph
        noStroke();
        rect(xOffset, barY, width, height, 7);
        
        //Draw Label
        textFont(largerFont);
        textSize(14);
        fill(darkColor);
        pushMatrix();
        if(height < 50){
            textAlign(LEFT);
            translate(xOffset + width/2 + 6, barY - 5);
        } else {
            textAlign(CENTER);
            translate(xOffset + width/2 + 6, barY + height/2);
        }
        rotate(-PI/2);
        text(label, 0, 0);
        popMatrix();
        
        return (mouseX > xOffset && mouseX < xOffset+width && mouseY > barY && mouseY < barY + height);
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
		
		public void changeWidthTo(int newWidth) {
			//Updates the slider width to a new value;
			float ratioR = (right-x)/(float)w, ratioL = (left-x)/(float)w;
			float ratioGR = (goalRight-x)/(float)w, ratioGL = (goalLeft-x)/(float)w;
			w = newWidth;
			right = x + (int)(ratioR * w + 0.5);
			left = x + (int)(ratioL * w + 0.5);
			goalRight = x + (int)(ratioGR * w + 0.5);
			goalLeft = x + (int)(ratioGL * w + 0.5);
			snapGoals();
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

			if(currentDisplayed == PART_ONE){
				drawData(DRAW_DISTANCE, values[0], values[values.length - 1],
						1, x, y, w, h);
				drawData(DRAW_SPEED, values[0], values[values.length -1], 1, x, y, w, h);
			} else{
				ArrayList<String> countries = filterByMedals(values[0], values[values.length-1]);
				strokeWeight(1);
				fill(dataColor0);
				for(int i = 0; i < countries.size(); i++){
					float barLen = map(numMedals.get(countries.get(i)), values[0], values[values.length-1], 0, w - w/(values.length));
					rect(x + w / (2 * values.length), y + h - (i+1) * h/countries.size(), barLen, h/countries.size());
				}
			}
			
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
			if(index != 0)
			rangeMax = values[index - 1];
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
	
	private class RaceRow implements Comparable<RaceRow>{
		
		public int year, firstCountryID, secondCountryID, thirdCountryID,
				numStages;
		public String firstPlaceRider, firstPlaceCountry, firstPlaceTeam,
				secondPlaceRider, secondPlaceCountry, secondPlaceTeam,
				thirdPlaceRider, thirdPlaceCountry, thirdPlaceTeam, bestTeam;
		public float c2nd, c3rd, avgSpeed, distance;
		
		public RaceRow(){
			year = firstCountryID = secondCountryID = thirdCountryID =
			numStages = 0;
			firstPlaceRider = firstPlaceCountry = firstPlaceTeam =
			secondPlaceRider = secondPlaceCountry = secondPlaceTeam =
			thirdPlaceRider = thirdPlaceCountry = thirdPlaceTeam = bestTeam = null;
			c2nd = c3rd = avgSpeed = distance = 0.0f;
		}

		@Override
		public int compareTo(RaceRow o) {
			return year - o.year;
		}
	}
}
