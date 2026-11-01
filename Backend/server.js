
const express = require("express");
const cors = require("cors");
require("dotenv").config();

const pool = require("./db");

const app = express();
const PORT = process.env.PORT || 3000;
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
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
            `SELECT
                id, course, title, location, time, members,
                created_at,
                ST_Y(coordinates::geometry) AS latitude,
                ST_X(coordinates::geometry) AS longitude
             FROM study_groups
             ORDER BY created_at DESC, id DESC`
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
        const {
            course,
            title,
            location,
            time,
            latitude,
            longitude
        } = req.body;

        if (![course, title, location, time].every(
            value => typeof value === "string" && value.trim().length > 0
        )) {
            return res.status(400).json({
                error: "All fields are required"
            });
        }

        const lat = Number(latitude);
        const lng = Number(longitude);

        if (
            latitude == null ||
            longitude == null ||
            !Number.isFinite(lat) ||
            !Number.isFinite(lng) ||
            lat < -90 || lat > 90 ||
            lng < -180 || lng > 180
        ) {
            return res.status(400).json({
                error: "Valid coordinates are required"
            });
        }

        const result = await pool.query(
            `INSERT INTO study_groups
            (course, title, location, time, members, coordinates)
            VALUES (
                $1, $2, $3, $4, 1,
                ST_SetSRID(
                    ST_MakePoint($6, $5), 4326
                )::geography
            )
            RETURNING
                id, course, title, location, time, members,
                ST_Y(coordinates::geometry) AS latitude,
                ST_X(coordinates::geometry) AS longitude`,
            [
                course.trim(),
                title.trim(),
                location.trim(),
                time.trim(),
                lat,
                lng
            ]
        );

        res.status(201).json(result.rows[0]);

    } catch (error) {
        console.error("Create group error:", error);
        res.status(500).json({
            error: "Failed to create study group"
        });
    }
});


// Register a new user
app.post("/auth/signup", async (req, res) => {
    try {
        const { firstName, lastName, email, password } = req.body;

        // Validate required fields
        if (![firstName, lastName, email, password].every(
            value => typeof value === "string" && value.trim().length > 0
        )) {
            return res.status(400).json({
                error: "All fields are required"
            });
        }

        const normalizedEmail = email.trim().toLowerCase();

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(normalizedEmail)) {
            return res.status(400).json({
                error: "Invalid email address"
            });
        }

        if (password.length < 8) {
            return res.status(400).json({
                error: "Password must be at least 8 characters"
            });
        }

        // Check whether email already exists
        const existingUser = await pool.query(
            "SELECT id FROM users WHERE email = $1",
            [normalizedEmail]
        );

        if (existingUser.rows.length > 0) {
            return res.status(409).json({
                error: "An account with this email already exists"
            });
        }

        // Hash password
        const passwordHash = await bcrypt.hash(password, 12);

        // Insert user into PostgreSQL
        const result = await pool.query(
            `INSERT INTO users
             (first_name, last_name, email, password_hash)
             VALUES ($1, $2, $3, $4)
             RETURNING id, first_name, last_name, email, created_at`,
            [
                firstName.trim(),
                lastName.trim(),
                normalizedEmail,
                passwordHash
            ]
        );

        res.status(201).json({
            message: "Account created successfully",
            user: result.rows[0]
        });

    } catch (error) {
        console.error("Signup error:", error);

        res.status(500).json({
            error: "Failed to create account"
        });
    }
});

// Authenticate an existing user
app.post("/auth/login", async (req, res) => {
    try {
        const { email, password } = req.body;

        if (
            typeof email !== "string" ||
            typeof password !== "string" ||
            !email.trim() ||
            !password
        ) {
            return res.status(400).json({
                error: "Email and password are required"
            });
        }

        // Find user
        const result = await pool.query(
            "SELECT * FROM users WHERE email = $1",
            [email.trim().toLowerCase()]
        );

        if (result.rows.length === 0) {
            return res.status(401).json({
                error: "Invalid email or password"
            });
        }

        const user = result.rows[0];

        // Verify password
        const passwordMatches = await bcrypt.compare(
            password,
            user.password_hash
        );

        if (!passwordMatches) {
            return res.status(401).json({
                error: "Invalid email or password"
            });
        }

        // Generate authentication token
        const token = jwt.sign(
            {
                userId: user.id,
                email: user.email
            },
            process.env.JWT_SECRET,
            {
                expiresIn: "7d"
            }
        );

        res.json({
            message: "Login successful",
            token,
            user: {
                id: user.id,
                first_name: user.first_name,
                last_name: user.last_name,
                email: user.email
            }
        });

    } catch (error) {
        console.error("Login error:", error);

        res.status(500).json({
            error: "Login failed"
        });
    }
});


app.get("/groups/nearby", async (req, res) => {
    try {
        const { latitude, longitude } = req.query;

        const lat = Number(latitude);
        const lng = Number(longitude);

        if (
            latitude == null ||
            longitude == null ||
            !Number.isFinite(lat) ||
            !Number.isFinite(lng) ||
            lat < -90 || lat > 90 ||
            lng < -180 || lng > 180
        ) {
            return res.status(400).json({
                error: "Valid coordinates are required"
            });
        }

        const result = await pool.query(
            `SELECT
                id, course, title, location, time, members,
                ST_Y(coordinates::geometry) AS latitude,
                ST_X(coordinates::geometry) AS longitude,
                ROUND(
                    ST_Distance(
                        coordinates,
                        ST_SetSRID(
                            ST_MakePoint($2, $1), 4326
                        )::geography
                    )::numeric
                ) AS distance_meters
             FROM study_groups
             WHERE ST_DWithin(
                 coordinates,
                 ST_SetSRID(
                     ST_MakePoint($2, $1), 4326
                 )::geography,
                 5000
             )
             ORDER BY distance_meters ASC`,
            [lat, lng]
        );

        res.json(result.rows);

    } catch (error) {
        console.error("Nearby groups error:", error);
        res.status(500).json({
            error: "Failed to retrieve nearby groups"
        });
    }
});

app.listen(PORT, () => {
    console.log(`StudySync backend running on port ${PORT}`);
});
