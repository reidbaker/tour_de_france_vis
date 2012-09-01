import processing.core.PApplet;
import processing.core.PFont;

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
		unpressed = true;
		movingSlider = false;
		leftHandle = false;
		rightHandle = false;

		// Handle data import
	}

	public void draw() {
		// Handle data drawing

		background(0xd9cccc);
		drawAxes();
		s.drawSlider();

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
			}
		}
	}

	public void mouseReleased() {
		unpressed = true;
		movingSlider = false;
		leftHandle = false;
		rightHandle = false;
	}

	public void drawAxes() {
		// Draw Axes Lines
		stroke(0);
		strokeWeight(3);
		line(50,50,50,450);
		line(50,450,750,450);

		// Draw Labels
	}

	public void drawData() {
		// Set colors and draw lines. Use a thicker stroke is possible
	}

	private class Slider {
		int x, y, w, h;
		int left, right;

		int[] values;

		public static final int OUTSIDE = 0, INSIDE = 1, LEFTSLIDER = 2,
				RIGHTSLIDER = 3;

		public Slider(int x, int y, int w, int h) {
			this.left = this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.right = w + x;
		}

		public void setValues(int[] values) {
			this.values = values;
		}

		public void drawSlider() {
			strokeWeight(1);
			
			// Draw underlying data
			fill(0,0,0);
			rect(x+20, y+h/2-20, 100, 40);

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
			left += nx-px;
			right += nx-px;
			if(left < x){
				right += x-left;
				left += x-left;
			}
			if(right > x + w){
				left -= (right-(x+w));
				right -= (right-(x+w));
			}
			//TODO: bounds checking
		}
		
		public void dragLH(int nx, int px){
			left += nx-px;
			if(left < x){
				left += x-left;
			} else if(left > right){
				left = right;
			}
			//TODO: bounds checking
		}
		
		public void dragRH(int nx, int px){
			right += nx-px;
			if(right > x+w){
				right -= (right-(x+w));
			} else if(left > right){
				right = left;
			}
			//TODO: bounds checking
		}
	}
}
