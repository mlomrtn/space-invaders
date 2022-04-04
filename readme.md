# Next up

1. [ ] detect when the fleet is at the right edge of the screen
   - find column position of the rightmost invader (started)
   - multiply by column width + offset

2. [ ] move left instead of right

4. [ ] read the keyboard events to move the ship
   - keyboard events are published on the channel already
   - store extra state in the fleet to represent the ship's position
     and direction, this is where the physics simulation is going to
     go, eventually
   - match keyboard commands and call a function to update the ship
     direction
