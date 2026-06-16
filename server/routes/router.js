import express from "express"
import * as controller from '../controllers/controller.js';

const router = express.Router();

router.get("/api/videos", controller.getAllVideos);
router.get("/thumbnail/:filename", controller.getThumbnail);
router.post("/process/:filename", controller.processFile);
router.get("/job/:jobId", controller.getJobStatus);
router.get("/job-logs", controller.getJobLogs);

export default router;
