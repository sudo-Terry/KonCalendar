package com.example.koncalendar.models

/*
CREATE TABLE calendar_category (
    id VARCHAR(255),
    user_id VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);
*/

data class CalendarCategory(
    val id: String,
    val userId: String,
    val title: String,
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)