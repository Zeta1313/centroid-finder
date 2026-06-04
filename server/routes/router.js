import express from "express"
import fs from 'fs/promises'
import path from 'path'
import dotenv from "dotenv"


dotenv.config()
const videosPath = path.resolve(process.env.VIDEOS_PATH)

const router = express.Router();

router.get("/api/videos", async (req, res) => {
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
});

export default router;

/*
import express from "express"
import fs from 'fs/promises'
import path from 'path'

export default function createRouter(videosPath) {
    const router = express.Router()

    router.get('/videos', async (req, res) => {
        try {
            const allowedExtensions = ['.mp4', '.mov', '.webm']
            const files = await fs.readdir(videosPath)

            const videoFiles = files.filter(file => {
                const extension = path.extname(file).toLowerCase()
                return allowedExtensions.includes(extension)
            })

            res.json(videoFiles)
        } catch (err) {
            res.status(500).json({ error: 'Error reading video directory' })
        }
    })

    return router
}
*/