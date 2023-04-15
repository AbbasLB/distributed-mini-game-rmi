all: 
	javac -d classes *.java 

start-rmi:
	if pgrep rmiregistry; then pkill rmiregistry; fi
	sleep 1
	rmiregistry -J-Djava.class.path=classes &

start-entry:
	java -cp classes EntryNodeServer 20 4

start-zone:
	java -cp classes ZoneNodeServer localhost

start-player:
	java -cp classes PlayerClient localhost

clean:
	rm -rf classes