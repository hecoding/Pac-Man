Ms Pac-Man
====
<p align="center"><img src="http://gamewires.com/Images/Posts/32542_banner.jpg" height='200' alt="Ms Pac-Man"/></p>

Ever since the birth of video-games we’ve seen Artificial Intelligence techniques applied to them: Character behaviour, enemy strategies, pathfinding, etc. We want to explore Grammatical Evolution (a Genetic Programming variant) to evolve game strategies generated from the derivation of defined grammar rules. For this purpose, we experimented with the evolution of a bot for Ms. Pac-Man, a well-known game which can have many sub-goals, like surviving the most time possible, eating the most pills, killing as many ghosts as it can, or go through a lot of levels before dying to the ghosts.

Particularly, we experimented with controllers based on two different grammars, with high and medium level actions respectively. Due to the complexity of video-games and how useful it could be for an artificial intelligence to modify its behaviour in real time, we want to check the results of multi-objective optimization in grammatical evolution. And how we can achieve the subgoals we consider more important in a situation by simply changing the evaluation functions we use in the grammatical evolution algorithm.

In this work we will show that this approach based on Grammatical Evolution gets excellent results and we will see that bots produced can obtain high scores and complete several levels, even better results than the coded bots included, or other known evolutionary bots.

<p align="center"><img src="https://j.gifs.com/NxRpLL.gif"/></p>

## Downloading
#### Using the command line
`git clone https://github.com/hecoding/Pac-Man.git`
#### From Eclipse or other IDE
`File > Import > Git > Projects from Git > Clone URI`  
Paste `https://github.com/hecoding/Pac-Man.git` in `URI` field  
Check `master` only and click `Finish`  

## Running
On the project, go to `src/main` and execture class `Main.java`. The GUI will open.

## Checking the code
[Configure your Eclipse](https://github.com/hecoding/Pac-Man/wiki/%5BHOW-TO%5D-Configurar-Eclipse) so that the code looks good (UTF-8 and Unix line endings).

### [Meet the Docs](https://github.com/hecoding/Pac-Man/wiki)
