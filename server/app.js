import router from './routes/router.js'
import express from "express"
import dotenv from "dotenv"
import ffmpeg from "fluent-ffmpeg"
import ffmpegPath from "ffmpeg-static"
import fs from "fs/promises"
import path from "path"
import os from "os"
import { fileURLToPath } from "url"
import { spawn } from "child_process"

const filename = fileURLToPath(import.meta.url)
const dirname = path.dirname(filename)

// make sure the .env is in the server folder
dotenv.config()

const videosPath = path.resolve(process.env.VIDEOS_PATH)
// console.log(videosPath)

const app = express();

const PORT = process.env.PORT;

const jobs = {}

ffmpeg.setFfmpegPath(ffmpegPath);

app.use(express.json());
app.use("/videos", express.static(videosPath))

app.use("", router);

ffmpeg.setFfmpegPath(ffmpegPath);
export default app;