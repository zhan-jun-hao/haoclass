CREATE TABLE `course_comment_like` (
                                       `id` bigint unsigned NOT NULL COMMENT '评论点赞记录ID，雪花ID',
                                       `commentId` bigint unsigned NOT NULL COMMENT '被点赞评论ID',
                                       `userId` bigint unsigned NOT NULL COMMENT '点赞用户ID',
                                       `status` tinyint NOT NULL DEFAULT '1' COMMENT '点赞状态：0已取消 1已点赞',
                                       `createdUser` bigint unsigned NOT NULL DEFAULT '0' COMMENT '创建人ID',
                                       `updatedUser` bigint unsigned NOT NULL DEFAULT '0' COMMENT '更新人ID',
                                       `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除 1已删除',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_user_comment` (`userId`,`commentId`),
                                       KEY `idx_comment_status` (`commentId`,`status`),
                                       KEY `idx_user_status` (`userId`,`status`,`updateTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程评论点赞表';