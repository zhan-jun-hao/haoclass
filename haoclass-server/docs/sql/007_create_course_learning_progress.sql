CREATE TABLE `course_learning_progress` (
                                            `id` bigint unsigned NOT NULL COMMENT '课程学习进度ID，雪花ID',
                                            `userId` bigint unsigned NOT NULL COMMENT '用户ID',
                                            `courseId` bigint unsigned NOT NULL COMMENT '课程ID',
                                            `episodeId` bigint unsigned NOT NULL COMMENT '章节ID',
                                            `progressSeconds` int unsigned NOT NULL DEFAULT '0' COMMENT '最后播放进度，单位：秒',
                                            `finished` tinyint NOT NULL DEFAULT '0' COMMENT '是否学完：0未学完 1已学完',
                                            `finishTime` datetime DEFAULT NULL COMMENT '完成时间',
                                            `lastLearnTime` datetime DEFAULT NULL COMMENT '最后学习时间',
                                            `createdUser` bigint unsigned NOT NULL DEFAULT '0' COMMENT '创建人ID',
                                            `updatedUser` bigint unsigned NOT NULL DEFAULT '0' COMMENT '更新人ID',
                                            `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除 1已删除',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `uk_user_course_episode` (`userId`,`courseId`,`episodeId`),
                                            KEY `idx_user_course` (`userId`,`courseId`,`deleted`),
                                            KEY `idx_user_last_learn` (`userId`,`deleted`,`lastLearnTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户课程学习进度表';