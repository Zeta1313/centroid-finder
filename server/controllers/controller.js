import express from "express"
import dotenv from "dotenv"
import ffmpeg from "fluent-ffmpeg"
import ffmpegPath from "ffmpeg-static"
import fs from "fs/promises"
import path from "path"
import os from "os"
import { fileURLToPath } from "url"
import { spawn } from "child_process"
import crypto from 'crypto'

dotenv.config()
const videosPath = path.resolve(process.env.VIDEOS_PATH)
const jobLogPath = path.join(path.resolve(process.env.LOG_PATH), "job-logs.json")
const jobs = {}
// tracks which videos currently have a running java process: filename -> jobId
const activeVideos = new Map()

const filename = fileURLToPath(import.meta.url)
const dirname = path.dirname(filename)

export const getAllVideos = async (req, res) => {
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
};

export const getThumbnail = async (req, res) => {
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
}
export const processFile = async (req, res) => {
    try {
        const filename = req.params.filename;
        const targetColor = req.query.targetColor;
        const threshold = req.query.threshold;

        if (!targetColor || !threshold) {
            return res.status(400).json({
                error: "Missing targetColor or threshold query parameter."
            });
        }

        const safeFilename = path.basename(req.params.filename);
        const inputPath = path.join(videosPath, safeFilename);

        // console.log(safeFilename) // ex: print video.mp4
        // console.log(inputPath)    // ex: ...centroid-finder/videos/video.mp4

        // only allow one running java process per video
        if (activeVideos.has(safeFilename)) {
            return res.status(409).json({
                error: "This video is already being processed.",
                jobId: activeVideos.get(safeFilename)
            });
        }

        const jobId = crypto.randomUUID();
        //updates or adds the jobID and its statuses report, this is for the /job/:jobID
        jobs[jobId] = { status: "processing", result: null, error: null };
        const logs = await readJobLogs()
        logs.push({
            jobId,
            filename: safeFilename,
            targetColor,
            threshold,
            submittedAt: new Date().toISOString(),
            status: "processing",
            result: null,
            error: null
        })
        await writeJobLogs(logs)

        const outputPath = path.join(
            path.resolve(process.env.OUTPUT_PATH),
            `${jobId}.csv`
        );

        await fs.mkdir(path.resolve(process.env.OUTPUT_PATH), { recursive: true });

        activeVideos.set(safeFilename, jobId);

        const javaProcess = spawn("java", [
            "-jar", path.resolve(dirname, process.env.JAR_PATH),
            inputPath, outputPath, targetColor, threshold
        ], { detached: true, stdio: "ignore" });

        javaProcess.on("close", async (code) => {
            activeVideos.delete(safeFilename);
            const update = code === 0
                ? { status: "done", result: `/results/${jobId}.csv`, error: null }
                : { status: "error", result: null, error: `Process exited with code ${code}` };
            jobs[jobId] = update
            const logs = await readJobLogs()
            const log = logs.find(l => l.jobId === jobId)
            if (log) {
                Object.assign(log, update, {
                    completedAt: new Date().toISOString()
                })
                await writeJobLogs(logs)
            }
        });

        javaProcess.on("error", async (err) => {
            activeVideos.delete(safeFilename)
            jobs[jobId] = {status: "error", result: null, error: err.message}
            const logs = await readJobLogs()
            const log = logs.find(l => l.jobId === jobId)
            if (log) {
                log.status = "error"
                log.error = err.message
                log.completedAt = new Date().toISOString()
                await writeJobLogs(logs)
            }
        })  

        javaProcess.unref();
        
        res.status(202).json({
            "jobId": jobId
        })
    } catch (error) {
        console.error(error)

        res.status(500).json({
            error: "Error starting job"
        })
    }
}

export const getJobStatus = (req, res) => {
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
}

const readJobLogs = async () => {
    try {
        const data = await fs.readFile(jobLogPath, "utf8")
        return JSON.parse(data)
    }
    catch {
        return []
    }
}

const writeJobLogs = async (logs) => {
    await fs.mkdir(path.dirname(jobLogPath), { recursive: true })
    await fs.writeFile(jobLogPath, JSON.stringify(logs, null, 2))
}

export const getJobLogs = async (req, res) => {
    try {
        const logs = await readJobLogs()
        res.json(logs)
    }
    catch {
        res.status(500).json({
            error: "Error reading job logs"
        })
    }
}