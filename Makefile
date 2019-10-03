default: Database
%: %.java
	javac $@.java
	java -ea $@
