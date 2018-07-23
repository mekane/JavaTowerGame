all:
	rm -f *.class
	javac TowerGame.java

warn:
	rm -f *.class
	javac -Xlint TowerGame.java

clean:	
	rm *.class
	rm *~

dist:	all	
	jar -cvfm dist/TowerGame.jar MANIFEST.MF *.java *.class
