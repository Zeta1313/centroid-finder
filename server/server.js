import express from "express"
import dotenv from "dotenv"

dotenv.config()

const app = express()

application.use(express.json());

app.get("/api/videos", async (req, res) => {
    try {
        const videosPath = process.env.VIDEOS_PATH

        if (!videosPath) {throw new Error("Error reading video directory")}

        const absolutePath = path.resolve(videosPath)

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

app.get("/thumbnail/{filename}", async (req, res) => {

});

app.post("/process/{filename}", async (req, res) => {

});

app.get("/process/{jobId}/status", async (req, res) => {

});

app.listen(3000, () => {
    console.log("Server running on port 3000")
});