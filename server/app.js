import router from './routes/router.js'
import express from "express"
import dotenv from "dotenv"
import ffmpeg from "fluent-ffmpeg"
import ffmpegPath from "ffmpeg-static"
import path from "path"


// make sure the .env is in the server folder
dotenv.config()

const videosPath = path.resolve(process.env.VIDEOS_PATH)

const app = express();

const PORT = process.env.PORT;

ffmpeg.setFfmpegPath(ffmpegPath);

app.use(express.json());
app.use("/videos", express.static(videosPath))

app.use("", router);

ffmpeg.setFfmpegPath(ffmpegPath);
export default app;