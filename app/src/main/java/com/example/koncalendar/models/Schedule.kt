package com.example.koncalendar.models

/*
CREATE TABLE schedule (
    id INT PRIMARY KEY AUTO_INCREMENT,
    start_time TIME,
    end_time TIME,
    start_date DATE,
    end_date DATE,
    title VARCHAR(255) NOT NULL,
    category_id INT,
    user_id INT,
    location VARCHAR(255),
    description TEXT,
    frequency ENUM('non', 'daily', 'weekly', 'monthly', 'yearly'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES calendar_category(id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);
 */

data class Schedule(
    val startTime: String,
    val endTime: String,
    val startDate: String,
    val endDate: String,
    val title: String,
    val categoryId: String,
    val userId: String,
    val location: String? = null,
    val description: String? = null,
    val frequency: String,
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)