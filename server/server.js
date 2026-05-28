import express from "express"
import dotenv from "dotenv"
import ffmpeg from "fluent-ffmpeg"
import fs from "fs/promises"
import path from "path"
import os from "os"

dotenv.config()

const app = express()

application.use(express.json());

app.use("/videos", express.static(process.env.VIDEOS_PATH))

app.get("/api/videos", async (req, res) => {
    try {
        const allowedExtensions = [".mp4", ".mov", ".webm"]
        const videosPath = process.env.VIDEOS_PATH

        if (!videosPath) {throw new Error("Error reading video directory")}

        const files = await fs.readdir(absolutePath)

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
        const videosPath = process.env.VIDEOS_PATH

        if (!videosPath) {
            throw new Error("Error generating thumbnail")
        }

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
            status: "running",
            filename,
            outputPath
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
                jobs[jobId].status = "complete"
            }
            else {
                jobs[jobId].status = "failed"
            }
        })

        javaProcess.on("error", () => {
            jobs[jobId].status = "failed"
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

app.get("/process/{jobId}/status", async (req, res) => {

});

app.listen(3000, () => {
    console.log("Server running on port 3000")
});

