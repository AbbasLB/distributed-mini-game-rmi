all: 
	javac *.java -d classes

start-rmi:
	if pgrep rmiregistry; then pkill rmiregistry; fi
	rmiregistry &
	
start-entry:
	java -cp classes EntryNodeServer

start-zone:
	java -cp classes ZoneNodeServer localhost

start-player:
	java -cp classes PlayerClient localhost

clean:
	rm -rf classes