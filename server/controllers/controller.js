import fs from 'fs/promises'
import path from 'path'
import dotenv from "dotenv"

dotenv.config()
const videosPath = path.resolve(process.env.VIDEOS_PATH)

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