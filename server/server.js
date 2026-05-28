import express from "express"

application.use(express.json());

app.get("/api/videos", async (req, res) => {

});

app.get("/thumbnail/{filename}", async (req, res) => {

});

app.post("/process/{filename}", async (req, res) => {

});

app.get("/process/{jobId}/status", async (req, res) => {
    
})