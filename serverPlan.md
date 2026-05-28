# SERVER PLAN

Summary: all the code that must satisfy the api below is written in javascript (index.js)

## List Available Videos

**GET** `/api/videos`

**Description:**  
Return a list of all video files in the mounted directory, available publicly at /videos/VIDEO_NAME.

**Plan:** since we are putting all the videos inside "videos" folder, we need to somehow read each file in "videos" folder, put each video in a list, and send it.

## Generate Thumbnail

**GET** `/thumbnail/{filename}`

**Path Parameters:**

- `filename` (string, required) — Name of the video file (e.g. `demo.mov`)

**Description:**  
Extract and return the first frame from the video as a JPEG.

**Plan:** for each video in "videos" folder, we are going to read each name and compare it to the path parameter that is given to us, if the filename matches return the first frame from the video as a JPEG for the thumbnail.


## Start Video Processing Job

**POST** `/process/{filename}`  
_Query parameters:_ `?targetColor=<hex>&threshold=<int>`

**Path Parameters:**

- `filename` (string, required) — Name of the video file to process (e.g. `intro.mp4`)

**Query Parameters:**

- `targetColor` (string, required) — Hex color code to match (e.g. `ff0000`)
- `threshold`   (number, required) — Match threshold (e.g. `120`)

**Description:**  
Kick off an asynchronous job to analyze the video. Returns a `jobId` you can poll.

**Plan:** at the path /process/{filename}, we are sending "targetColor" and "threshold" to the server, and we get jobID in return. ill be honest, i have no idea what jobID is, im assuming its the video file after its been binarized.

## Get Processing Job Status

**GET** `/process/{jobId}/status`

**Path Parameters:**

- `jobId` (string, required) — ID returned by the **POST** `/process` call

**Description:**  
Check whether the job is still running, has completed, or failed.

**Plan:** when user is acessing /process/{jobId}/status, if {jobID} exist, set status path with object to status:"processing", if processing is done, then update to status:"done" along with another property that contains the video path. and etc.