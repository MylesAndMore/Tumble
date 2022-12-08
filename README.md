# tumble-alphatest (@MylesAndMore @CraivMan)

please let me know if there is anything more you think we should add @CraivMan!
once this list is complete and all bugs are fixed, we *should* be ready for release...

## generation  

- [ ] layers should be able to generate w/ "clumps" of blocks; instead of only one material as a whole
  - [ ] the clump size should be customizable (for later); be able to set a min/max val and it will choose randomly per each clump (not in config file yet, just internally)
- [ ] make shovels generation actually work properly
  - make different types of platforms (square, circle, multi-tiered, etc.); still should be pseudo-random
- [ ] make snowballs generation actually work properly (shocker)
  - make three layers generate (same layer types as shovels, just multiple of them)

## game realism  

- [x] make the shovel in shovels mode not lose any durabilty
- [x] make it so that you can't move until the game begins
- [ ] make the game blocks breakable very fast, but **not instantly--very important for balancing!!**
  - Basically, just set a "cooldown" on both snowballs and shovels--not a long one--but one at that
- [ ] add infinite snowballs in the gamemanager for tumble mode  
- [x] make it so that you can't remove any of the game items from your inventory
- [x] make snowballs actually break blocks (duh)
- [x] make the randomized mode logic
- [ ] set some limits on the spectator mode in-game; make it so they can't fly outside of the map
- [ ] make it so rounds end in a draw after 5m
- [ ] make it so that players get snowballs instead of shovels in shovels rounds after 2m 30s
- [ ] remove snowball knockback

## game logic  

- [x] make a Game class and object that we can initialize a new instance of with a gameType
- [x] prevent players from joining/autojoining during a game
- [x] keep track of when someone wins; start a new round when this happens
- [x] keep track of how many wins each player has; end the game when a player reaches 3
  - [x] add a section in the config for a place to tp the winning player
    - [x] add logic to do this

## configuration/customization

- [ ] add two configs where you can:
  - [ ] set if you want the game to auto-start
  - [ ] set the amt of players you want the game to auto-start at
  - [ ] program the auto-start (just add an if statement on the PlayerJoin listener to run the StartGame method on a certain amt of players in the config)  

## etc  

- [ ] add game music? but probably only for us; I feel like the og music must be copyrighted
