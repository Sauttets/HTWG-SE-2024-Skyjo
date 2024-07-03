[![Coverage Status](https://coveralls.io/repos/github/Sauttets/HTWG-SE-2024-Skyjo/badge.svg?branch=main)](https://coveralls.io/github/Sauttets/HTWG-SE-2024-Skyjo?branch=main)

## Skyjo

**How to play:**

1. Draw a card from the ''Stack'' or ''Trash''  
   2a. Swap it with a card from your Field  
   2b. Discard it by clicking on the trash  
   and flip open a card from your Field  

The goal of the game is to have the least amount of points on the board
(closed cards included, they get flipped open at the end of the game).
If a row has only cards with the same value, the whole row is not counted towards the points.

## Screenshots

### Game Screen

![Game Screen](example_imgs/game_screen.png)

### Finish Screen

![Finish Screen](example_imgs/finish_screen.png)

### Docker

to use the docker container run:
`docker build -t skyjo:v1 . && xhost +local:docker && docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix --network host -ti skyjo:v1`

For macos XQuartz and socat is requred.
