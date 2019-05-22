all: compile

compile:
	java -jar ../jtb132di.jar -te minijavagram.jj
	java -jar ../javacc5.jar minijavagram-jtb.jj
	javac Main.java

clean:
	rm -f *.class *~
