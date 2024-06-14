package com.example.koncalendar.models

import com.google.firebase.Timestamp

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
    var id: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var title: String = "",
    var categoryId: String = "",
    var userId: String = "",
    var location: String? = null,
    var description: String? = null,
    var frequency: String = "",
    var createdAt: Timestamp = Timestamp.now()
)