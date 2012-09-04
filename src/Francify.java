import processing.core.PApplet;

public class Francify extends PApplet {

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "Francify" });
	}

	Slider s;
	boolean unpressed;
	boolean movingSlider, leftHandle, rightHandle;

	public void setup() {
		size(800, 600);
		frameRate(30);
		
		s = new Slider(50, 500, 700, 50);
		s.setValues(new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12});
		unpressed = true;
		movingSlider = false;
		leftHandle = false;
		rightHandle = false;

		// Handle data import
	}

	public void draw() {
		// Handle data drawing

		background(0xcccccc);
		drawAxes();
		s.drawSlider();
		handleInput();
		updateAnim();
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
		s.updateAnim(4);
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
		}

		public void drawSlider() {
			strokeWeight(1);
			
			// Draw underlying data
			fill(0,0,0);
			for(int i = 0; i < values.length; i++){
				int xpos = x + (i) * w / (values.length) + w / (2*values.length);
				ellipse(xpos,y+h/2,10,10);
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
			if (x > left && x < right && y > this.y && y < this.y + h) {
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
			
			int rightX = goalRight - x;
			float ratioR = rightX / (float)w;
			index = (int)(ratioR * values.length + 0.5);
			snappedRight = x + w * index / values.length;
		}
		
		public float getLeftBound(){
			//TODO: fix after adding data snap points
			return left;
		}
		
		public float getRightBound(){
			//TODO: fix after adding data snap points
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
