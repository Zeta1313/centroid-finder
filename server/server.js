import dotenv from "dotenv"
import app from './app.js'

dotenv.config();
const PORT = process.env.PORT;

// app.get("/api/videos", async (req, res) => {
//     try {
//         const allowedExtensions = [".mp4", ".mov", ".webm"]
//         const files = await fs.readdir(videosPath)

//         const videoFiles = files.filter(file => {
//             const extension = path.extname(file).toLowerCase()
//             return allowedExtensions.includes(extension)
//         })

//         res.json(videoFiles)
//     }
//     catch {
//         res.status(500).json({
//             error: "Error reading video directory"
//         })
//     }
// })

// app.get("/thumbnail/:filename", async (req, res) => {
//     try {
//         const filename = req.params.filename

//         const videoPath = path.join(videosPath, filename)

//         await fs.access(videoPath)

//         const thumbnailPath = path.join(
//             os.tmpdir(),
//             `${Date.now()}-${path.parse(filename).name}.png`
//         )

//         await new Promise((resolve, reject) => {
//             ffmpeg(videoPath)
//                 .screenshots({
//                     timestamps: ["0"],
//                     filename: path.basename(thumbnailPath),
//                     folder: path.dirname(thumbnailPath),
//                     size: "320x180"
//                 })
//                 .on("end", resolve)
//                 .on("error", reject)
//         })

//         res.sendFile(thumbnailPath)
//     }
//     catch (error) {
//         console.error(error)

//         res.status(500).json({
//             error: "Error generating thumbnail"
//         })
//     }
// });
// curl -X POST "http://localhost:3000/process/d?targetColor=FFA200&threshold=164"
// curl -X POST "http://localhost:3000/process/video.mp4?targetColor=FFA200&threshold=164"
// app.post("/process/:filename", controller.processFile);
// app.post("/process/:filename", async (req, res) => {
//     try {
//         const filename = req.params.filename;
//         const targetColor = req.query.targetColor;
//         const threshold = req.query.threshold;

//         if (!targetColor || !threshold) {
//             return res.status(400).json({
//                 error: "Missing targetColor or threshold query parameter."
//             });
//         }

//         const safeFilename = path.basename(req.params.filename);
//         const inputPath = path.join(videosPath, safeFilename);

//         // console.log(safeFilename) // ex: print video.mp4
//         // console.log(inputPath)    // ex: ...centroid-finder/videos/video.mp4

//         const jobId = crypto.randomUUID();
//         //updates or adds the jobID and its statuses report, this is for the /job/:jobID
//         jobs[jobId] = { status: "processing", result: null, error: null };

//         const outputPath = path.join(
//             path.resolve(process.env.OUTPUT_PATH),
//             `${jobId}.csv`
//         );

//         const javaProcess = spawn("java", [
//             "-jar", path.resolve(dirname, process.env.JAR_PATH),
//             inputPath, outputPath, targetColor, threshold
//         ], { detached: true, stdio: "ignore" });

//         javaProcess.on("close", (code) => {
//             jobs[jobId] = code === 0
//                 ? { status: "done", result: `/results/${jobId}.csv`, error: null }
//                 : { status: "error", result: null, error: `Process exited with code ${code}` };
//         });

//         javaProcess.on("error", (err) => {
//             jobs[jobId] = { status: "error", result: null, error: err.message };
//         });

//         javaProcess.unref();

//         await fs.mkdir(path.resolve(process.env.OUTPUT_PATH), { recursive: true });
        
//         res.status(202).json({
//             "jobId": jobId
//         })
//     } catch (error) {
//         console.error(error)

//         res.status(500).json({
//             error: "Error starting job"
//         })
//     }
// })

// http://localhost:3000/job/:jobs
// http://localhost:3000/job/77601cbf-6449-46b4-8f51-3ab8bdbc1ea0
// app.get("/job/:jobId", (req, res) => {
//     try {
//         const job = jobs[req.params.jobId]

//         if (!job) {
//             return res.status(404).json({
//                 error: "Job ID not found"
//             })
//         }

//         if (job.status === "processing") {
//             return res.status(200).json({
//                 status: "processing"
//             })
//         }

//         if (job.status === "done") {
//             return res.status(200).json({
//                 status: "done",
//                 result: job.result
//             })
//         }

//         if (job.status === "error") {
//             return res.status(200).json({
//                 status: "error",
//                 error: job.error
//             })
//         }
//     }
//     catch (error) {
//         console.error(error)

//         res.status(500).json({
//             error: "Error fetching job status"
//         })
//     }
// })
// cd into server folder
// node server.js or npm run dev (make sure to install nodemon)
//http://localhost:3000/api/videos
//http://localhost:3000/thumbnail/video.mp4
//http://localhost:3000/videos/video.mp4
//http://localhost:3000/process/video.mp4?targetColor=FFA200&threshold=164
//http://localhost:3000/process/d?targetColor=FFA200&threshold=164
//http://localhost:3000/job-logs

//your env ex: 
/*
VIDEOS_PATH=../videos
OUTPUT_PATH=../output
PORT=3000
JAR_PATH=../processor/target/videoprocessor.jar
*/
app.listen(PORT, () => {
    console.log(`Server started on http://localhost:${PORT}`);
});
