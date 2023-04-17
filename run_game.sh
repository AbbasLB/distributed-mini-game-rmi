
if [ "$#" -ne 2 ]; then
  echo "Error: Usage run_game.sh matrixSize splitSize"
  exit 1
fi

echo "Compiling Code ..."
gnome-terminal --tab --title="Compile" -- bash -c "ant; exec bash"
sleep 2
echo "Running Rmi ..."
gnome-terminal --tab --title="RMI" -- bash -c "./start-rmi.sh; exec bash"
sleep 1
echo "Running Entry Node ..."
gnome-terminal --tab --title="Entry Node" -- bash -c "java -jar dist/EntryNodeServer.jar $1 $2; exec bash"
sleep 2
for (( i=1; i<=$2*$2; i++ ))
do
  echo "Running Zone Node $i ..."
  gnome-terminal --tab --title="Zone Node $i" -- bash -c "java -jar dist/ZoneNodeServer.jar localhost; exec bash"
done
sleep 2
gnome-terminal --title="Player 1" -- bash -c "java -jar dist/PlayerClient.jar localhost; exec bash"
gnome-terminal --title="Player 2" -- bash -c "java -jar dist/PlayerClient.jar localhost; exec bash"

