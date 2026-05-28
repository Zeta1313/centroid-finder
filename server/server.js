import express from "express"
import dotenv from "dotenv"
import ffmpeg from "fluent-ffmpeg"
import fs from "fs/promises"
import path from "path"
import os from "os"
import { fileURLToPath } from "url"


const filename = fileURLToPath(import.meta.url)
const dirname = path.dirname(filename)

// make sure the .env is in the server folder
dotenv.config()

const videosPath = path.resolve(process.env.VIDEOS_PATH)

const app = express()

const PORT = process.env.PORT;

app.use(express.json());

app.use("/videos", express.static(videosPath))

app.get("/api/videos", async (req, res) => {
    try {
        const allowedExtensions = [".mp4", ".mov", ".webm"]
        const files = await fs.readdir(videosPath)

        const videoFiles = files.filter(file => {
            const extension = path.extname(file).toLowerCase()
            return allowedExtensions.includes(extension)
        })

        res.json(videoFiles)
    }
    catch {
        res.status(500).json({
            error: "Error reading video directory"
        })
    }
})

app.get("/api/thumbnail/:filename", async (req, res) => {
    try {
        const filename = req.params.filename

        const videoPath = path.join(videosPath, filename)

        await fs.access(videoPath)

        const thumbnailPath = path.join(
            os.tmpdir(),
            `${Date.now()}-${path.parse(filename).name}.png`
        )

        await new Promise((resolve, reject) => {
            ffmpeg(videoPath)
                .screenshots({
                    timestamps: ["0"],
                    filename: path.basename(thumbnailPath),
                    folder: path.dirname(thumbnailPath),
                    size: "320x180"
                })
                .on("end", resolve)
                .on("error", reject)
        })

        res.sendFile(thumbnailPath)
    }
    catch (error) {
        console.error(error)

        res.status(500).json({
            error: "Error generating thumbnail"
        })
    }
});

app.post("/process/:filename", async (req, res) => {
    try {
        const videosPath = process.env.VIDEOS_PATH

        if (!videosPath) {
            throw new Error("VIDEOS_PATH missing")
        }

        const filename = path.basename(req.params.filename)

        const targetColor = req.query.targetColor
        const threshold = req.query.threshold
        const parsedThreshold = Number.parseInt(threshold)
        const inputPath = path.join(videosPath, filename)
        await fs.access(inputPath)

        const jobId = crypto.randomUUID()

        const outputPath = path.join(
            process.env.OUTPUT_PATH || "./output",
            `${jobId}.csv`
        )

        jobs[jobId] = {
            status: "processing",
            result: null,
            error: null
        }

        const javaProcess = spawn("java", [
            "-jar",
            "target/videoprocessor.jar",
            inputPath,
            outputPath,
            targetColor,
            parsedThreshold.toString()
        ])

        javaProcess.on("close", code => {
            if (code === 0) {
                jobs[jobId] = {
                    status: "done",
                    result: `/result/${outputFilename}`,
                    error: null
                }
            }
            else {
                jobs[jobId] = {
                    status: "error",
                    result: null,
                    error: `Process exited with code ${code}`
                }
            }
        })

        javaProcess.on("error", error => {
            jobs[jobId] = {
                status: "error",
                result: null,
                error: error.message
            }
        })

        res.json({
            jobId
        })
    }
    catch (error) {
        console.error(error)

        res.status(500).json({
            error: "Error starting job"
        })
    }
})

app.get("/job/:jobId", (req, res) => {
    try {
        const job = jobs[req.params.jobId]

        if (!job) {
            return res.status(404).json({
                error: "Job ID not found"
            })
        }

        if (job.status === "processing") {
            return res.status(200).json({
                status: "processing"
            })
        }

        if (job.status === "done") {
            return res.status(200).json({
                status: "done",
                result: job.result
            })
        }

        if (job.status === "error") {
            return res.status(200).json({
                status: "error",
                error: job.error
            })
        }
    }
    catch (error) {
        console.error(error)

        res.status(500).json({
            error: "Error fetching job status"
        })
    }
})
// cd into server folder
// node server.js or npm run dev (make sure to install nodemon)
//http://localhost:3000/api/videos
//http://localhost:3000/api/thumbnail/video.mp4
//http://localhost:3000/videos/video.mp4.
app.listen(PORT, () => {
    console.log(`Server started on http://localhost:${PORT}`);
});
