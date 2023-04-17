if pgrep rmiregistry; then pkill rmiregistry; fi
sleep 1
rmiregistry -J-Djava.class.path=bin &
