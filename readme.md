# Next up

1. detect when the fleet is at the right edge of the screen
   - [x] find column position of the extrememots invader
   - [x] multiply by column width + offset
   - [x] xtreme-invader can use `max` or `min` to find first or last

2. move left as well as right
   - [x] one additional frame for down-right

3. read the keyboard events to move the ship
   - keyboard events are published on the channel already
   - [ ] store extra state in the fleet to represent the ship's position
     and direction
   - [ ] arrows apply acceleration, inertia is real
   - [ ] match keyboard commands and call a function to update the ship
     direction

4. bullets
   - [ ] firing a bullet accelerates the ship down
   - [ ] explosion animation states
   - [ ] lore is that invaders are capturing stars, so explosion
         releases a star, which maybe stays in place?

5. bugs
   - [ ] curved part at the top of the invader leaves behind streaks,
         erasing needs to not curve that part.
