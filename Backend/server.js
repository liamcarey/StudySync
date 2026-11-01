
const express = require("express");
const cors = require("cors");
require("dotenv").config();

const pool = require("./db");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Test endpoint
app.get("/", (req, res) => {
    res.json({
        message: "StudySync API is running!"
    });
});

// Retrieve all study groups
app.get("/groups", async (req, res) => {
    try {
        const result = await pool.query(
            "SELECT * FROM study_groups ORDER BY created_at DESC, id DESC"
        );

        res.json(result.rows);
    } catch (error) {
        console.error(error);
        res.status(500).json({
            error: "Failed to retrieve study groups"
        });
    }
});

// Create a new study group
app.post("/groups", async (req, res) => {
    try {
        const { course, title, location, time } = req.body;

        if (
            ![course, title, location, time].every(
                value => typeof value === "string" &&
                         value.trim().length > 0
            )
        ) {
            return res.status(400).json({
                error: "All fields are required"
            });
        }

        const result = await pool.query(
            `INSERT INTO study_groups
             (course, title, location, time, members)
             VALUES ($1, $2, $3, $4, 1)
             RETURNING *`,
            [
                course.trim(),
                title.trim(),
                location.trim(),
                time.trim()
            ]
        );

        res.status(201).json(result.rows[0]);
    } catch (error) {
        console.error(error);
        res.status(500).json({
            error: "Failed to create study group"
        });
    }
});

app.listen(PORT, () => {
    console.log(`StudySync backend running on port ${PORT}`);
});
