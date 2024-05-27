package com.example.koncalendar.models

/*
CREATE TABLE category_sharing (
    user_id VARCHAR(255),
    target_user_id VARCHAR(255),
    target_category_id VARCHAR(255),
    PRIMARY KEY (user_id, target_user_id, target_category_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (target_user_id) REFERENCES user(user_id),
    FOREIGN KEY (target_category_id) REFERENCES calendar_category(id)
);
*/

data class CategorySharing(
    val userId: String,
    val targetUserId: String,
    val targetCategoryId: String
)