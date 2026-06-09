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