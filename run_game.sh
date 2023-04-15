
if [ "$#" -ne 2 ]; then
  echo "Error: Usage run_game.sh matrixSize splitSize"
  exit 1
fi

echo "Compiling Code ..."
gnome-terminal --tab --title="Compile" -- bash -c "make; exec bash"
sleep 2
echo "Running Rmi ..."
gnome-terminal --tab --title="RMI" -- bash -c "make start-rmi; exec bash"
sleep 1
echo "Running Entry Node ..."
gnome-terminal --tab --title="Entry Node" -- bash -c "java -cp classes EntryNodeServer $1 $2; exec bash"
sleep 2
for (( i=1; i<=$2*$2; i++ ))
do
  echo "Running Zone Node $i ..."
  gnome-terminal --tab --title="Zone Node $i" -- bash -c "make start-zone; exec bash"
done
sleep 2
gnome-terminal --title="Player 1" -- bash -c "make start-player; exec bash"
gnome-terminal --title="Player 2" -- bash -c "make start-player; exec bash"

