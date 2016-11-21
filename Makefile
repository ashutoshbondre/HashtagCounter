JFLAGS = -g
JC = javac
JCR = java

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	hashtagcounter.java \
	MaxFiboHeap.java \

TXT_FILE = \
	sampleInput.txt \

compile: classes

default: classes exec-tests

classes: $(CLASSES:.java=.class)

clean: 
	$(RM) *.class *~

exec-tests: classes
	$(JCR) hashtagcounter $(x)


.PHONY: default clean classes exec-tests