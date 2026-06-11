 # Robot Improvements

 ## Security (Server — high priority)

 - `server.js` resolves `process.env.VIDEOS_PATH` at startup but never boundary-checks the resolved `inputPath` against `videosPath` in the POST route. A crafted filename could still reach outside the folder on some OS/path edge cases. Add a strict `startsWith(videosPath)` check after resolution.
 - The thumbnail route at `server.js:50` does the same `path.join` without the boundary check.
 - `VIDEOS_PATH` and `OUTPUT_PATH` are not validated at startup — if they are missing or invalid, the server boots silently and crashes only on first request.
 - The `jobs` object grows forever in memory. A server restart clears all job history, and a long-running server accumulates entries with no eviction.

 ## Bugs

 ### Server

 - `fs.mkdir` for the output directory happens after `spawn` in `server.js:120`. If the directory does not exist, Java writes to a path that does not exist and exits non-zero. Move `mkdir` before `spawn`.
 - `javaProcess.unref()` is called after attaching `.on("close")` and `.on("error")` listeners. With `detached: true` and `stdio: "ignore"`, the `close` event will never fire after `unref()` because Node stops tracking the process. Choose one:
	 - Track the process (remove `unref`/`detached` and keep listeners).
	 - Or truly fire-and-forget (keep `unref` but track status via output file existence on disk).

 ### Java

 - `GroupManager.findLargestGroup` and `findClosestGroup` call `groups.get(0)` without checking if the list is empty first. If an empty list reaches either method, you get an `IndexOutOfBoundsException`.
 - `Grabber.close()` does not close the underlying FrameGrab channel — it is an empty method. This leaks the file handle for every video processed.
 - `CSVExport` uses a raw `PrintWriter` with no `flush()` before `close()`. If an exception occurs mid-write, partial CSV data may not be flushed to disk.

 ## Error Handling

 ### Server

 - The `/job/:jobId` route `catch` block can never actually be reached — the `try` body has no async operations and no throws. It gives a false sense of protection.
 - `console.error` in the thumbnail `catch` logs the full error object including stack traces. In production this can leak internal paths. Consider logging a sanitized message and keeping details server-side only.

 ### Java

 - `VideoSummaryApp` catches all exceptions with a top-level `catch (Exception e)` and calls `e.printStackTrace()`. This gives Java exit code `0` even on failure, so Node's `close` listener would incorrectly mark the job done. Use `System.exit(1)` on error so Node can detect failure.
 - `ImageSummaryApp` writes output files to the current working directory (`binarized.png`, `groups.csv`) with no configurable output path — this will write into whatever directory Node's server was started from.

 ## Tests

 ### Java — gaps in `CentroidTesting.java`

 - No test for `GroupManager.findLargestGroup` or `findClosestGroup` with an empty list (the known crash path).
 - No test for `Grabber` or `Salamander`.
 - No test for what happens when all pixels are `1` (entire image is one group).
 - No test for a single-row or single-column image.
 - `CSVExport` has zero tests — at minimum test that written rows have the correct format.

 ## Performance (Java)

 - `DfsBinaryGroupFinder` mutates the original image array (sets visited cells to `0`). This makes the method destructive — calling it twice on the same array gives different results. Consider using a separate `visited` boolean array instead.
 - `EuclideanColorDistance.distance` allocates two `int[]` arrays per pixel call. For a 1080p frame that is ~2 million array allocations per frame. Inline the RGB extraction with bit shifting instead.

 ## Refactoring / Code Quality

 ### Server

 - `allowedExtensions` in the `/api/videos` route is a local array defined inside the handler on every request. Move it to a module-level constant alongside `videosPath`.
 - The `jobs` object with no type structure makes it easy to accidentally write inconsistent shapes. Define a clear job creation helper function so all writes use the same shape.

 ### Java

 - `VideoSummaryApp` and `ImageSummaryApp` both duplicate the hex-color parsing and threshold-parsing logic. This could live in a shared utility method.
 - `Salamander` is essentially a one-field wrapper with a getter and setter — consider whether it carries enough logic to justify being its own class, or whether the tracking logic belongs in `VideoSummaryApp` directly.
 - `GroupManager` has only static methods with no state — it is a utility class. Consider making the constructor private to signal it is not meant to be instantiated.

 ## Documentation

 ### Java

 - `GroupManager`, `Salamander`, `Grabber`, and `CSVExport` have no Javadoc at all.
 - `VideoSummaryApp.main` has no usage comment at the class level (unlike `ImageSummaryApp` which is well documented).

 ### Server

 - No JSDoc on any route handler. At minimum document the expected env variables and their purpose near the top of `server.js`.

 ## Priority order to tackle

 1. **Bug:** move `mkdir` before `spawn`, fix `VideoSummaryApp` exit code on error.
 2. **Bug:** fix `Grabber.close()` and the `unref`/listener conflict.
 3. **Security:** add boundary check on resolved input paths.
 4. **Bug:** guard `groups.get(0)` calls in `GroupManager`.
 5. **Tests:** add `GroupManager` edge-case tests and `CSVExport` tests.
 6. **Performance:** inline RGB extraction in `EuclideanColorDistance`.
 7. **Refactor:** module-level constants in server, Javadoc on undocumented classes.

Refactor server.js

Split responsibilities: config loading, validation, routes, job management, and ffmpeg thumbnail logic.
Keep the main server file small and move utilities into separate modules.
Add tests

Backend: add tests for /api/videos, /thumbnail/:filename, /process/:filename, and /job/:jobId.
Validate error cases: missing query params, invalid file names, unsupported extensions, and job not found.
Java: add JUnit tests for the image binarization, group finding, distance calculation, and CSV export logic.
Test edge cases such as empty images, single-pixel groups, and color thresholds.
Improve Java project structure and reliability

Use clear separation between image binarization, group finding, and output generation.
Add more descriptive logging or exceptions around processing failures.
Review the Maven build config: the shade plugin is duplicated/commented, and the exec plugin may not be necessary for the jar packaging stage.