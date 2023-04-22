# Game Project

## Presentation Slides
https://docs.google.com/presentation/d/1Q7BBSKuJeJMZEBPUjwyebySSNRu1Doms5BoOL3TGIcY/edit?usp=sharing

## Requirements:
- JavaJDK
- Java Runtime
- Ant (for building the project)
- Terminal supporting ANSI escape codes

## How to run:

If “gnome-terminal” is installed, run the following script, it will compile the code, run all nodes, and start 
3 player clients:
```
./run_game.sh matrixSize splitSize
```
For Example, the following command will create a 20x20 map managed by 4 nodes (2x2 nodes):
ex:
``` 
./run_game.sh 20 2
```
If “gnome-terminal” not is installed, compile the code and create the jar files by running the ant 
command:
```
ant
```
Run the rmiregistry using the provided script:
```
./start-rmi.sh
```
Run the Entry Node with the preferred parameters:
```
java -jar dist/EntryNodeServer.jar matrixSize splitSize
```
For Example, the following command will create an entry node that handles 20x20 map managed by 4 
nodes (2x2 nodes):
```
java -jar dist/EntryNodeServer.jar 20 2
```
Start Zone nodes depending on the splitSize set, you need splitSize*splitSize nodes:
```
java -jar dist/ZoneNodeServer.jar localhost
```
Run a player client:
```
java -jar dist/PlayerClient.jar localhost
```