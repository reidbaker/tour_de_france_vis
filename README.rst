This is a visualization of tour de france data.

To compile, run from the project root:
javac -sourcepath src -classpath lib\core.jar src\Francify.java -d bin

Make a bin folder with the core.jar file in it, and an HTML file with these contents:

<HTML>
<body>
<applet code="Francify.class" archive="core.jar" codebase="bin/" width="800" height="600">
Main Applet Here
</applet>
</body>
</HTML>
