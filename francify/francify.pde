void setup() {
  size(480, 120);
  String [] lines = loadStrings("data/tour_de_france_data.csv");
  for (String line : lines) {
      String[] pieces = split(line, ',');
      print(pieces[0] + " " + pieces[1] + " " + pieces[2]);
  }
}

void draw() {
  if (mousePressed) {
    fill(0);
  } else {
    fill(255);
  }
  ellipse(mouseX, mouseY, 80, 80);
}
