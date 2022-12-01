# tumble-alphatest (@MylesAndMore @CraivMan)

please let me know if there is anything more you think we should add @CraivMan!
once this list is complete and all bugs are fixed, we *should* be ready for release...

## generation  

- [ ] layers should be able to generate w/ "clumps" of blocks; instead of only one material as a whole
  - [ ] the clump size should be customizable (for later); be able to set a min/max val and it will choose randomly per each clump (not in config file yet, just internally)
  - PLEASE make a new class for this and make use of the generator if you can!  

## game realism  

- [ ] make the game blocks instant-breakable
- [ ] add infinite snowballs in the gamemanager for tumble mode  
- [ ] make the randomized mode logic

## game logic (fyi: very object-oriented)  

- [ ] make a Game class and object that we can initialize a new instance of with a gameType
  - [ ] within this game object, while games are running:
    - [ ] prevent players from joining/autojoining during
    - [ ] keep track of when someone wins; start a new round when this happens
    - [ ] keep track of how many wins each player has; end the game when a player reaches 3
      - [ ] add a section in the config for a place to tp the winning player
      - [ ] add logic to do this  

## game legitimacy (@MylesAndMore)  

- [ ] add some example layer generation and layer material types, from actual game @MylesAndMore  

## configuration/customization

- [ ] add two configs where you can:
  - [ ] set if you want the game to auto-start
  - [ ] set the amt of players you want the game to auto-start at
  - [ ] program the auto-start (just add an if statement on the PlayerJoin listener to run the StartGame method on a certain amt of players in the config)  

## etc  

- [ ] add game music? but probably only for us; I feel like the og music must be copyrighted
