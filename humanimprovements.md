1. Expand on Salamander and GroupManagar classes, I feel like more could be outsourced into them to make the main code slimmer.
2. Add tests for later parts of the project, we didn't test that angle that much
3. Job calling in server seems like it lacks information, possible nested if loops to detect common errors?

# Refactoring ideas
## Sam:
- commentation/documentation for each API in the server/server.js
- moving each API to its own component file or MVC Layer?
- the last api route (job/:jobs) works, but takes a long time to test it with our 8-9 min salamander video, maybe add a 10 second video.