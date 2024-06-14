package com.example.koncalendar.models
import com.google.firebase.Timestamp

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
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var color: String? = null,
    var createdAt: Timestamp = Timestamp.now()
)